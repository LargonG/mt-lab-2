package common

interface BufferedIterator<out T> {
    fun next(): T
    fun hasNext(): Boolean
    fun look(): T
}