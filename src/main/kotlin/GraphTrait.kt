object GraphTrait {

    fun <V> isTree(g: SimpleGraph<V>): Boolean =
        (g.m == g.n - 1) && Connectivity.isConnected(g) // fail fast: checking edge count runs in constant time


    fun <V> isAcyclic(g: SimpleGraph<V>): Boolean =
        g.m == g.n - Connectivity.numberOfConnectedComponents(g)

    fun <V> iskRegular(g: SimpleGraph<V>, k : Int): Boolean {
        if (g.n == 0) throw IllegalArgumentException("Regularity for empty graph doesn't really make sense.")
        if (k < 0) throw IllegalArgumentException("k-regularity is not defined for negative values of k.")

        return g.V.all { g.degreeOf(it) == k }
    }

    fun <V> isCubic(g: SimpleGraph<V>): Boolean = iskRegular(g, 3)
}