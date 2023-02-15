package sipura.graphs

/**
 * A class representing a simple undirected graph.
 */
open class SimpleGraph<V> {

    // If v1 and v2 are connected, then *v1 in map[v2]* and *v2 in map[v1]* has to hold
    private val map = HashMap<V, MutableSet<V>>()

    /**
     * The number of vertices in this graph.
     *
     * Runtime: O(1)  ->  constant
     */
    val n: Int
        get() = map.size

    /**
     * The number of edges in this graph.
     *
     * Runtime: O(1)  ->  constant
     */
    var m: Int = 0
        private set

    /**
     * The set of all vertices in this graph.
     *
     * Runtime: O(1)  ->  constant
     *
     * Warning: Do not try to remove any elements from this set. This set is part of the internal data structure of
     * this class. Removing elements would lead to undefined behaviour.
     */
    val V: Set<V>
        get() = map.keys

    private fun assertVertexExists(v: V, vertexName: String) {
        if (v !in map) throw IllegalArgumentException("Graph does not contain vertex $vertexName.")
    }

    /**
     * Checks if the given vertex [v] is part of this graph.
     *
     * Runtime: O(1)  ->  constant
     * @return True if [v] is in this graph. False otherwise.
     */
    operator fun contains(v: V): Boolean = v in map

    /**
     * Adds the given vertex [v] to this graph. If [v] is already in this graph, nothing happens.
     *
     * Runtime: O(1)  ->  constant
     * @return True if [v] is successfully added to this graph. False if [v] is already in this graph.
     */
    open fun addVertex(v: V): Boolean {
        return if (v !in map) {
            map[v] = mutableSetOf()
            true
        } else {
            false
        }
    }

    /**
     * Removes the given vertex [v] from this graph. If [v] is not in this graph, nothing happens.
     *
     * Runtime: O(degree of [v])  ->  each neighbour needs to be updated that [v] is not in its neighbourhood anymore
     * @return True if [v] is successfully removed from this graph. False if [v] is not in this graph.
     */
    open fun removeVertex(v: V): Boolean {
        if (v !in map) return false
        for (nb in map[v]!!) {
            map[nb]!!.remove(v)
        }
        m -= map[v]!!.size
        map.remove(v)
        return true
    }

    /**
     * Returns the amount of neighbors of the given vertex [v], i.e. the number of other vertices that are connected
     * to [v] via an edge.
     *
     * Runtime: O(1)  ->  constant
     * @throws IllegalArgumentException if [v] is not in this graph.
     * @return the amount of neighbors of [v].
     */
    open fun degreeOf(v: V): Int {
        assertVertexExists(v, "v")
        return map[v]!!.size
    }

    /**
     * Adds an edge between the given vertices [v1] and [v2]. If the edge already exists, nothing happens.
     *
     * Runtime: O(1)  ->  constant
     * @throws IllegalArgumentException if [v1] or [v2] are the same vertex, or if either of them is not in the graph.
     * @return True if the edge is successfully added to the graph. False if the edge already exists.
     */
    open fun addEdge(v1: V, v2: V): Boolean {
        assertVertexExists(v1, "v1")
        assertVertexExists(v2, "v2")
        if (v1 == v2) throw IllegalArgumentException("no loops are allowed")

        if (v1 in map[v2]!!) return false

        map[v1]!!.add(v2)
        map[v2]!!.add(v1)
        m++
        return true
    }

    /**
     * Removes the edge between the two given vertices [v1] and [v2]. If the edge does not exist, nothing happens.
     *
     * Runtime: O(1)  ->  constant
     * @throws IllegalArgumentException if [v1] or [v2] are the same vertex, or if either of them is not in the graph.
     * @return True if the edge is successfully removed. False if the edge does not exist.
     */
    open fun removeEdge(v1: V, v2: V): Boolean {
        assertVertexExists(v1, "v1")
        assertVertexExists(v2, "v2")
        if (v1 == v2) throw IllegalArgumentException("no loops are allowed")

        if (v1 !in map[v2]!!) return false

        map[v1]!!.remove(v2)
        map[v2]!!.remove(v1)
        m--
        return true
    }

    /**
     * Checks if [v1] and [v2] are connected via an edge.
     *
     * Runtime: O(1)  ->  constant
     * @throws IllegalArgumentException if [v1] or [v2] are the same vertex, or if either of them is not in the graph.
     * @return True if [v1] and [v2] are connected, false otherwise.
     */
    open fun hasEdge(v1: V, v2: V): Boolean {
        if (v1 == v2) throw IllegalArgumentException("no loops are allowed. this is likely a false input")
        assertVertexExists(v1, "v1")
        assertVertexExists(v2, "v2")

        return v1 in map[v2]!!
    }

