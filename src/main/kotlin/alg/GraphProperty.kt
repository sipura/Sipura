package alg

import graphs.SimpleGraph

object GraphProperty {

    fun <V> isTree(g: SimpleGraph<V>): Boolean =
        (g.m == g.n - 1) && Connectivity.isConnected(g) // fail fast: checking edge count runs in constant time

    /**
     * runtime: O( 1 )  ->  Is constant because we know that a complete graph has n * (n - 1) / 2 edges.
     * This term can be computed and checked in constant time.
     *
     * @return True if [g] is complete, meaning that every vertex is connected to every other vertex.
     */
    fun <V> isComplete(g: SimpleGraph<V>): Boolean = g.m == g.n * (g.n - 1) / 2

    /**
     * @return True if [g] is acyclic, meaning that there is no path of length greater 0 from a vertex to itself.
     */
    fun <V> isAcyclic(g: SimpleGraph<V>): Boolean =
        g.m == g.n - Connectivity.numberOfConnectedComponents(g)

    /**
     * @return True if every vertex in [g] has degree [k], false otherwise.
     */
    fun <V> isKRegular(g: SimpleGraph<V>, k: Int): Boolean {
        if (g.n == 0) throw IllegalArgumentException("Regularity for empty graph doesn't really make sense.")
        if (k < 0) throw IllegalArgumentException("k-regularity is not defined for negative values of k.")

        return g.V.all { g.degreeOf(it) == k }
    }

    /**
     * A graph is cubic if every vertex has degree 3. This is a special case of [isKRegular] with k = 3.
     */
    fun <V> isCubic(g: SimpleGraph<V>): Boolean = isKRegular(g, 3)

    /**
     * A graph is bipartite if its vertices can be partitioned into two disjoint sets such that every edge connects
     * a vertex in one set to a vertex in the other set.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Bipartite_graph">Wikipedia: Bipartite graph</a>
     *
     * @return True if [g] is bipartite, false otherwise.
     *
     */
    fun <V> isBipartite(g: SimpleGraph<V>): Boolean {
        val components: MutableCollection<MutableSet<V>> = Connectivity.listAllConnectedComponents(g).values

        for (component in components) {
            val layerIter = Traversal.breadthFirstSearchLayerIterator(g, component.first())

            val sides = HashMap<V, Int>()
            var toggle = 1

            for (layer in layerIter) {
                for (v in layer) {
                    sides[v] = toggle
                    if (g.neighbors(v).any { sides.getOrDefault(it, 0) == toggle }) {
                        return false
                    }
                }
                toggle = -toggle
            }
        }
        return true
    }

    /**
     * Runtime: O([g].maxDegree)
     *
     * @return The h-Index of [g], i.e. the biggest natural number *h* so that there are at
     * least *h* vertices with degree of at least *h*
     */
    fun <V> hIndex(g: SimpleGraph<V>): Int {
        if (g.n == 0) throw IllegalArgumentException()

        val arr = IntArray(g.maxDegree() + 2) // Indices are: 0, 1, ..., maxDegree, maxDegree+1
        for (v in g.V) arr[g.degreeOf(v)] += 1

        for (i in g.maxDegree() downTo 0) {
            arr[i] += arr[i + 1] // arr[i] contains number of vertices with degree at least i
            if (arr[i] >= i) return i
        }

        throw RuntimeException("Shouldn't reach this part as the h-index is always at least 0 per definition")
    }

    /**
     *  Checks if the graph has no triangles.
     */
    fun <V> isTriangleFree(g: SimpleGraph<V>): Boolean {
        for (v in g.V) {
            val neighbors = g.neighbors(v)
            for (u in neighbors) {
                for (w in neighbors) {
                    if (u != w && g.hasEdge(u, w)) return false
                }
            }
        }
        return true
    }
}
