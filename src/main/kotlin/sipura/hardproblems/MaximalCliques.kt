package sipura.hardproblems

import sipura.graphs.SimpleGraph
import sipura.utils.SetTheory

object MaximalCliques {

    /**
     * Uses the Bron-Kerbosch-Algorithm
     */
    private fun <V> listMaximalCliques(
        g: SimpleGraph<V>,
        R: MutableSet<V>,
        P: MutableSet<V>,
        X: MutableSet<V>
    ): Set<Set<V>> {
        if (P.isEmpty() && X.isEmpty()) {
            return setOf(R)
        }

        val res = HashSet<Set<V>>()
        for (v in P.toSet()) {
            val newR = HashSet(R).apply { add(v) }
            val newP = SetTheory.intersection(P, g.neighbors(v))
            val newX = SetTheory.intersection(X, g.neighbors(v))
            res.addAll(listMaximalCliques(g, newR, newP, newX))

            P.remove(v)
            X.add(v)
        }
        return res
    }

    fun <V> listMaximalCliques(g: SimpleGraph<V>) = listMaximalCliques(
        g,
        R = mutableSetOf(),
        X = mutableSetOf(),
        P = g.V.toMutableSet()
    )
}
