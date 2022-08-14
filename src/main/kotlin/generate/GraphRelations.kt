package generate

import graphs.SimpleGraph
import utils.SetTheory.intersection

object GraphRelations {

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
     *
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

    fun <V> isSubgraph(sub: SimpleGraph<V>, g: SimpleGraph<V>): Boolean {
        if (sub.V.any { it !in g.V }) return false
        for ((v1, v2) in sub.edgeIterator()) {
            if (!g.hasEdge(v1, v2)) return false
        }

        return true
    }

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
}