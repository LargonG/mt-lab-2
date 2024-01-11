interface Atom

interface Term: Atom
interface NonTerm: Atom

data class RegexTerm(val regex: Regex): Term {
    override fun toString(): String = regex.toString()
}
data class StringTerm(val value: String): Term {
    override fun toString(): String = value
}
data object ZeroTerm: Term {
    override fun toString(): String = "ε"
}
data object Dollar: Term {
    override fun toString(): String = "$"
}

data class State(val name: String): NonTerm {
    override fun toString(): String = name
}

data class Rule(val from: NonTerm, val atoms: List<Atom>) {
    override fun toString(): String = "$from -> $atoms"

}

fun first(rules: List<Rule>): Map<NonTerm, Set<Term>> {
    var change = true

    val fi: MutableMap<NonTerm, MutableSet<Term>> = HashMap()
    while (change) {
        change = false
        for (rule in rules) {
            val nfi = first(rule.atoms, fi)

            change = change || fi.addIfNotContains(rule.from, nfi)
        }
    }

    return fi
}

fun first(atoms: List<Atom>, fi: Map<NonTerm, Set<Term>>): Set<Term> {
    val result: MutableSet<Term> = mutableSetOf()
    for (atom in atoms) {
        val nfi: Set<Term> =
        if (atom is Term)
            mutableSetOf(atom)
        else
            fi[atom].orEmpty()

        result.remove(ZeroTerm)
        result.addAll(nfi)

        if (!nfi.contains(ZeroTerm)) {
            break
        }
    }

    if (atoms.isEmpty()) {
        result.add(ZeroTerm)
    }

    return result
}

fun follow(rules: List<Rule>, fi: Map<NonTerm, Set<Term>>): Map<NonTerm, Set<Term>> {
    var change = true

    val fo: MutableMap<NonTerm, MutableSet<Term>> = mutableMapOf()
    fo[rules[0].from] = mutableSetOf(Dollar)
    while (change) {
        change = false
        for (rule in rules) {
            for (i in rule.atoms.indices) {
                val atom = rule.atoms[i]
                if (atom is NonTerm) {
                    val nfo: Set<Term> = first(rule.atoms.subList(i + 1, rule.atoms.size), fi)
                    val edited = nfo - ZeroTerm

                    change = change || fo.addIfNotContains(atom, edited)

                    change = change ||
                            (nfo.contains(ZeroTerm) &&
                                    fo.addIfNotContains(atom, fo.getOrDefault(rule.from, setOf())))
                }
            }
        }
    }

    return fo
}

private fun <K, V> MutableMap<K, MutableSet<V>>.addIfNotContains(key: K, value: Set<V>): Boolean {
    return getOrPut(key) { mutableSetOf() }.addAll(value)
}

fun isLL1Grammar(rules: List<Rule>): Boolean {
    val fi = first(rules)
    val fo = follow(rules, fi)

    println("\nfirst: ")
    for (i in fi) {
        println("\t$i")
    }

    println("\nfollow: ")
    for (i in fo) {
        println("\t$i")
    }

    println()

    val grouped = rules.groupBy(Rule::from)
    for (group in grouped) {
        for (a in group.value) {
            for (b in group.value) {
                if (a == b) {
                    continue
                }

                val aFirst = first(a.atoms, fi)
                val bFirst = first(b.atoms, fi)

                if ((aFirst.any { bFirst.contains(it) })) {
                    println("Error 1:")
                    println("$a $aFirst")
                    println("$b $bFirst")
                    return false
                }

                if (aFirst.contains(ZeroTerm) &&
                    bFirst.any {fo[a.from]!!.contains(it)}) {
                    println("Error 2:")
                    println(aFirst)
                    println(bFirst)
                    println(fo[a.from])
                    return false
                }
            }
        }
    }

    return true
}