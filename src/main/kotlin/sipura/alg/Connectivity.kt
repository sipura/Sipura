package sipura.alg

import sipura.graphs.SimpleGraph
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
     * Checks if the two given vertices [v1] and [v2] are connected in [g].
     *
     * Does two BFSs at the same time starting from and alternating between [v1] and [v2]. Once one of them finds a
     * vertex that the other already found the function immediately stops and returns *true*. If one of the BFS ends
     * without that occurring then the function also stops and returns *false*.
     *
     * Runtime: O(n + m)
     * @throws IllegalArgumentException if [v1] or [v2] are not in [g].
     * @return True if [v1] and [v2] are connected, false otherwise.
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
     * Calculates the connected component in the given graph [g] containing the vertex [v].
     *
     * Runtime: O(n + m)
     * @throws IllegalArgumentException if [v] is not in the graph.
     * @return a set of all vertices that are reachable from [v].
     */
    fun <V> getConnectedComponent(g: SimpleGraph<V>, v: V): MutableSet<V> {
        if (v !in g.V) throw IllegalArgumentException("Graph does not contain vertex v")

        return Traversal.breadthFirstSearchIterator(g, v).asSequence().toMutableSet()
    }

    /**
     * Calculates all connected components of the given graph [g].
     *
     * The connected components are disjoint, i.e. no vertex is contained in more than one connected component.
     *
     * The connected components are maximal, i.e. adding any vertex to it will disconnect the component.
     *
     * Runtime: O(n + m)
     * @return a map mapping every vertex to its connected component. Connected components are represented
     * as sets of vertices.
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
     * Counts the number of connected components in the given graph [g].
     * Unlike [listAllConnectedComponents] does not need to store all components in memory at the same time.
     *
     * Runtime: O(n + m)
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
     * Checks if the given graph [g] is connected.
     *
     * A graph is connected if every pair of vertices is connected via at least one path.
     *
     * Runtime: O(n + m)
     * @throws IllegalArgumentException if [g] is empty.
     * @return True if the graph is connected, False otherwise.
     */
    fun <V> isConnected(g: SimpleGraph<V>): Boolean {
        if (g.n == 0) throw IllegalArgumentException("Connectivity for empty graph is ambiguous, so exception for good measure")
        return getConnectedComponent(g, g.V.first()).size == g.n
    }

    /**
     * Calculates the shortest path from [vStart] to [vEnd] by doing a BFS starting from [vStart].
     * The result is stored in a list of vertices, where the first element is [vStart] and the last element is [vEnd].
     *
     * Runtime: O(n + m)
     * @throws IllegalArgumentException if [vStart] or [vEnd] is not in the graph or if [g] is empty.
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
     * Calculates the distance between the two given vertices [v1] and [v2].
     *
     * The distance is defined as the number of edges in the shortest path between the two vertices.
     * The distance of two neighboring vertices is 1, and the distance of a vertex to itself is 0.
     *
     * Runtime: O(n + m)
     * @throws IllegalArgumentException if [v1] or [v2] is not in the graph.
     * @return The distance between [v1] and [v2].
     */
    fun <V> distance(g: SimpleGraph<V>, v1: V, v2: V): Int = shortestPath(g, v1, v2).size - 1

    /**
     * Calculates all cut-vertices and all bridge-edges of the given graph [g].
     *
     * A vertex v is a cut-vertex if the graph has more connected components after v is removed from it.
     * An edge (v1,v2) is a bridge-edge if the graph has more connected components after (v1,v2) is removed from it.
     *
     * Uses an algorithm that calculates a dfs-tree and a chain decomposition based on that tree. The cut-vertices
     * and bridge-edges can then easily be determined from the chain decomposition.
     *
     * Runtime: O(n + m)
     * @return A [Pair] containing a set of all cut-vertices and a set of all bridge-edges.
     */
    fun <V> cutVerticesAndBridgeEdges(g: SimpleGraph<V>): Pair<Set<V>, Set<Pair<V, V>>> {
        val cutVertices = HashSet<V>()
        val bridgeEdges = HashSet<Pair<V, V>>()
        val remaining = g.V.toHashSet()
        // make sure we visit all connected components
        while (remaining.isNotEmpty()) {
            // choose starting vertex
            val first = remaining.first()

            // create dfs tree
            val order = LinkedList<V>()
            val parent = HashMap<V, V>()
            val stack = LinkedList<V>()
            val visited = HashSet<V>()
            stack.addFirst(first)
            parent[first] = first
            while (stack.isNotEmpty()) {
                val v = stack.removeFirst()
                if (v !in visited) {
                    visited.add(v)
                    remaining.remove(v)
                    order.addLast(v)
                    for (n in g.neighbors(v)) {
                        if (n in visited) continue
                        stack.addFirst(n)
                        parent[n] = v
                    }
                }
            }
            visited.clear()

            // calculate chain decomposition and all cut-vertices and bridge-edges for this connected component
            val chainVisited = HashSet<V>()
            val remainingEdges = order.toHashSet()
            remainingEdges.remove(first)
            for (v in order) {
                for (n in g.neighbors(v)) {
                    // check that (v,n) is not part of the tree and that n has not been visited yet
                    if (n in chainVisited || parent[v] == n || parent[n] == v) continue
                    // at this point n has been discovered by dfs after v and v must be an ancestor of n
                    // traverse the parent edges until a visited vertex is found or until we reach v
                    chainVisited.add(v)
                    var next = n
                    while (next !in chainVisited) {
                        chainVisited.add(next)
                        remainingEdges.remove(next)
                        next = parent[next]!!
                    }
                    // v is a cut-vertex if the chain reaches it and v is not the first vertex
                    if (v == next && v != first) {
                        cutVertices.add(v)
                    }
                }
            }
            chainVisited.clear()
            // all edges of the tree that are not part of a chain are bridge-edges
            // their endpoints are cut-vertices unless they have degree 1
            for (v in remainingEdges) {
                bridgeEdges.add(Pair(v, parent[v]!!))
                if (g.degreeOf(parent[v]!!) > 1) cutVertices.add(parent[v]!!)
                if (g.degreeOf(v) > 1) cutVertices.add(v)
            }
        }
        return Pair(cutVertices, bridgeEdges)
    }

    /**
     * Checks if the given graph [g] is biconnected.
     *
     * A graph is biconnected if it is connected and if it is still connected after removing any one vertex.
     *
     * Runtime: O(n + m)
     * @throws IllegalArgumentException if [g] is empty.
     * @return True if the graph is biconnected, False otherwise.
     */
    fun <V> isBiconnected(g: SimpleGraph<V>): Boolean {
        if (g.n == 0) throw IllegalArgumentException("Connectivity for empty graph is ambiguous, so exception for good measure")
        val (cV, bE) = cutVerticesAndBridgeEdges(g)
        return cV.isEmpty() && isConnected(g)
    }

    /**
     * Checks if the given graph [g] is 2-edge-connected.
     *
     * A graph is 2-edge-connected if it is connected and if it is still connected after removing any one edge.
     *
     * Runtime: O(n + m)
     * @throws IllegalArgumentException if [g] is empty.
     * @return True if the graph is 2-edge-connected, False otherwise.
     */
    fun <V> is2EdgeConnected(g: SimpleGraph<V>): Boolean {
        if (g.n == 0) throw IllegalArgumentException("Connectivity for empty graph is ambiguous, so exception for good measure")
        val (cV, bE) = cutVerticesAndBridgeEdges(g)
        return bE.isEmpty() && isConnected(g)
    }
}
