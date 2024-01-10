package common

class SimpleIdGen: IdGen {
    var it = 0

    override fun next(): Int = it++
}