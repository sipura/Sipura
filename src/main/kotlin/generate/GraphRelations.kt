package generate

import graphs.SimpleGraph
import utils.SetTheory.intersection
import utils.SetTheory.isSubset

/**
 * Provides utility methods that arise when graphs stand in a relationship to each other,
 * for example if one graph is a subgraph or the complement of another graph.
 */
object GraphRelations {

    /**
     * @return The complement graph of [g]. This means that the complement graph has the same vertices as [g],
     * and two vertices are connected in the complement graph if and only if they are not connected in [g].
     */
    fun <V> complementGraph(g: SimpleGraph<V>): SimpleGraph<V> {
        val complement = SimpleGraph<V>()
        for (v in g.V) complement.addVertex(v)
        for (v1 in g.V) {
            for (v2 in g.V) {
                if (v1 != v2 && !g.hasEdge(v1, v2)) {
                    complement.addEdge(v1, v2)
                }
            }
        }
        return complement
    }

    /**
     * @throws IllegalArgumentException if [S] is not a subset of the vertices [g].
     *
     * @return The subgraph of [g] induced by the vertices in [S]. This means that if an edge is connected to a
     * vertex outside of [S] it is not included in the subgraph.
     */
    fun <V> inducedSubgraph(g: SimpleGraph<V>, S: Set<V>): SimpleGraph<V> {
        val subgraph = SimpleGraph<V>()
        for (s in S) {
            if (s !in g.V) throw IllegalArgumentException("S must be subset of the vertices")
            subgraph.addVertex(s)
        }

        for (s in S) {
            for (nb in intersection(g.neighbors(s), S)) {
                if (g.hasEdge(s, nb)) subgraph.addEdge(s, nb)
            }
        }
        return subgraph
    }

    /**
     * @return True if [sub] is a subgraph of [g], false otherwise.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Glossary_of_graph_theory#subgraph</a>
     */
    fun <V> isSubgraph(sub: SimpleGraph<V>, g: SimpleGraph<V>): Boolean {
        if (!isSubset(sub.V, g.V)) return false
        if (sub.m > g.m) return false
        for ((v1, v2) in sub.edgeIterator()) {
            if (!g.hasEdge(v1, v2)) return false
        }

        return true
    }

    /**
     * @return The graph that is the union of [g1] and [g2].
     */
    fun <V> union(g1: SimpleGraph<V>, g2: SimpleGraph<V>): SimpleGraph<V> {
        val res = SimpleGraph<V>()
        g1.V.forEach { res.addVertex(it) }
        g2.V.forEach { res.addVertex(it) }

        for ((v1, v2) in g1.edgeIterator()) {
            res.addEdge(v1, v2)
        }

        for ((v1, v2) in g2.edgeIterator()) {
            res.addEdge(v1, v2)
        }

        return res
    }

    /**
     * @return The graph that is the disjoint union of [g1] and [g2].
     */
    fun <V1, V2> disjointUnion(g1: SimpleGraph<V1>, g2: SimpleGraph<V2>): SimpleGraph<Int> {
        val res = SimpleGraph<Int>()
        val map1 = HashMap<V1, Int>()
        val map2 = HashMap<V2, Int>()
        var nextIntVertex = 0
        for (v1 in g1.V) {
            map1[v1] = nextIntVertex
            res.addVertex(nextIntVertex)
            nextIntVertex++
        }
        for (v2 in g2.V) {
            map2[v2] = nextIntVertex
            res.addVertex(nextIntVertex)
            nextIntVertex++
        }
        g1.forEachEdge { v1, v2 -> res.addEdge(map1[v1]!!, map1[v2]!!) }
        g2.forEachEdge { v1, v2 -> res.addEdge(map2[v1]!!, map2[v2]!!) }
        return res
    }

    /**
     * @return The graph that is the union of [g1] and [g2] with their vertices mapped by [map1] and [map2] respectively.
     */
    fun <V1, V2, V3> mappedUnion(
        g1: SimpleGraph<V1>,
        map1: Map<V1, V3>,
        g2: SimpleGraph<V2>,
        map2: Map<V2, V3>
    ): SimpleGraph<V3> {
        val res = SimpleGraph<V3>()
        for (v in g1.V) {
            if (map1.containsKey(v)) {
                res.addVertex(map1[v]!!)
            }
        }
        for (v in g2.V) {
            if (map2.containsKey(v)) {
                res.addVertex(map2[v]!!)
            }
        }
        g1.forEachEdge { v1, v2 ->
            if (map1.containsKey(v1) && map1.containsKey(v2)) {
                res.addEdge(map1[v1]!!, map1[v2]!!)
            }
        }
        g2.forEachEdge { v1, v2 ->
            if (map2.containsKey(v1) && map2.containsKey(v2)) {
                res.addEdge(map2[v1]!!, map2[v2]!!)
            }
        }
        return res
    }
}
