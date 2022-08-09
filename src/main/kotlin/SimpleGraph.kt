open class SimpleGraph<V> {

    // If v1 and v2 are connected, then *v1 in map[v2]* and *v2 in map[v1]* has to hold
    private val map = HashMap<V, MutableSet<V>>()

    val n: Int
        get() = map.size

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
    fun addVertex(v: V): Boolean {
        return if (v !in map) {
            map[v] = mutableSetOf()
            true
        } else false
    }

    /**
     * runtime: O([degreeOf] [v])  ->  each neighbour needs to be updated that [v] is not in its neighbourhood anymore
     */
    fun removeVertex(v: V): Boolean {
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
    fun degreeOf(v: V): Int {
        assertVertexExists(v, "v")
        return map[v]!!.size
    }

    /**
     * runtime: O(1)  ->  constant
     */
    fun addEdge(v1: V, v2: V): Boolean {
        assertVertexExists(v1, "v1")
        assertVertexExists(v2, "v2")

        if (v1 in map[v2]!!) return false

        map[v1]!!.add(v2)
        map[v2]!!.add(v1)
        m++
        return true
    }

    /**
     * runtime: O(1)  ->  constant
     */
    fun removeEdge(v1: V, v2: V): Boolean {
        assertVertexExists(v1, "v1")
        assertVertexExists(v2, "v2")

        if (v1 !in map[v2]!!) return false

        map[v1]!!.remove(v2)
        map[v2]!!.remove(v1)
        m--
        return true
    }

    /**
     * runtime: O(1)  ->  constant
     */
    fun hasEdge(v1: V, v2: V): Boolean {
        assertVertexExists(v1, "v1")
        assertVertexExists(v2, "v2")

        return v1 in map[v2]!!
    }

    /**
     * runtime: O( [m] * [f] )  ->  calls [f] for each edge
     */
    fun forEachEdge(f: (V, V) -> Unit) {
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
    fun neighbors(v: V): Set<V> {
        assertVertexExists(v, "v")
        return map[v]!!
    }

    /**
     * shallow copy
     */
    fun copy(): SimpleGraph<V> {
        val copy = SimpleGraph<V>()

        for (v in V) copy.addVertex(v)
        forEachEdge { v1, v2 -> copy.addEdge(v1, v2) }

        return copy
    }

    /**
     * runtime: O([n])  ->  for every vertex, the degree gets checked in constant time
     */
    fun maxDegree(): Int = V.maxOf { degreeOf(it) }
}












