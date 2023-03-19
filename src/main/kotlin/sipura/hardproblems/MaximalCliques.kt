package sipura.hardproblems

import sipura.alg.GraphProperty
import sipura.graphs.SimpleGraph
import sipura.utils.SetTheory
import java.util.LinkedList

object MaximalCliques {

    /**
     * Iterates over every maximal clique in the given graph [g].
     *
     * A maximal clique is defined as a subgraph c of [g] with the following two properties:
     * - c is a clique, i.e. every pair of vertices in c is connected via an edge.
     * - c is maximal, i.e. there is no vertex in [g] that is connected to every vertex in c.
     *
     * Uses an iterative implementation of the Bron-Kerbosch algorithm that uses pivot vertices and
     * a degeneracy ordering of the vertices in [g] for the initial calls.
     *
     * This version of the algorithm has a running time bound of O(3^(d/3) * d * n) for iterating over all maximal
     * cliques where d is the degeneracy of the graph.
     *
     * @return an [Iterator] that iterates over the vertex sets of every maximal clique in [g].
     * @see <a href=https://arxiv.org/abs/1006.5440>Listing All Maximal Cliques in Sparse Graphs in Near-optimal Time</a>
     */
    fun <V> maximalCliqueIterator(g: SimpleGraph<V>): Iterator<MutableSet<V>> = object : Iterator<MutableSet<V>> {

        var iterationDone = false // true only if the iteration over all maximal cliques is done
        val degeneracyIter: Iterator<V> // iterator over a degeneracy ordering, used for initial calls in recursion tree
        val lookedAt = HashSet<V>() // saves for which vertices the initial call has already been performed
        val k = LinkedList<V>() // contains the vertices of the current clique in the recursion tree
        val c: Array<MutableSet<V>> // contains the candidate sets for each level in the recursion tree
        val f: Array<MutableSet<V>> // contains the set of vertices that are not allowed for each level in the recursion tree
        val pivotNeighbors: Array<Set<V>> // contains the neighbors of the pivot vertex for each level in the recursion tree
        val candidateIter: Array<MutableIterator<V>> // contains an iterator over the candidate set for each level in the recursion tree
        var curDepth = -1 // saves the current level of the recursion tree, -1 is an initial call
        var foundMaximal = false // true if the algorithm has found a new maximal clique

        init {
            // initialize all data structures and variables that are necessary for the iteration to work
            if (g.n == 0) {
                degeneracyIter = setOf<V>().iterator()
                iterationDone = true
                c = Array(0) { HashSet() }
                f = Array(0) { HashSet() }
                pivotNeighbors = Array(0) { HashSet() }
                candidateIter = Array(0) { mutableSetOf<V>().iterator() }
            } else {
                val maxDegree = g.maxDegree()
                c = Array(maxDegree + 1) { HashSet() }
                f = Array(maxDegree + 1) { HashSet() }
                pivotNeighbors = Array(maxDegree) { HashSet() }
                candidateIter = Array(maxDegree) { mutableSetOf<V>().iterator() }
                val (_, ordering) = GraphProperty.degeneracyOrdering(g)
                degeneracyIter = ordering.iterator()
                calcNextMaximalClique()
            }
        }

        override fun hasNext(): Boolean = !iterationDone

        override fun next(): MutableSet<V> {
            if (!hasNext()) throw NoSuchElementException("The iteration is done.")
            val copy = k.toMutableSet()
            calcNextMaximalClique()
            return copy
        }

        private fun calcNextMaximalClique() {
            foundMaximal = false
            while (!foundMaximal) {
                if (curDepth == -1) {
                    // curDepth == -1 means that we are doing an initial call in the recursive tree
                    if (!degeneracyIter.hasNext()) {
                        // in this case we are already done with all initial calls meaning we will not find any more
                        // maximal cliques
                        iterationDone = true
                        return
                    } else {
                        // make sure that the vertex that was previously added is removed
                        if (k.size == 1) k.pop()
                        // create sets for next call
                        val v = degeneracyIter.next()
                        k.push(v)
                        c[0].clear()
                        g.neighbors(v).filterTo(c[0]) { it !in lookedAt }
                        f[0].clear()
                        g.neighbors(v).filterTo(f[0]) { it in lookedAt }
                        lookedAt.add(v)
                        curDepth = 0
                        prepareCurDepth()
                    }
                } else {
                    // walk through recursion tree until a maximal clique has been found or until we are back to an
                    // initial call
                    while (curDepth >= 0 && !foundMaximal) {
                        if (!candidateIter[curDepth].hasNext()) {
                            // make sure that the vertex that was previously added is removed
                            if (k.size == curDepth + 2) k.pop()
                            curDepth--
                        } else {
                            val v = candidateIter[curDepth].next()
                            if (v in pivotNeighbors[curDepth]) continue
                            // make sure that the vertex that was previously added is removed
                            if (k.size == curDepth + 2) k.pop()
                            // update sets for next call
                            k.push(v)
                            c[curDepth + 1] = SetTheory.intersection(c[curDepth], g.neighbors(v))
                            f[curDepth + 1] = SetTheory.intersection(f[curDepth], g.neighbors(v))
                            // update current candidate set and ignore set
                            candidateIter[curDepth].remove()
                            f[curDepth].add(v)
                            curDepth++
                            prepareCurDepth()
                        }
                    }
                }
            }
        }

        /**
         * Does three things:
         * - Checks if the current node in the in the recursion tree represents a maximal clique.
         * - Checks if there are more candidates to add to the current clique.
         * - If there are more candidates then the pivot vertex is selected.
         *
         * In all three cases the data structures and variables are updated accordingly.
         */
        private fun prepareCurDepth() {
            // if c and f are empty then k is a maximal clique
            if (c[curDepth].isEmpty() && f[curDepth].isEmpty()) {
                foundMaximal = true
                curDepth--
                return
            }
            // if c is empty but f not then c is not maximal and we can return
            if (c[curDepth].isEmpty()) {
                curDepth--
                return
            }
            // find pivot vertex that has the largest number of neighbors in c
            var pivot = c[curDepth].first()
            var size = -1
            var curSize = 0
            for (v in c[curDepth]) {
                curSize = SetTheory.intersectionSize(c[curDepth], g.neighbors(v))
                if (curSize > size) {
                    size = curSize
                    pivot = v
                }
            }
            for (v in f[curDepth]) {
                curSize = SetTheory.intersectionSize(c[curDepth], g.neighbors(v))
                if (curSize > size) {
                    size = curSize
                    pivot = v
                }
            }
            pivotNeighbors[curDepth] = g.neighbors(pivot)
            candidateIter[curDepth] = c[curDepth].iterator()
        }
    }
}
