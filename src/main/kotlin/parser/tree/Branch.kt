package parser.tree

import common.IdGen
import parser.tree.Node.Companion.tab

data class Branch<K, T>(
    val key: K,
    val children: MutableList<Node<T>>
): Node<T> {
    var id: Int = -1

    fun addChild(value: Node<T>): Boolean {
        return children.add(value)
    }

    override fun toFormatString(builder: StringBuilder, tabs: Int) {
        builder
            .append(tab.repeat(tabs)).append("$key(\n")
        for (i in children.indices) {
            val child = children[i]
            child.toFormatString(builder, tabs + 1)
            if (i + 1 < children.size) {
                builder.append(",")
            }
            builder.append("\n")
        }
        builder
            .append(tab.repeat(tabs)).append(")")
    }

    override fun toDot(builder: StringBuilder, idGen: IdGen) {
        builder.append(name(idGen)).append("[label=$key]\n")
        for (child in children) {
            builder.append(name(idGen)).append(" -> ").append(child.name(idGen)).append("\n")
        }
        for (child in children) {
            child.toDot(builder, idGen)
        }
    }

    override fun name(idGen: IdGen): String {
        if (id == -1) {
            id = idGen.next()
        }
        return "$key$id"
    }
}
