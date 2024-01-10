package grammar

import java.math.BigInteger

sealed interface Arithmetics {
    fun toSuffixString(): String {
        val builder = StringBuilder()
        toSuffixString(builder, 0)
        return builder.toString()
    }

    fun toSuffixString(builder: StringBuilder, before: Int)
}

sealed interface Operator {
    val priority: Int
}
sealed interface BinaryOperator: Operator
sealed interface UnaryOperator: Operator

data object Add: BinaryOperator {
    override val priority: Int = 1

    override fun toString(): String = "+"
}
data object Sub: BinaryOperator {
    override val priority: Int = 1

    override fun toString(): String = "-"
}
data object Mul: BinaryOperator {
    override val priority: Int = 5

    override fun toString(): String = "*"
}

data object Negate: UnaryOperator {
    override val priority: Int = 9

    override fun toString(): String = "-"
}

data class BinaryOperation(
    val op: BinaryOperator,
    val left: Arithmetics,
    val right: Arithmetics): Arithmetics {

    override fun toSuffixString(builder: StringBuilder, before: Int) {
        if (before > op.priority) {
            builder.append("(")
        }

        left.toSuffixString(builder, op.priority)
        builder.append(op)
        right.toSuffixString(builder, op.priority)

        if (before > op.priority) {
            builder.append(")")
        }
    }
}

data class UnaryOperation(
    val op: UnaryOperator,
    val expr: Arithmetics): Arithmetics {
    override fun toSuffixString(builder: StringBuilder, before: Int) {
        if (before > op.priority) {
            builder.append("(")
        }

        builder.append(op)
        expr.toSuffixString(builder, op.priority)

        if (before > op.priority) {
            builder.append(")")
        }
    }
}

data class Const(val value: BigInteger): Arithmetics {
    override fun toSuffixString(builder: StringBuilder, before: Int) {
        builder.append(value)
    }
}
data class Function(val name: String, val expr: Arithmetics): Arithmetics {
    override fun toSuffixString(builder: StringBuilder, before: Int) {
        builder.append(name).append("(")
        expr.toSuffixString(builder, 0)
        builder.append(")")
    }
}