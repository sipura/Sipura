package alg

import graphs.SimpleGraph
import utils.SetTheory.isDisjoint

object VertexSets {

    fun <V> getLeafs(g: SimpleGraph<V>): MutableSet<V> =
        g.V.filterTo(HashSet()) { g.degreeOf(it) == 1 }

    /**
     * assumes that [tree] is a tree.
     *
     * Works by creating a copy of [tree] and then repeatedly
     * removing all leafs of [tree], until only the center remains.
     *
     * runtime: O(n)
     */
    fun <V> treeCenter(tree: SimpleGraph<V>): MutableSet<V> {
        val copy = tree.copy()
        var leafs = HashSet(getLeafs(copy))

        while (copy.n > 2) {
            val newLeafs = HashSet<V>()
            for (leaf in leafs) {
                for (n in copy.neighbors(leaf)) { // has just 1 neighbour
                    if (copy.degreeOf(n) == 2) { // n will be a leaf after we remove vertex leaf
                        newLeafs.add(n)
                    }
                }
                copy.removeVertex(leaf)
            }
            leafs = newLeafs
        }

        return copy.V.toMutableSet()
    }

    /**
     * @Runtime O( sum of degrees in [S] )  which is bounded by O( |S| * [g].maxDegree )
     * @see <a href="https://en.wikipedia.org/wiki/Independent_set_(graph_theory)">Wikipedia page</a>
     *
     * @throws IllegalArgumentException if [S] contains a vertex that is not in [g].
     *
     * @return True iff [S] is an independent set of [g].
     */
    fun <V> isIndependentSet(g: SimpleGraph<V>, S: Set<V>): Boolean {
        for (s in S) {
            if (!isDisjoint(g.neighbors(s), S)) {
                return false
            }
        }
        return true
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/Vertex_cover">Wikipedia page</a>
     *
     * @throws IllegalArgumentException if [S] contains a vertex that is not in [g].
     *
     * @return True iff [S] is a vertex cover of [g]
     */
    fun <V> isVertexCover(g: SimpleGraph<V>, S: Set<V>): Boolean {
        for (v in S) {
            if (!g.contains(v)) throw IllegalArgumentException("Graph does not contain vertex $v.")
        }
        return g.edgeIterator().asSequence().all { (v1, v2) -> v1 in S || v2 in S }
    }

    /**
     * @see <a href="https://tcs.rwth-aachen.de/~langer/pub/partial-vc-wg08.pdf">Wikipedia page</a>
     *
     * @throws IllegalArgumentException if [S] contains a vertex that is not in [g].
     *
     * @return How many edges are covered by [S]
     */
    fun <V> countCoveredEdges(g: SimpleGraph<V>, S: Set<V>): Int {
        var counterOnce = 0
        var counterTwice = 0

        for (s in S) {
            for (nb in g.neighbors(s)) {
                if (nb !in S) {
                    counterOnce++
                } else {
                    counterTwice++
                }
            }
        }

        return counterOnce + (counterTwice / 2)
    }

    /**
     * Counts the number of edges in [g] between vertices in [S] and vertices not in [S].
     *
     * @throws IllegalArgumentException if [S] contains a vertex that is not in [g].
     */
    fun <V> cutSize(g: SimpleGraph<V>, S: Collection<V>): Int =
        S.sumOf { s -> g.neighbors(s).count { it !in S } }
}
