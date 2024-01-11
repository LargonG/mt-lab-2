package parser

import common.BufferedIterator
import lexer.*
import lexer.Number
import parser.tree.Branch
import parser.tree.Leaf
import parser.tree.Node

class ArithmeticsParser: Parser<ArithmeticsToken, Node<ArithmeticsToken>> {
    companion object {
        private sealed interface Rule
        private data object Expr : Rule // E
        private data object Mul: Rule // T
        private data object ExprContinuous: Rule // R
        private data object MulContinuous: Rule // Y
        private data object Atom: Rule // F
        private data object ExprNext: Rule // K
    }

    private fun createErrorMessage(token: ArithmeticsToken) =
        "unexpected token: $token"

    override fun parse(tokens: BufferedIterator<ArithmeticsToken>): Node<ArithmeticsToken> =
        ruleE(tokens)

    private fun ruleE(it: BufferedIterator<ArithmeticsToken>): Node<ArithmeticsToken> {
        /*
        E -> TR
         */
        val node = Branch<Rule, ArithmeticsToken>(Expr, mutableListOf())
        return when (val token = it.look()) {
            Minus, Open, is Name, is Number -> {
                node.addChild(ruleT(it))
                node.addChild(ruleR(it))
                node
            }
            else -> throw AssertionError(createErrorMessage(token))
        }
    }

    private fun ruleT(it: BufferedIterator<ArithmeticsToken>): Node<ArithmeticsToken> {
        /*
        T -> FY
         */
        val node = Branch<Rule, ArithmeticsToken>(Mul, mutableListOf())
        return when (val token = it.look()) {
            Minus, Open, is Number, is Name -> {
                node.addChild(ruleF(it))
                node.addChild(ruleY(it))
                node
            }
            else -> throw AssertionError(createErrorMessage(token))
        }
    }

    private fun ruleR(it: BufferedIterator<ArithmeticsToken>): Node<ArithmeticsToken> {
        /*
        R -> -TR
        R -> +TR
        R ->
         */
        val node = Branch<Rule, ArithmeticsToken>(ExprContinuous, mutableListOf())
        return when (val token = it.look()) {
            Plus, Minus -> {
                node.addChild(Leaf(token))
                it.next()
                node.addChild(ruleT(it))
                node.addChild(ruleR(it))
                node
            }
            Close, EndLine, NextArgument -> {
                node
            }
            else -> throw AssertionError(createErrorMessage(token))
        }
    }

    private fun ruleY(it: BufferedIterator<ArithmeticsToken>): Node<ArithmeticsToken> {
        /*
        Y -> *FY
        Y ->
         */
        val node = Branch<Rule, ArithmeticsToken>(MulContinuous, mutableListOf())
        return when (val token = it.look()) {
            Multiply -> {
                node.addChild(Leaf(token))
                it.next()

                node.addChild(ruleF(it))
                node.addChild(ruleY(it))

                node
            }
            Plus, Minus, EndLine, Close, NextArgument -> {
                node
            }
            else -> throw AssertionError(createErrorMessage(token))
        }
    }

    private fun ruleF(it: BufferedIterator<ArithmeticsToken>): Node<ArithmeticsToken> {
        /*
        F -> -F
        F -> n
        F -> (E)
        F -> f(EK)
         */
        val node = Branch<Rule, ArithmeticsToken>(Atom, mutableListOf())
        return when (val token = it.look()) {
            Minus -> {
                node.addChild(Leaf(token))
                it.next()
                node.addChild(ruleF(it))
                node
            }
            is Number -> {
                node.addChild(Leaf(token))
                it.next()
                node
            }
            is Name -> {
                node.addChild(Leaf(token))
                it.next()

                val openBracket = it.next()
                assert(openBracket == Open)
                node.addChild(Leaf(openBracket))

                node.addChild(ruleE(it))
                node.addChild(ruleK(it))

                val closeBracket = it.next()
                assert(closeBracket == Close)
                node.addChild(Leaf(closeBracket))

                node
            }
            Open -> {
                node.addChild(Leaf(token))
                it.next()

                node.addChild(ruleE(it))

                val closeBracket = it.next()
                assert(closeBracket == Close)
                node.addChild(Leaf(closeBracket))

                node
            }
            else -> throw AssertionError(createErrorMessage(token))
        }
    }

    private fun ruleK(it: BufferedIterator<ArithmeticsToken>): Node<ArithmeticsToken> {
        /*
        K -> ,EK
        K ->
         */
        val node = Branch<Rule, ArithmeticsToken>(ExprNext, mutableListOf())
        return when(val token = it.look()) {
            NextArgument -> {
                node.addChild(Leaf(token))
                it.next()

                node.addChild(ruleE(it))
                node.addChild(ruleK(it))

                node
            }
            Close -> {
                node
            }
            else -> throw AssertionError(createErrorMessage(token))
        }

    }
}