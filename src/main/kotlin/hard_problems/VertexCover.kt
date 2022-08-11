package hard_problems

import SimpleGraph

object VertexCover {

    fun <V> decisionVertexCover(g: SimpleGraph<V>, k: Int): Boolean = decisionVertexCoverInner(g.copy(), k)

    /**
     * Doesn't do a copy of [g]
     */
    private fun <V> decisionVertexCoverInner(g: SimpleGraph<V>, k: Int): Boolean {
        if (g.m == 0) return true
        if (k == 0) return false

        val copy = g.copy()
        val (v1, v2) = g.getEdgeOnceIterator().next()
        g.removeVertex(v1)
        copy.removeVertex(v2)

        return decisionVertexCoverInner(g, k - 1) || decisionVertexCoverInner(copy, k - 1)
    }

}