    /**
     * Executes the given function [f] for every pair of vertices that is connected via an edge.
     *
     * Runtime: O( [m] * [f] )  ->  calls [f] for each edge
     */
    open fun forEachEdge(f: (V, V) -> Unit) {
        val visited = mutableSetOf<V>()
        for (v1 in map.keys) {
            visited.add(v1)
            for (v2 in map[v1]!!) {
                if (v2 in visited) continue
                f(v1, v2)
            }
        }
    }

    /**
     * Returns the set of all neighbors of the given vertex [v], i.e. the set of all other vertices that are connected
     * to [v] via an edge.
     *
     * Runtime: O(1)  ->  constant
     *
     * Warning: Do not try to remove any elements from this set. This set is part of the internal data structure of
     * this class. Removing elements would lead to undefined behaviour.
     * @throws IllegalArgumentException if [v] is not in this graph.
     * @return The set of neighbors of [v].
     */
    open fun neighbors(v: V): Set<V> {
        assertVertexExists(v, "v")
        return map[v]!!
    }

    /**
     * Creates a shallow copy of this graph. Copies all vertices and edges but uses the same instance of each vertex
     * as this graph.
     * @return the copy of this graph.
     */
    open fun copy(): SimpleGraph<V> {
        val copy = SimpleGraph<V>()

        for (v in V) copy.addVertex(v)
        forEachEdge { v1, v2 -> copy.addEdge(v1, v2) }

        return copy
    }

    /**
     * Calculates the largest degree of any vertex in this graph.
     *
     * Runtime: O([n])  ->  for every vertex, the degree gets checked in constant time
     * @throws NoSuchElementException if the graph does not contain any vertices.
     * @return the largest degree of any vertex in this graph.
     */
    open fun maxDegree(): Int = V.maxOf { degreeOf(it) }

    /**
     * Iterates over every directed edge in this graph, i.e. for every two vertices v1 and v2 that are connected via
     * an edge, this iterator iterates over both pairs (v1, v2) and (v2, v1).
     * @return an iterator for every directed edge in this graph.
     */
    open fun edgeTwiceIterator(): Iterator<Pair<V, V>> {
        if (n == 0) { // please wash your eyes
            return object : Iterator<Pair<V, V>> {
                override fun hasNext() = false
                override fun next(): Pair<V, V> = throw NoSuchElementException("Traversal has finished")
            }
        }

        return object : Iterator<Pair<V, V>> {

            var eCtr = 0

            val v1Iterator = map.keys.iterator()
            var v1Curr = v1Iterator.next()
            var v2Iterator = neighbors(v1Curr).iterator()

            override fun hasNext() = eCtr < 2 * m

            override fun next(): Pair<V, V> {
                if (!hasNext()) throw NoSuchElementException("Traversal has finished")
                while (!v2Iterator.hasNext()) {
                    v1Curr = v1Iterator.next()
                    v2Iterator = neighbors(v1Curr).iterator()
                }

                eCtr++
                return Pair(v1Curr, v2Iterator.next())
            }
        }
    }

    /**
     * Iterates over every undirected edge in this graph, i.e. for every two vertices v1 and v2 that are connected via
     * an edge, this iterator iterates over only exactly one of the pairs (v1, v2) and (v2, v1).
     * @return an iterator for every undirected edge in this graph.
     */
    open fun edgeIterator(): Iterator<Pair<V, V>> {
        return object : Iterator<Pair<V, V>> {

            var eCtr = 0

            val iter = edgeTwiceIterator()
            val seen = mutableSetOf<V>()

            override fun hasNext() = eCtr < m

            override fun next(): Pair<V, V> {
                while (iter.hasNext()) {
                    val next = iter.next()

                    if (next.second in seen) continue

                    eCtr++
                    seen.add(next.first)
                    return next
                }
                throw NoSuchElementException("Traversal has finished")
            }
        }
    }

    /**
     * Checks if [other] is an exact copy of this graph. This means that [other] needs to contain the same
     * vertex objects as this graph and the edges of [other] need to be between the same vertices as the edges of
     * this graph.
     *
     * The equals method of the vertex objects needs to be implemented properly for this method to work properly.
     *
     * @return True if [other] is an exact copy of this graph. False otherwise.
     */
    override fun equals(other: Any?): Boolean {
        return other != null &&
            other is SimpleGraph<*> &&
            other.n == this.n &&
            other.m == this.m &&
            other.V.all { it in this.V } &&
            other.map == this.map
    }

    /**
     * Creates a string containing the number of vertices in this graph, the number of edges in this graph, the
     * maximum degree of this graph and a list of all neighbors of every vertex in this graph.
     * @return A string representation of this graph.
     */
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("n:          $n\n")
        builder.append("m:          $m\n")
        builder.append("max-degree: ${maxDegree()}\n")
        for (v1 in V)
            builder.append("$v1: [${neighbors(v1).joinToString()}]\n")
        return builder.toString()
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }
}
