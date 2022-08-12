package hard_problems

import graphs.SimpleGraph

object VertexColoring {

    /**
     * Always assumes that [coloring] is valid (i.e. no neighbors have the same colors)
     */
    fun <V> hasKColoring(g: SimpleGraph<V>, k: Int, coloring: Map<V, Int> = emptyMap()): Boolean {
        if (coloring.size == g.n) return true

        val v = g.V.first { it !in coloring }
        for (color in 1..k) {
            if (g.neighbors(v).none { coloring[it] == color }) {
                val newColors = HashMap(coloring)
                newColors[v] = color
                if (hasKColoring(g, k, newColors)) return true
            }
        }
        return false
    }
}