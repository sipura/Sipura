package alg

import alg.Traversal.breadthFirstSearchLayerIterator
import graphs.SimpleGraph

object Neighbors {

    /**
     * Returns a [MutableSet] of all vertices with a distance of at most [k] to [v].
     *
     * @throws IllegalArgumentException if [v] is not a vertex in [g]
     *
     * @return The closed [k]-neighborhood of vertex [v].
     */
    fun <V> closedNeighborsDistK(g: SimpleGraph<V>, v: V, k: Int): MutableSet<V> {
        val res = HashSet<V>()
        val iter = breadthFirstSearchLayerIterator(g, v)
        for (i in 1..(k + 1)) {
            if (iter.hasNext()) {
                res.addAll(iter.next())
            } else {
                break
            }
        }
        return res
    }

    /**
     * Returns a [MutableSet] of all vertices with a distance of at most 1 to [v]
     *
     * @throws IllegalArgumentException if [v] is not a vertex in [g]
     *
     * @return The closed neighborhood of vertex [v].
     */
    fun <V> closedNeighbors(g: SimpleGraph<V>, v: V): MutableSet<V> =
        g.neighbors(v).toMutableSet().apply { add(v) }

    /**
     * Returns a [MutableSet] of all vertices with a distance of exactly [k] to [v].
     *
     * @throws IllegalArgumentException if [v] is not a vertex in [g]
     *
     * @return The closed [k]-neighborhood of vertex [v].
     */
    fun <V> openNeighborsDistK(g: SimpleGraph<V>, v: V, k: Int): MutableSet<V> {
        val iter = breadthFirstSearchLayerIterator(g, v)
        for (i in 1..k) {
            if (iter.hasNext()) {
                iter.next()
            } else {
                break
            }
        }
        return if (iter.hasNext()) iter.next() else mutableSetOf()
    }
}
