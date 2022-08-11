import Traversal.breadthFirstSearchLayerIterator

object Neighbors {

    /**
     * Returns a [MutableSet] of all vertices with a distance of at most [k] to [v].
     *
     * @return The closed [k]-neighborhood of vertex [v].
     */
    fun <V> closedNeighbors(g: SimpleGraph<V>, v: V, k: Int): MutableSet<V> {

        val res = HashSet<V>()
        val iter = breadthFirstSearchLayerIterator(g, v)
        repeat(k + 1) { res.addAll(iter.next()) }
        return res
    }

    /**
     * Returns a [MutableSet] of all vertices with a distance of at most [k] to [v].
     *
     * @return The closed [k]-neighborhood of vertex [v].
     */
    fun <V> openNeighbors(g: SimpleGraph<V>, v: V, k: Int): MutableSet<V> {

        val iter = breadthFirstSearchLayerIterator(g, v)
        repeat(k) { iter.next() }
        return iter.next()
    }
}