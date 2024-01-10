package parser.tree

import common.IdGen

interface Node<T> {
    companion object {
        @JvmStatic
        val tab = "  "
    }

    fun toFormatString(builder: StringBuilder, tabs: Int)
    fun toFormatString(): String {
        val builder = StringBuilder()
        toFormatString(builder, 0)
        return builder.toString()
    }

    fun toDot(idGen: IdGen): String {
        val builder = StringBuilder()
        builder.append("digraph G {\n")
        toDot(builder, idGen)
        builder.append("}")
        return builder.toString()
    }

    fun toDot(builder: StringBuilder, idGen: IdGen)

    fun name(idGen: IdGen): String
}