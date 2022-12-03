package alg

import graphs.SimpleGraph

object GraphProperty {

    fun <V> isTree(g: SimpleGraph<V>): Boolean =
        (g.m == g.n - 1) && Connectivity.isConnected(g) // fail fast: checking edge count runs in constant time

    fun <V> isComplete(g: SimpleGraph<V>): Boolean = g.m == g.n * (g.n - 1) / 2

    fun <V> isAcyclic(g: SimpleGraph<V>): Boolean =
        g.m == g.n - Connectivity.numberOfConnectedComponents(g)

    fun <V> isKRegular(g: SimpleGraph<V>, k: Int): Boolean {
        if (g.n == 0) throw IllegalArgumentException("Regularity for empty graph doesn't really make sense.")
        if (k < 0) throw IllegalArgumentException("k-regularity is not defined for negative values of k.")

        return g.V.all { g.degreeOf(it) == k }
    }

    fun <V> isCubic(g: SimpleGraph<V>): Boolean = isKRegular(g, 3)

    fun <V> isBipartite(g: SimpleGraph<V>): Boolean {
        val components: MutableCollection<MutableSet<V>> = Connectivity.listAllConnectedComponents(g).values

        for (component in components) {
            val layerIter = Traversal.breadthFirstSearchLayerIterator(g, component.first())

            val sides = HashMap<V, Int>()
            var toggle = 1

            for (layer in layerIter) {
                for (v in layer) {
                    sides[v] = toggle
                    if (g.neighbors(v).any { sides.getOrDefault(it, 0) == toggle }) {
                        return false
                    }
                }
                toggle = -toggle
            }
        }
        return true
    }

    /**
     * Runtime: O([g].maxDegree)
     *
     * @return The h-Index of [g], i.e. the biggest natural number *h* so that there are at
     * least *h* vertices with degree of at least *h*
     */
    fun <V> hIndex(g: SimpleGraph<V>): Int {
        if (g.n == 0) throw IllegalArgumentException()

        val arr = IntArray(g.maxDegree() + 2) // Indices are: 0, 1, ..., maxDegree, maxDegree+1
        for (v in g.V) arr[g.degreeOf(v)] += 1

        for (i in g.maxDegree() downTo 0) {
            arr[i] += arr[i + 1] // arr[i] contains number of vertices with degree at least i
            if (arr[i] >= i) return i
        }

        throw RuntimeException("Shouldn't reach this part as the h-index is always at least 0 per definition")
    }
}