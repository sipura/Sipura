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
}