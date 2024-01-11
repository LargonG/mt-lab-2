package lexer

import common.BufferedIterator
import common.DefaultIterator

class RulesLexer: Lexer<Char, RuleToken> {
    override fun tokenize(input: BufferedIterator<Char>): BufferedIterator<RuleToken> =
        DefaultIterator(getAllTokens(input).iterator())

    private fun oneSymbol(it: BufferedIterator<Char>): RuleToken {
        return when(val symbol = skipSpace(it)) {
            'n' -> DigitToken
            'f' -> WordToken
            '\n' -> NewLine
            Char(0) -> NewLine
            else -> {
                if (symbol.isUpperCase()) {
                    NonTerm(symbol.toString())
                } else {
                    Term(symbol.toString())
                }
            }
        }
    }

    private fun arrow(it: BufferedIterator<Char>): RuleToken {
        val minus = skipSpace(it)
        val more = it.next()
        assert(minus == '-' && more == '>')
        return ArrowToken
    }

    private fun skipSpace(it: BufferedIterator<Char>): Char {
        if (!it.hasNext()) {
            return Char(0)
        }

        var symbol = it.next()
        while (symbol == ' ' && it.hasNext()) {
            symbol = it.next()
        }
        return if (symbol == ' ') Char(0) else symbol
    }

    override fun getToken(input: BufferedIterator<Char>): RuleToken = oneSymbol(input)

    override fun getAllTokens(input: BufferedIterator<Char>): List<RuleToken> {
        val result = mutableListOf<RuleToken>()
        while (input.hasNext()) {
            val from = oneSymbol(input)
            if (from == NewLine) {
                continue
            }
            result.add(from)
            result.add(arrow(input))
            var next = oneSymbol(input)
            while (next != NewLine && input.hasNext()) {
                result.add(next)
                if (input.hasNext()) {
                    next = oneSymbol(input)
                }
            }
            result.add(next)
        }

        return result
    }

}

sealed interface RuleToken

data object DigitToken: RuleToken
data object WordToken: RuleToken
data object ArrowToken: RuleToken
data object NewLine: RuleToken
data class Term(val value: String): RuleToken
data class NonTerm(val name: String): RuleToken