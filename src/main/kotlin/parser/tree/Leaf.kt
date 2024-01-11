package parser.tree

import common.IdGen
import parser.tree.Node.Companion.tab

data class Leaf<T>(val value: T): Node<T> {
    var id: Int = -1

    override fun toFormatString(builder: StringBuilder, tabs: Int) {
        builder.append(tab.repeat(tabs)).append(value)
    }

    override fun toDot(builder: StringBuilder, idGen: IdGen) {
        builder.append(name(idGen)).append("[label=$value]\n")
    }

    override fun name(idGen: IdGen): String {
        if (id == -1) {
            id = idGen.next()
        }
        return "$value$id"
    }
}
