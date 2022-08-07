open class SimpleGraph<V> {

    private val m = HashMap<V, MutableSet<V>>()

    operator fun contains(v: V) : Boolean = v in m

    fun addVertex(v: V) : Boolean {
        return if (v !in m) {
            m[v] = mutableSetOf()
            true
        } else false
    }

    fun removeVertex(v: V) : Boolean {
        if (v !in m) return false
        for (nb in m[v]!!) {
            m[nb]!!.remove(v)
        }
        m.remove(v)
        return true
    }

    fun degreeOf(v: V) : Int {
        if (v !in m) throw IllegalArgumentException("Graph does not contain the given vertex.")
        return m[v]!!.size
    }
}