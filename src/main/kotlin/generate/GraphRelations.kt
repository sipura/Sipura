package generate

import alg.Connectivity.numberOfConnectedComponents
import graphs.SimpleGraph
import utils.SetTheory.intersection
import utils.SetTheory.isSubset

/**
 * Provides utility methods that arise when graphs stand in a relationship to each other,
 * for example if one graph is a subgraph or the complement of another graph.
 */
object GraphRelations {

    /**
     * Creates the complement graph of [g].
     *
     * The complement graph of [g] is the graph that contains the same vertices as [g] and any pair of vertices v1,v2
     * is connected if and only if they are not connected in [g].
     *
     * @return The complement graph of [g].
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
     * Creates the subgraph of [g] induced by the vertices in [S].
     *
     * The subgraph of [g] that is induced by [S]
     * is the smallest graph, i.e. the graph with the fewest vertices and edges, that contains all vertices from [g]
     * that are in [S] and contains all edges (v1, v2) of [g] where v1 and v2 are both in [S].
     *
     * @throws IllegalArgumentException if [S] is not a subset of the vertices [g].
     *
     * @return The subgraph of [g] induced by the vertices in [S].
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
     * Checks if [sub] is a subgraph of [g]. This can only be true if the vertices used in [sub] are the same objects
     * that are used as vertices in [g].
     *
     * @return True if [sub] is a subgraph of [g], false otherwise.
     *
     * @see <a href=https://en.wikipedia.org/wiki/Glossary_of_graph_theory#subgraph>Wikipedia: Subgraph</a>
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
     * Creates the union graph of the two graphs [g1] and [g2]. This is done by simply adding all vertices and edges
     * from the two graphs to the new union graph. If both graphs contain the same object as a vertex then those two
     * vertices will be treated as the same vertex in the union graph.
     *
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
     * Creates the disjoint union of the two graphs [g1] and [g2]. This is done by mapping the vertices of both graphs
     * to unique Integers and using those as vertices in the union graph. So even if the vertex sets of the two graphs
     * are not disjoint they will be treated like they are.
     *
     * @return The [SimpleGraph] that is the disjoint union of [g1] and [g2] with Integers as vertices.
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
     * Creates the union of the two graphs [g1] and [g2] by using the provided maps [map1] and [map2] as the vertex
     * mapping between the two graphs and the union graph. The maps may map two or more vertices to the same vertex.
     * They may also not contain a mapping for some vertices. In that case those vertices and their incident edges will
     * not be part of the union graph.
     * @param map1 A [Map] mapping some or all vertices of [g1] to vertices in the union graph.
     * @param map2 A [Map] mapping some or all vertices of [g1] to vertices in the union graph.
     *
     * @return The [SimpleGraph] that is the union of [g1] and [g2] with their vertices mapped by [map1] and [map2] respectively.
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

    /**
     * Checks a bunch of conditions that are necessary for two graphs to be isomorphic. If any of these conditions
     * are not met then the graphs cannot be isomorphic.
     *
     * Graph isomorphism is a hard problem which is why we provide a fast check that can often be used
     * to find out if two graphs are not isomorphic. If this function returns true then the graphs may still be
     * non-isomorphic but if it returns false then the graphs are definitely not isomorphic.
     *
     * @return True if the graphs may be isomorphic, false if they are definitely not isomorphic.
     */
    fun <V> checkNecessaryConditionsForIsomorphism(g1: SimpleGraph<V>, g2: SimpleGraph<V>): Boolean {
        return g1.n == g2.n &&
            g1.m == g2.m &&
            g1.V.map { g1.degreeOf(it) }.sorted() == g2.V.map { g2.degreeOf(it) }.sorted() &&
            numberOfConnectedComponents(g1) == numberOfConnectedComponents(g2)
    }
}
