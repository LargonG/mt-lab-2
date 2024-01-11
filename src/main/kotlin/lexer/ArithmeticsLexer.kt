package lexer

import common.BufferedIterator
import java.math.BigInteger

class ArithmeticsLexer: Lexer<Char, ArithmeticsToken> {
    companion object {
        private class LexerIterator(
            val lexer: ArithmeticsLexer,
            val input: BufferedIterator<Char>
            ): BufferedIterator<ArithmeticsToken> {

            var token: ArithmeticsToken? = null
            var endLine: Boolean = false

            override fun next(): ArithmeticsToken {
                return if (token === null) {
                    if (lexer.hasNextSymbol(input)) {
                        lexer.getToken(input)
                    } else if (!endLine) {
                        endLine = true
                        EndLine
                    } else {
                        throw NoSuchElementException()
                    }
                } else {
                    val result = token
                    token = null
                    result ?: throw NoSuchElementException()
                }
            }

            override fun hasNext(): Boolean {
                return if (token === null) {
                    if (lexer.hasNextSymbol(input)) {
                        true
                    } else {
                        !endLine
                    }
                } else {
                    true
                }
            }

            override fun look(): ArithmeticsToken {
                if (token === null) {
                    if (lexer.hasNextSymbol(input)) {
                        token = lexer.getToken(input)
                    } else if (!endLine) {
                        endLine = true
                        token = EndLine
                    }
                }
                return token ?: throw NoSuchElementException()
            }

        }
    }

    override fun tokenize(input: BufferedIterator<Char>): BufferedIterator<ArithmeticsToken> =
        LexerIterator(this, input)

    override fun getToken(input: BufferedIterator<Char>): ArithmeticsToken {
        if (!hasNextSymbol(input)) {
            throw NoSuchElementException()
        }
        return when (val symbol = skipSpace(input)) {
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
            else -> {
                if (symbol.isDigit()) {
                    number(input)
                } else if (symbol.isLetter()) {
                    function(input)
                } else {
                    throw IllegalStateException()
                }
            }
        }
    }

    override fun getAllTokens(input: BufferedIterator<Char>): List<ArithmeticsToken> {
        val result = mutableListOf<ArithmeticsToken>()
        while (hasNextSymbol(input)) {
            result.add(getToken(input))
        }
        result.add(EndLine)
        return result
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

    private fun hasNextSymbol(it: BufferedIterator<Char>): Boolean {
        if (!it.hasNext()) {
            return false
        }

        var symbol = it.look()
        while (symbol.isWhitespace()) {
            it.next()
            if (it.hasNext()) {
                symbol = it.look()
            } else {
                break
            }
        }
        return !symbol.isWhitespace()
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
