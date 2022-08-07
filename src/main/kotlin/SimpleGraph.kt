open class SimpleGraph<V> {

    // If v1 and v2 are connected, then *v1 in m[v2]* and *v2 in m[v1]* has to hold
    private val m = HashMap<V, MutableSet<V>>()

    operator fun contains(v: V): Boolean = v in m

    fun addVertex(v: V): Boolean {
        return if (v !in m) {
            m[v] = mutableSetOf()
            true
        } else false
    }

    fun removeVertex(v: V): Boolean {
        if (v !in m) return false
        for (nb in m[v]!!) {
            m[nb]!!.remove(v)
        }
        m.remove(v)
        return true
    }

    fun degreeOf(v: V): Int {
        if (v !in m) throw IllegalArgumentException("Graph does not contain the given vertex.")
        return m[v]!!.size
    }

    fun addEdge(v1: V, v2: V): Boolean {
        if (v1 !in m) throw IllegalArgumentException("Graph does not contain v1.")
        if (v2 !in m) throw IllegalArgumentException("Graph does not contain v2.")

        if (v1 in m[v2]!!) return false

        m[v1]!!.add(v2)
        m[v2]!!.add(v1)
        return true
    }

    fun removeEdge(v1: V, v2: V): Boolean {
        if (v1 !in m) throw IllegalArgumentException("Graph does not contain v1.")
        if (v2 !in m) throw IllegalArgumentException("Graph does not contain v2.")

        if (v1 !in m[v2]!!) return false

        m[v1]!!.remove(v2)
        m[v2]!!.remove(v1)
        return true
    }
}