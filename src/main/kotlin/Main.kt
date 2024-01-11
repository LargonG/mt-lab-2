import common.DefaultIterator
import common.SimpleIdGen
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import lexer.ArithmeticsLexer
import lexer.ArithmeticsToken
import lexer.RulesLexer
import parser.ArithmeticsParser
import parser.RulesParser
import parser.tree.Node
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
            F -> f(EK)
            K -> ,EK
            K -> 
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
        f(4 + 5 * 6 - 83459, 5 * 10 * sin(245 + 5))
    """.trimIndent()

    val arithmeticsLexer = ArithmeticsLexer()
    val arithmeticTokens = arithmeticsLexer.tokenize(DefaultIterator(arithmetics.iterator()))

    val arithmeticsParser = ArithmeticsParser()
    val res = arithmeticsParser.parse(arithmeticTokens)

    Util.saveToFile("out", "out", res, "// $arithmetics\n")
}

object Util {
    fun saveToFile(
        dir: String, name: String,
        node: Node<ArithmeticsToken>,
        addition: String = ""
    ) {
        val path = Path("$dir/$name.txt")
        Files.writeString(path, addition + node.toDot(SimpleIdGen()))
        Graphviz
            .fromFile(path.toFile())
            .render(Format.SVG)
            .toFile(Path("$dir/$name.svg").toFile())
    }
}