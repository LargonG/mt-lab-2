package lexer

import common.BufferedIterator

interface Lexer<in A, out B> {
    fun tokenize(input: BufferedIterator<A>): BufferedIterator<B>
}