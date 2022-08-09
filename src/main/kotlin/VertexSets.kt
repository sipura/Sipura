object VertexSets {

    fun <V> getLeafs(g: SimpleGraph<V>): MutableSet<V> =
        g.V.filterTo(HashSet()) { g.degreeOf(it) == 1 }

    /**
     * assumes that [g] is a tree.
     *
     * Works by creating a copy of [g] and then repeatedly
     * removing all leafs of [g], until only the center remains.
     */
    fun <V> treeCenter(g: SimpleGraph<V>): MutableSet<V> {
        val copy = g.copy()
        var leafs = HashSet(getLeafs(copy))

        while (copy.n > 2) {
            val newLeafs = HashSet<V>()
            for (leaf in leafs) {
                newLeafs.addAll(copy.neighbors(leaf)) // has just 1 neighbour ...
                copy.removeVertex(leaf)
            }
            leafs = newLeafs
        }

        return copy.V.toMutableSet()
    }

    /**
     * @Runtime O( sum of degrees in [S] )  which is bounded by O( |S| * [g].maxDegree )
     * @see <a href="https://en.wikipedia.org/wiki/Independent_set_(graph_theory)">Wikipedia page</a>
     * @return True iff [S] is an independent set of [g]
     */
    fun <V> isIndependentSet(S: Set<V>, g: SimpleGraph<V>): Boolean {
        for (s in S)
            if (g.neighbors(s).any { it in S })
                return false

        return true
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/Vertex_cover">Wikipedia page</a>
     * @return True iff [S] is a vertex cover of [g]
     */
    fun <V> isVertexCover(S: Set<V>, g: SimpleGraph<V>): Boolean {
        val x = HashSet(S) // copy-constructor
        for (s in S) { // add neighbours for all
            x.addAll(g.neighbors(s))
        }

        return x.size == g.n
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/Dominating_set">Wikipedia page</a>
     * @return True iff [S] is a dominating set of [g]
     */
    fun <V> isDominatingSet(S: Set<V>, g: SimpleGraph<V>): Boolean {
        for (v in g.V subtract S) {
            var hasDominatingNeighbour = false
            for (nb in g.neighbors(v))
                if (nb in S)
                    hasDominatingNeighbour = true

            if (!hasDominatingNeighbour)
                return false
        }
        return true
    }

    /**
     * @see <a href="https://tcs.rwth-aachen.de/~langer/pub/partial-vc-wg08.pdf">Wikipedia page</a>
     * @return How many edges are covered by [S]
     */
    fun <V> countCoveredEdges(S: Set<V>, g: SimpleGraph<V>): Int {
        var counter = 0
        // don't count edges with both ends in S twice
        for (v in S)
            for (w in g.neighbors(v))
                if (w !in S || (w in S && v.hashCode() < w.hashCode()))
                    counter++
        return counter
    }

    /**
     * Assumes that [S] is a subset of the vertices of [g].
     */
    fun <V> cutSize(g: SimpleGraph<V>, S: Collection<V>): Int =
        S.sumOf { v -> g.neighbors(v).count { it !in S } }

}