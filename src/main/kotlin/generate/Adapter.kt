package generate

import graphs.SimpleGraph
import org.jgrapht.graph.DefaultEdge

object Adapter {

    fun <V> toJGraphT(g: SimpleGraph<V>): org.jgrapht.graph.SimpleGraph<V, DefaultEdge> {
        val res = org.jgrapht.graph.SimpleGraph<V, DefaultEdge>(DefaultEdge::class.java)
        for (v in g.V) {
            res.addVertex(v)
        }
        for ((v1, v2) in g.edgeIterator()) {
            res.addEdge(v1, v2)
        }

        return res
    }

    fun <V> fromJGraphT(g: org.jgrapht.graph.SimpleGraph<V, DefaultEdge>): SimpleGraph<V> {
        val res = SimpleGraph<V>()

        for (v in g.vertexSet()) {
            res.addVertex(v)
        }
        for (edge in g.edgeSet()) {
            res.addEdge(g.getEdgeSource(edge), g.getEdgeTarget(edge))
        }

        return res
    }

}