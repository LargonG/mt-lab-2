package lexer

import common.BufferedIterator
import common.DefaultIterator
import java.math.BigInteger

class ArithmeticsLexer: Lexer<Char, ArithmeticsToken> {
    override fun tokenize(input: BufferedIterator<Char>): BufferedIterator<ArithmeticsToken> {
        val result: MutableList<ArithmeticsToken> = mutableListOf()
        while (input.hasNext()) {
            result.add(when (val symbol = skipSpace(input)) {
                '+' -> {
                    input.next()
                    Plus
                }
                '-' -> {
                    input.next()
                    Minus
                }
                '*' -> {
                    input.next()
                    Multiply
                }
                '(' -> {
                    input.next()
                    Open
                }
                ')' -> {
                    input.next()
                    Close
                }
                0.toChar() -> {
                    continue
                }
                else -> {
                    if (symbol.isDigit()) {
                        number(input)
                    } else if (symbol.isLetter()) {
                        function(input)
                    } else {
                        throw IllegalStateException()
                    }
                }
            })
        }
        result.add(EndLine)

        return DefaultIterator(result.iterator())
    }

    private fun number(it: BufferedIterator<Char>): ArithmeticsToken {
        val res = StringBuilder()
        while (it.hasNext() && it.look().isDigit()) {
            res.append(it.next())
        }
        return Number(BigInteger(res.toString()))
    }

    private fun function(it: BufferedIterator<Char>): ArithmeticsToken {
        val res = StringBuilder()
        while (it.hasNext() && it.look().isLetter()) {
            res.append(it.next())
        }
        return Name(res.toString())
    }

    private fun skipSpace(it: BufferedIterator<Char>): Char {
        var res = it.look()
        while (res.isWhitespace() && it.hasNext()) {
            it.next()
            if (it.hasNext()) {
                res = it.look()
            } else {
                res = 0.toChar()
            }
        }
        return res
    }
}

sealed interface ArithmeticsToken

data object Plus: ArithmeticsToken
data object Minus: ArithmeticsToken
data object Multiply: ArithmeticsToken
data class Number(val value: BigInteger): ArithmeticsToken {
    override fun toString(): String = value.toString()
}
data class Name(val value: String): ArithmeticsToken {
    override fun toString(): String = value
}
data object Open: ArithmeticsToken
data object Close: ArithmeticsToken
data object EndLine: ArithmeticsToken
