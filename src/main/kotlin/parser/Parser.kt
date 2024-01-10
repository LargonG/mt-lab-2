package parser

import common.BufferedIterator

interface Parser<in A, out B> {
    fun parse(tokens: BufferedIterator<A>): B
}