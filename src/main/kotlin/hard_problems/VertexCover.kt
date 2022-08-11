package hard_problems

import SimpleGraph

object VertexCover {

    fun <V> decisionVertexCover(g: SimpleGraph<V>, k: Int): Boolean {
        if (k < 0) throw IllegalArgumentException("k cannot be negative")

        if (g.m == 0) return true
        if (k == 0) return false

        val (v1, v2) = g.edgeIterator().next()

        fun checkVertex(v: V): Boolean {
            val nbSet = g.neighbors(v)

            g.removeVertex(v)
            val result = decisionVertexCover(g, k - 1)
            g.addVertex(v)

            for (nb in nbSet) g.addEdge(v, nb)
            return result
        }

        return checkVertex(v1) || checkVertex(v2)

    }
}