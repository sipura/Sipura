package sipura.generate

import sipura.graphs.SimpleGraph

object Factory {

    /**
     * @throws IllegalArgumentException if [n] is negative
     *
     * @return A [SimpleGraph] of the form:          (1) - (2) - ... - (n-1) - (n)
     */
    fun createPathGraph(n: Int): SimpleGraph<Int> {
        if (n < 0) throw IllegalArgumentException("size can not be negative")
        return SimpleGraph<Int>().apply {
            (1..n).forEach { addVertex(it) }
            for (i in 1 until n) {
                addEdge(i, i + 1)
            }
        }
    }

    /**
     * @throws IllegalArgumentException if [n] is less than 3
     *
     *                                                #-------------------------#
     *                                                |                         |
     * @return A [SimpleGraph] of the form:          (1) - (2) - ... - (n-1) - (n)
     */
    fun createCycleGraph(n: Int): SimpleGraph<Int> {
        if (n < 3) throw IllegalArgumentException("A cycle must have at least 3 vertices")
        return SimpleGraph<Int>().apply {
            (1..n).forEach { addVertex(it) }
            for (i in 1 until n) {
                addEdge(i, i + 1)
            }
            addEdge(1, n)
        }
    }

    /**
     * @throws IllegalArgumentException if [n] is negative
     *
     * @return A [SimpleGraph] of the form:
     */
    fun createCompleteGraph(n: Int): SimpleGraph<Int> {
        if (n < 0) throw IllegalArgumentException("size can not be negative")
        return SimpleGraph<Int>().apply {
            (1..n).forEach { addVertex(it) }
            for (i in 1..n) {
                for (j in 1..n) {
                    if (i != j) addEdge(i, j)
                }
            }
        }
    }

    /**
     * @throws IllegalArgumentException if [n] is not positive
     *
     *                                              2
     *                                              |
     * @return A [SimpleGraph] of the form:     5---1---3
     *                                              |
     *                                              4
     */
    fun createStarGraph(n: Int) = createBipartiteGraph(1, n - 1)

    /**@return A bipartite graph, whose vertex partition has sizes [sizeA] and [sizeB]. Any two vertices of from
     * different sides in the graph are connected, thus there are [sizeA] * [sizeB] edges in total.*/
    fun createBipartiteGraph(sizeA: Int, sizeB: Int): SimpleGraph<Int> {
        if (sizeA < 0 || sizeB < 0) throw IllegalArgumentException("Both sides must have non-negative size")
        return SimpleGraph<Int>().apply {
            (1..(sizeA + sizeB)).forEach { addVertex(it) }
            for (i in 1..sizeA) {
                for (j in (sizeA + 1)..(sizeA + sizeB)) {
                    addEdge(i, j)
                }
            }
        }
    }

    /**
     * Creates a split graph with an independent set of size [sizeIS] and a clique of size [sizeC]. Also adds all
     * possible edges between the independent set and the clique.
     *
     * The vertices of the independent set are [1, ..., [sizeIS]] and the vertices of the clique
     * are [[sizeIS] + 1, ..., [sizeIS] + [sizeC]].
     *
     * @throws IllegalArgumentException if either [sizeIS] or [sizeC] are negative.
     * @return The created split graph.
     */
    fun createSplitGraph(sizeIS: Int, sizeC: Int): SimpleGraph<Int> {
        if (sizeIS < 0 || sizeC < 0) throw IllegalArgumentException("Both sides must have non-negative size")
        val g = SimpleGraph<Int>()
        // add all vertices
        for (v in 1..(sizeIS + sizeC)) {
            g.addVertex(v)
        }
        // add edges of clique
        for (v1 in (sizeIS + 1) until sizeIS + sizeC) {
            for (v2 in (v1 + 1)..(sizeIS + sizeC)) {
                g.addEdge(v1, v2)
            }
        }
        // add all edges between IS and Clique
        for (v1 in 1..sizeIS) {
            for (v2 in (sizeIS + 1)..(sizeIS + sizeC)) {
                g.addEdge(v1, v2)
            }
        }
        return g
    }
}
