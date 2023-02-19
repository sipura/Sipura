package sipura.alg

import sipura.graphs.SimpleGraph
import java.util.LinkedList

/**
 * A data structure that allows fast calculation of the k-cores of [g] for all k in the range of 0 to n.
 *
 * @constructor Creates the k-core decomposition based on the given graph [g].
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
        if (k < 0 || k > lists.size) throw IllegalArgumentException("k must be in the range from 0 to n")
        val core = HashSet<V>()
        var i = k
        while (i != -1) {
            core.addAll(lists[i])
            i = nextNonEmpty[i]
        }
        return core
    }
}
