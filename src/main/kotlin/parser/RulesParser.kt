package parser

import Atom
import RegexTerm
import Rule
import State
import StringTerm
import ZeroTerm
import common.BufferedIterator
import lexer.*

class RulesParser: Parser<RuleToken, List<Rule>> {
    companion object {
        private val digitRegex = Regex("([1-9][0-9]*|0)")
        private val functionRegex = Regex("[a-zA-Z]+")
        private val coma = Regex(",")
    }

    override fun parse(tokens: BufferedIterator<RuleToken>): List<Rule> {
        val rules = mutableListOf<Rule>()
        while (tokens.hasNext()) {
            val from = State((tokens.next() as NonTerm).name)

            assert(tokens.next() == ArrowToken)

            val atoms = mutableListOf<Atom>()

            var atom = tokens.next()
            while (atom != NewLine) {
                atoms.add(when(atom) {
                    WordToken -> RegexTerm(functionRegex)
                    Coma -> RegexTerm(coma)
                    is NonTerm -> State(atom.name)
                    DigitToken -> RegexTerm(digitRegex)
                    is Term -> StringTerm(atom.value)
                    else -> throw IllegalStateException("Not expected this token: $atom")
                })
                if (!tokens.hasNext()) {
                    break
                }
                atom = tokens.next()
            }

            if (atoms.isEmpty()) {
                atoms.add(ZeroTerm)
            }

            rules.add(Rule(from, atoms))
        }

        return rules
    }
}