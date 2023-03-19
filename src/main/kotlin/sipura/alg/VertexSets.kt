package sipura.alg

import sipura.graphs.SimpleGraph
import sipura.utils.SetTheory.isDisjoint
import java.util.LinkedList

object VertexSets {

    /**
     * @return a [MutableSet] of all vertices with at exactly 1 neighbor.
     */
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

    /**
     * A data structure that allows fast calculation of the k-cores of [g] for all k in the range of 0 to n.
     *
     * @constructor Creates the k-core decomposition based on the given graph [g].
     *
     * Runtime: O(n + m)
     */
    class KCoreDecomposition<V>(val g: SimpleGraph<V>) {

        private val lists: Array<LinkedList<V>> = Array(g.n) { LinkedList() }
        private val nextNonEmpty: IntArray = IntArray(g.n) { -1 }

        init {
            val degeneracyOrdering = GraphProperty.degeneracyOrdering(g).second
            val found = HashSet<V>()
            var k = 0
            for (v in degeneracyOrdering) {
                found.add(v)
                val forwardDegree = g.neighbors(v).count { it !in found }
                if (forwardDegree > k) {
                    for (i in k until forwardDegree) {
                        nextNonEmpty[i] = forwardDegree
                    }
                    k = forwardDegree
                }
                lists[k].addLast(v)
            }
        }

        /**
         * Calculates the vertex set of the [k]-core of [g].
         *
         * The [k]-core of [g] is the largest subgraph of [g] with minimum degree at least [k].
         *
         * Runtime: O(|kCore([k])|)  ->  Runtime only depends on the size of the result
         * @throws IllegalArgumentException if k is smaller than 0 or larger than the size of the graph [g].
         */
        fun kCore(k: Int): MutableSet<V> {
            if (k < 0 || k >= lists.size) throw IllegalArgumentException("k must be in the range from 0 to n")
            val core = HashSet<V>()
            var i = k
            while (i != -1) {
                core.addAll(lists[i])
                i = nextNonEmpty[i]
            }
            return core
        }
    }

    /**
     * Iterates over every triangle in the given graph [g].
     *
     * A triangle is a set of three vertices that are all connected to each other.
     *
     * Uses a simple algorithm due to Chiba and Nishizeki that has a running time bound of O(m * d) for iterating
     * over all triangles where d is the degeneracy of the graph.
     *
     * @return an [Iterator] that iterates over the vertex sets of every Triangle in [g]. The vertex sets are
     * represented as a [Triple].
     * @see <a href=https://doi.org/10.1137/0214017>Arboricity and Subgraph Listing Algorithms</a>
     */
    fun <V> triangleIterator(g: SimpleGraph<V>): Iterator<Triple<V, V, V>> = object : Iterator<Triple<V, V, V>> {

        private val copy = g.copy()
        private val degreeOrdering: Array<LinkedList<V>>
        private var curDegree = 0
        private var iterationDone = false
        private var nextTriangle = Triple(g.V.first(), g.V.first(), g.V.first())
        private val marked = HashSet<V>()
        private var curV: V? = null
        private var curU: V? = null
        private var outerIterator = setOf<V>().iterator()
        private var innerIterator = setOf<V>().iterator()

        init {
            // sort vertices by degree in linear time
            val maxDegree = copy.maxDegree()
            degreeOrdering = Array(maxDegree + 1) { LinkedList() }
            for (v in copy.V) {
                degreeOrdering[copy.degreeOf(v)].addFirst(v)
            }
            calcNextTriangle()
        }

        override fun hasNext(): Boolean = !iterationDone

        override fun next(): Triple<V, V, V> {
            if (!hasNext()) throw NoSuchElementException("The iteration is done.")
            val curTriangle = nextTriangle
            calcNextTriangle()
            return curTriangle
        }

        private fun calcNextTriangle() {
            var triangleFound = false
            // either find the next triangle or finish the iteration
            while (!triangleFound && !iterationDone) {
                prepareIterators()
                if (iterationDone) continue
                // at this point innerIterator must have a next element
                val w = innerIterator.next()
                if (w in marked) {
                    nextTriangle = Triple(curV!!, curU!!, w)
                    triangleFound = true
                }
            }
        }

        /**
         * After this function has been called either the innerIterator has a next element or iterationDone is true.
         */
        private fun prepareIterators() {
            // check if outerIterator has next element
            while (outerIterator.hasNext() && !innerIterator.hasNext()) {
                if (curU != null) marked.remove(curU!!)
                curU = outerIterator.next()
                innerIterator = copy.neighbors(curU!!).iterator()
            }
            // check if inner iterator has next element
            if (innerIterator.hasNext()) return
            // a new v needs to be selected
            if (curV != null) copy.removeVertex(curV!!)
            if (curU != null) {
                marked.remove(curU!!)
                curU = null
            }
            while (copy.n > 2) {
                // find next vertex in degreeOrdering
                while (degreeOrdering[curDegree].isEmpty()) {
                    curDegree++
                }
                curV = degreeOrdering[curDegree].removeFirst()
                // mark all neighbors of v
                marked.addAll(copy.neighbors(curV!!))
                // make sure v has neighbors and that one of those neighbors also has neighbors
                outerIterator = copy.neighbors(curV!!).iterator()
                while (outerIterator.hasNext()) {
                    if (curU != null) marked.remove(curU!!)
                    curU = outerIterator.next()
                    innerIterator = copy.neighbors(curU!!).iterator()
                    if (innerIterator.hasNext()) return
                }
                copy.removeVertex(curV!!)
            }
            iterationDone = true
        }
    }
}
