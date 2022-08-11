package generate

import graphs.SimpleGraph
import utils.Utils.intersection

object GraphModifications {

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

}