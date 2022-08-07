open class SimpleGraph<V> {

    private val m = HashMap<V, MutableSet<V>>()

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
}