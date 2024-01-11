import Util.saveToFile
import common.DefaultIterator
import grammar.*
import grammar.Function
import lexer.ArithmeticsLexer
import parser.ArithmeticsParser
import java.math.BigInteger
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.test.Test

class Tests {
    private val lexer = ArithmeticsLexer()
    private val parser = ArithmeticsParser()

    private val tests = listOf(
        "(1+2)*sin(-3*(7-4)+2)", // пример
        "42", // число
        "109 + 10", // сумма
        "6-7876", // разность
        "56", // другое число
        "42 * 9", // произведение
        "5 * 2 + 10", // произведение с суммой
        "-105234", // отрицательное число
        "--1543", // двойной унарный минус
        "-------5674323", // больше унарных минусов
        "---7 - 565585646453 --- 74575674 ---- 36574432", // унарные минусы с вычитанием
        "-2 + 4 * 5 + 3", // унарный минус в начале + выражение
        "---4", // ещё унарные минусы
        "sin(1)", // функция
        "f(300)", // ещё функция
        "lambda(sin(2))", // функция от функции
        "sin(cos(1))", // функция от функции
        "sin(1)*sin(2)+cos(1)*cos(2)", // выражение с функциями
        "(1+2)*sin(-3*(7-4)+2)+50-10*5", // усложнённый пример
        "(((5 * 3) + 2 * (5 + -3)) + 599 * (100 + 43245) - 154 + (1200 - 200)) - (500 + 10000)", // случайное сложное выражение
        "   -  (100 + 2)    ", // пробелы
        "                  -          (                  100 + 5 - (55  --     2313    +     5345      *           43    )    *   5)         ", // больше пробелов
        "1 - 2 - 3 - 4 - 5", // проверка на право ассоциативность минуса
        "calc(502, 6345, sin(5,2353, 6456))", // функция с множеством аргументов от функции
        "f(100, 400, 52)", // функция с множеством аргументов
        "2353  + 546 * iytrewr(1 * 5346 + 56456, 2 + -2453 * 457)", // функция с множеством аргументов с выражениями
        "hello(my(dear(4324565, 45, 6546, 124345 ), world(349, 354)), welcome(356), to(456, 23), the(chat(5467)))", // функция от функции от функции от функции ....
        "hello(my(dear(4324565,         sin(2345),         6546        , 124345 ), world(      349, 354      )), welcome(          356           ), to(             456                   ,        23           ), the(chat(    5467    , 5646854   )))", // пробелы, табы в функциях от нескольких переменных
        "zero", // нуль арная функция
        "variable + x + y - z * t", // переменные
        "someOtherFunction(hello + 5, y - x, 1 * 2 + 6 - 7)", // функция от нескольких переменных
        "zero + one(5) + two(3, sin(x))" // много функций
    )

    private val iterations = 100

    private val charPool = ('a'..'z') + ('A'..'Z')

    @Test
    fun cases() {
        for (i in tests.indices) {
            val test = tests[i]
            val res = parser.parse(lexer.tokenize(DefaultIterator(test.iterator())))

            saveToFile("out/cases", i.toString(), res)
        }
    }

    @Test
    fun random() {
        for (i in 1..iterations) {
            val expr = generateRandomArithmetics(5, Random)
            val res = parser.parse(
                lexer.tokenize(
                    DefaultIterator(expr.toSuffixString().iterator())))
            saveToFile("out/random", "random$i", res, "// ${expr.toSuffixString()}\n")
        }
    }

    private fun generateRandomArithmetics(depth: Int, random: Random): Arithmetics =
        when (val key = random.nextInt().absoluteValue % (if (depth == 0) 1 else 6)) {
            0 -> Const(BigInteger((random.nextInt().absoluteValue % 100).toString()))
            1 -> UnaryOperation(Negate, generateRandomArithmetics(depth - 1, random))
            2, 3, 4 -> {
                val op: BinaryOperator = when (key) {
                    2 -> Add
                    3 -> Sub
                    4 -> Mul
                    else -> throw IllegalStateException()
                }
                BinaryOperation(
                    op,
                    generateRandomArithmetics(depth - 1, random),
                    generateRandomArithmetics(depth - 1, random)
                )
            }
            5 -> Function(randomString(3, random),
                generateRandomList(
                    depth - 1,
                    random.nextInt().absoluteValue % 6,
                    random))
            else -> throw IllegalStateException("value = $key")
        }

    private fun randomString(len: Int, random: Random): String = (1..len)
        .map { random.nextInt(0, charPool.size).let { charPool[it] } }
        .joinToString("")

    private fun generateRandomList(depth: Int, len: Int, random: Random): List<Arithmetics> {
        val result = mutableListOf<Arithmetics>()
        for (i in 1..len) {
            result.add(generateRandomArithmetics(depth, random))
        }
        return result
    }
}