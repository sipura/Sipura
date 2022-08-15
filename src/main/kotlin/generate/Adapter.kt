package generate

import com.google.common.graph.GraphBuilder

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

    /**
     * Most graph-stuff from Guava is marked as Beta :/
     */
    fun <V> toGuava(g: SimpleGraph<V>): com.google.common.graph.Graph<V> {
        val res: com.google.common.graph.MutableGraph<V> = GraphBuilder.undirected().build()
        for (v in g.V) {
            res.addNode(v)
        }
        for ((v1, v2) in g.edgeIterator()) {
            res.putEdge(v1, v2)
        }

        return res
    }

    /**
     * Most graph-stuff from Guava is marked as Beta :/
     */
    fun <V> fromGuava(g: com.google.common.graph.Graph<V>): SimpleGraph<V> {
        val res = SimpleGraph<V>()

        for (v in g.nodes()) {
            res.addVertex(v)
        }
        for (edge in g.edges()) {
            res.addEdge(edge.nodeU(), edge.nodeV())
        }

        return res
    }

}