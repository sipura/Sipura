package sipura.hardproblems

import sipura.alg.GraphProperty
import sipura.graphs.SimpleGraph
import sipura.utils.SetTheory
import java.util.LinkedList

object MaximalCliques {

    /**
     * Recursive part of Bron-Kerbosch algorithm used in [listMaximalCliques].
     *
     * @param g the graph.
     * @param k the current clique.
     * @param c the set of candidates that may be added to [k] in the future.
     * @param f the set of vertices that may not be added to [k] in the future.
     */
    private fun <V> bronKerboschPivot(
        g: SimpleGraph<V>,
        k: MutableSet<V>,
        c: MutableSet<V>,
        f: MutableSet<V>,
        results: MutableList<Set<V>>,
    ) {
        // if c and f are empty then k is a maximal clique
        if (c.isEmpty() && f.isEmpty()) {
            results.add(k)
            return
        }
        // if c is empty but f not then c is not maximal and we can return
        if (c.isEmpty()) return
        // find pivot vertex that has the largest number of neighbors in c
        var pivot = c.first()
        var size = -1
        var curSize = 0
        for (v in c) {
            curSize = SetTheory.intersectionSize(c, g.neighbors(v))
            if (curSize > size) {
                size = curSize
                pivot = v
            }
        }
        for (v in f) {
            curSize = SetTheory.intersectionSize(c, g.neighbors(v))
            if (curSize > size) {
                size = curSize
                pivot = v
            }
        }
        // now do recursive call for each candidate in c that is not a neighbor of pivot
        val cIter = c.iterator()
        while (cIter.hasNext()) {
            val v = cIter.next()
            if (v in g.neighbors(pivot)) continue
            // create new sets for next call
            val newK = k.toMutableSet()
            newK.add(v)
            val newC = SetTheory.intersection(c, g.neighbors(v))
            val newF = SetTheory.intersection(f, g.neighbors(v))
            bronKerboschPivot(g, newK, newC, newF, results)
            // update candidate set and ignore set
            cIter.remove()
            f.add(v)
        }
    }

    /**
     * Enumerates the vertex set of every maximal clique in the given graph [g].
     *
     * A maximal clique is defined as a subgraph c of [g] with the following two properties:
     * - c is a clique, i.e. every pair of vertices in c is connected via an edge.
     * - c is maximal, i.e. there is no vertex in [g] that is connected to every vertex in c.
     *
     * Uses a version of the Bron-Kerbosch algorithm that uses a pivot vertex in every recursive call and
     * a degeneracy ordering of the vertices in [g] for the initial calls.
     *
     * This version of the algorithm has a running time bound of O(3^(d/3) * d * n) where d is the degeneracy of
     * the graph.
     *
     * @return a [LinkedList] containing the vertex sets of every maximal clique in [g].
     * @see <a href=https://arxiv.org/abs/1006.5440>Listing All Maximal Cliques in Sparse Graphs in Near-optimal Time</a>
     */
    fun <V> listMaximalCliques(g: SimpleGraph<V>): LinkedList<Set<V>> {
        val results = LinkedList<Set<V>>()
        if (g.n == 0) return results
        // call bronKerboschPivot() once for each vertex with its neighbors that come later in the degeneracy ordering
        // as the candidate set. This guarantees that c will not be bigger than the degeneracy.
        val (_, ordering) = GraphProperty.degeneracyOrdering(g)
        val lookedAt = HashSet<V>()
        for (v in ordering) {
            val k = HashSet<V>()
            k.add(v)
            val c = HashSet<V>()
            g.neighbors(v).filterTo(c) { it !in lookedAt }
            val f = HashSet<V>()
            g.neighbors(v).filterTo(f) { it in lookedAt }
            bronKerboschPivot(g, k, c, f, results)
            lookedAt.add(v)
        }
        return results
    }
}
