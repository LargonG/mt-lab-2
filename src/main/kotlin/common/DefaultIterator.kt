package common

class DefaultIterator<T>(val iterator: Iterator<T>): BufferedIterator<T> {
    var actual: T? = null

    override fun next(): T {
        return if (actual == null) {
            iterator.next()
        } else {
            val value = actual
            actual = null
            value ?: throw NoSuchElementException()
        }
    }

    override fun hasNext(): Boolean {
        if (actual == null) {
            return iterator.hasNext()
        }
        return true
    }

    override fun look(): T {
        if (actual == null) {
            actual = iterator.next()
        }
        return actual ?: throw NoSuchElementException()
    }
}