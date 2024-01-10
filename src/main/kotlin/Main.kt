import common.SimpleIdGen
import common.DefaultIterator
import lexer.ArithmeticsLexer
import lexer.RulesLexer
import parser.ArithmeticsParser
import parser.RulesParser
import java.nio.file.Files
import kotlin.io.path.Path

fun main() {
    val input =
        """
            E -> TR
            R -> +TR
            R -> -TR
            R ->
            T -> FY
            Y -> *FY
            Y ->
            F -> -F
            F -> n
            F -> (E)
            F -> f(E)
    """.trimMargin()

    val lexer = RulesLexer()
    val tokens = lexer.tokenize(DefaultIterator(input.iterator()))

    val parser = RulesParser()
    val rules = parser.parse(tokens)

    for (rule in rules) {
        println(rule)
    }

    println(if (isLL1Grammar(rules)) "G is LL1" else "G is not LL1")

    val arithmetics = """
        (1+2)*sin(-3*(7-4)+2)+50-10*5
    """.trimIndent()

    val arithmeticsLexer = ArithmeticsLexer()
    val arithmeticTokens = arithmeticsLexer.tokenize(DefaultIterator(arithmetics.iterator()))

    val arithmeticsParser = ArithmeticsParser()
    val res = arithmeticsParser.parse(arithmeticTokens)

    val path = Path("out.txt")
    Files.writeString(path, res.toDot(SimpleIdGen()))
}