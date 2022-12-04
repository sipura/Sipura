package alg

import graphs.SimpleGraph
import java.util.LinkedList

/**
 * Provides method to inspect the connectivity of a graph.
 * This includes methods to check if a graph is connected, and to find the connected components of a graph.
 * Also provides methods to find the shortest path between two vertices.
 *
 * For a specific vertex, information like its connected component and its distance to a given vertex can be found.
 *
 * For more information, see https://en.wikipedia.org/wiki/Connectivity_(graph_theory)
 */
object Connectivity {

    /**
     * Does two BFSs at the same time starting from and alternating between [v1] and [v2]. Once one of them finds a
     * vertex that the other already found the function immediately stops and returns *true*. If one of the BFS ends
     * without that occurring then the function also stops and returns *false*.
     */
    fun <V> checkIfConnected(g: SimpleGraph<V>, v1: V, v2: V): Boolean {
        if (v1 !in g.V) throw IllegalArgumentException("Graph does not contain vertex v1")
        if (v2 !in g.V) throw IllegalArgumentException("Graph does not contain vertex v2")

        val queue1 = LinkedList(listOf(v1))
        val seen1 = mutableSetOf(v1)

        val queue2 = LinkedList(listOf(v2))
        val seen2 = mutableSetOf(v2)

        while (queue1.isNotEmpty() && queue2.isNotEmpty()) {
            val next1: V = queue1.removeFirst()
            for (nb in g.neighbors(next1)) {
                if (nb !in seen1) {
                    if (nb in seen2) return true
                    queue1.addLast(nb)
                    seen1.add(nb)
                }
            }
            val next2: V = queue2.removeFirst()
            for (nb in g.neighbors(next2)) {
                if (nb !in seen2) {
                    if (nb in seen1) return true
                    queue2.addLast(nb)
                    seen2.add(nb)
                }
            }
        }

        return false // traversal finished without hit
    }

    /**
     * Does a BFS starting from [v] and returns a set of all vertices that are reachable from [v].
     *
     * runtime: O(n + m)  ->  every vertex and every edge is visited
     *
     * @throws IllegalArgumentException if [v] is not in the graph
     *
     * @return a set of all vertices that are reachable from [v]
     */
    fun <V> getConnectedComponent(g: SimpleGraph<V>, v: V): MutableSet<V> {
        if (v !in g.V) throw IllegalArgumentException("Graph does not contain vertex v")

        return Traversal.breadthFirstSearchIterator(g, v).asSequence().toMutableSet()
    }

    /**
     * Returns a map where the keys of which are all vertices in the graph and
     * the values of which are the connected components, represented as sets of vertices.
     *
     * The connected components are disjoint, i.e. no vertex is contained in more than one connected component.
     * The connected components are maximal, i.e. no vertex can be added to a connected component without
     *
     * @return a map where the keys of which are all vertices in the graph and the values are sets of the
     * respective connected component.
     */
    fun <V> listAllConnectedComponents(g: SimpleGraph<V>): MutableMap<V, MutableSet<V>> {
        val result = HashMap<V, MutableSet<V>>()

        for (v in g.V) {
            if (v !in result) {
                val component = getConnectedComponent(g, v)
                component.forEach { result[it] = component }
            }
        }

        return result
    }

    /**
     * Unlike [listAllConnectedComponents] does not need to store all components in memory at the same time.
     *
     * @return The number of connected components in the graph.
     */
    fun <V> numberOfConnectedComponents(g: SimpleGraph<V>): Int {
        val seen = HashSet<V>()
        var count = 0

        for (v in g.V) {
            if (v !in seen) {
                seen.addAll(getConnectedComponent(g, v))
                count++
            }
        }

        return count
    }

    /**
     * @throws IllegalArgumentException if [g] is empty.
     *
     * @return True if the graph is connected, false otherwise.
     */
    fun <V> isConnected(g: SimpleGraph<V>): Boolean {
        if (g.n == 0) throw IllegalArgumentException("alg.Connectivity for empty graph is ambiguous, so exception for good measure")
        return getConnectedComponent(g, g.V.first()).size == g.n
    }

    /**
     * Returns the shortest path from [vStart] to [vEnd] by doing a BFS starting from [vStart].
     * The result is stored in a list of vertices, where the first element is [vStart] and the last element is [vEnd].
     *
     * @throws IllegalArgumentException if [vStart] or [vEnd] is not in the graph or if [g] is empty.
     *
     * @return a list of vertices representing the shortest path from [vStart] to [vEnd].
     */
    fun <V> shortestPath(g: SimpleGraph<V>, vStart: V, vEnd: V): List<V> {
        if (g.n == 0) throw IllegalArgumentException("alg.Connectivity for empty graph is ambiguous, so exception for good measure")
        if (vStart !in g.V) throw IllegalArgumentException("Graph does not contain vertex vStart")
        if (vEnd !in g.V) throw IllegalArgumentException("Graph does not contain vertex vEnd")

        val bfs = Traversal.breadthFirstSearchIterator(g, vEnd)
        val dist = HashMap<V, Int>()
        for (v in g.V) dist[v] = Int.MAX_VALUE
        dist[vEnd] = 0
        val prev = HashMap<V, V>()

        for (curr in bfs) {
            if (curr == vStart) break

            for (nb in g.neighbors(curr)) {
                if (dist[curr]!! + 1 < dist[nb]!!) {
                    dist[nb] = dist[curr]!! + 1
                    prev[nb] = curr
                }
            }
        }

        if (dist[vStart] == Int.MAX_VALUE) throw IllegalStateException()

        // restore path
        val l = LinkedList(listOf(vStart))
        while (l.last != vEnd) {
            l.addLast(prev[l.last])
        }
        return l
    }

    /**
     * The distance of two neighboring vertices is 1, and the distance of a vertex to itself is 0.
     *
     * @return The number of edges in the shortest path from [v1] to [v2] in graph [g]
     */
    fun <V> distance(g: SimpleGraph<V>, v1: V, v2: V): Int = shortestPath(g, v1, v2).size - 1
}
