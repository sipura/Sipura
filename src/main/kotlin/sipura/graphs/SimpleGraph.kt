package sipura.graphs

open class SimpleGraph<V> {

    // If v1 and v2 are connected, then *v1 in map[v2]* and *v2 in map[v1]* has to hold
    private val map = HashMap<V, MutableSet<V>>()

    val n: Int
        get() = map.size

    /**
     * runtime: O(1)  ->  constant, because [m] gets updated every time an edge gets added or removed
     */
    var m: Int = 0
        private set

    /**
     * Uses an immutable set because no elements should be removed from this set. The set that is
     * returned is used in the internal data structure of this class. Removing elements from this
     * set may cause incorrect behaviour of the graph in future calls.
     */
    val V: Set<V>
        get() = map.keys

    private fun assertVertexExists(v: V, vertexName: String) {
        if (v !in map) throw IllegalArgumentException("Graph does not contain vertex $vertexName.")
    }

    /**
     * runtime: O(1)  ->  constant
     */
    operator fun contains(v: V): Boolean = v in map

    /**
     * runtime: O(1)  ->  constant
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
     * runtime: O([degreeOf] [v])  ->  each neighbour needs to be updated that [v] is not in its neighbourhood anymore
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
     * runtime: O(1)  ->  constant
     */
    open fun degreeOf(v: V): Int {
        assertVertexExists(v, "v")
        return map[v]!!.size
    }

    /**
     * runtime: O(1)  ->  constant
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
     * @throws IllegalArgumentException if [v1] or [v2] are the same vertex, or if [v1] or [v2] are not in the graph.
     *
     * runtime: O(1)  ->  constant
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
     * runtime: O(1)  ->  constant
     *
     * @throws IllegalArgumentException if [v1] or [v2] are the same vertex, or if [v1] or [v2] are not in the graph.
     *
     * @return True if [v1] and [v2] are connected, false otherwise.
     */
    open fun hasEdge(v1: V, v2: V): Boolean {
        if (v1 == v2) throw IllegalArgumentException("no loops are allowed. this is likely a false input")
        assertVertexExists(v1, "v1")
        assertVertexExists(v2, "v2")

        return v1 in map[v2]!!
    }

    /**
     * runtime: O( [m] * [f] )  ->  calls [f] for each edge
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
     * Uses an immutable set because no elements should be removed from this set. The set that is
     * returned is used in the internal data structure of this class. Removing elements from this
     * set may cause incorrect behaviour of the graph in future calls.
     *
     * runtime: O(1)  ->  constant. This is possible the neighbourhood that is already stored gets returned.
     */
    open fun neighbors(v: V): Set<V> {
        assertVertexExists(v, "v")
        return map[v]!!
    }

    /**
     * shallow copy
     */
    open fun copy(): SimpleGraph<V> {
        val copy = SimpleGraph<V>()

        for (v in V) copy.addVertex(v)
        forEachEdge { v1, v2 -> copy.addEdge(v1, v2) }

        return copy
    }

    /**
     * runtime: O([n])  ->  for every vertex, the degree gets checked in constant time
     */
    open fun maxDegree(): Int = V.maxOf { degreeOf(it) }

    /**
     * returns edge each twice, as Pair(v1, v2) and as Pair(v2, v1)
     */
    open fun edgeTwiceIterator(): Iterator<Pair<V, V>> {
        if (n == 0) { // please wash your eyes
            return object : Iterator<Pair<V, V>> {
                override fun hasNext() = false
                override fun next(): Pair<V, V> = throw NoSuchElementException("alg.Traversal has finished")
            }
        }

        return object : Iterator<Pair<V, V>> {

            var eCtr = 0

            val v1Iterator = map.keys.iterator()
            var v1Curr = v1Iterator.next()
            var v2Iterator = neighbors(v1Curr).iterator()

            override fun hasNext() = eCtr < 2 * m

            override fun next(): Pair<V, V> {
                if (!hasNext()) throw NoSuchElementException("alg.Traversal has finished")
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
     * returns edge each twice, as Pair(v1, v2) and as Pair(v2, v1)
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
                throw NoSuchElementException("alg.Traversal has finished")
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
     * @return A string representation of the graph.
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
}
