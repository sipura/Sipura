object Connectivity {

    /**
     * Does a BFS starting from [v1]. If [v2] gets hit, the BFS gets cancelled and immediately *true* is returned.
     * Once the BFS finishes exploring the whole connected component without finding [v2], *false* is returned.
     */
    fun <V> checkIfConnected(g: SimpleGraph<V>, v1: V, v2: V): Boolean {
        if (v1 !in g.V) throw IllegalArgumentException("Graph does not contain vertex v1")
        if (v2 !in g.V) throw IllegalArgumentException("Graph does not contain vertex v2")

        val iter = Traversal.breadthFirstSearchIterator(g, v1) // doesn't matter which one
        iter.forEach { if (it == v2) return true }

        return false // traversal finished without hit
    }

    fun <V> getConnectedComponent(g: SimpleGraph<V>, v: V): MutableSet<V> {
        if (v !in g.V) throw IllegalArgumentException("Graph does not contain vertex v")

        return Traversal.breadthFirstSearchIterator(g, v).asSequence().toMutableSet()
    }

    fun <V> listAllConnectedComponents(g: SimpleGraph<V>): MutableMap<V, MutableSet<V>> {
        val result = HashMap<V, MutableSet<V>>()

        for (v in g.V) {
            if (v !in result) {
                val component = getConnectedComponent(g, v)
                component.forEach { result[it] = component }
            }
        }

        return result
    }

    fun <V> isConnected(g: SimpleGraph<V>): Boolean {
        if (g.n == 0) throw IllegalArgumentException("Connectivity for empty graph is ambiguous, so exception for good measure")
        return getConnectedComponent(g, g.V.first()).size == g.n
    }

    fun <V> isTree(g: SimpleGraph<V>): Boolean =
        (g.m == g.n - 1) && isConnected(g) // fail fast: checking edge count runs in constant time

}