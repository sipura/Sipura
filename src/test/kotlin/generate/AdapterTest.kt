package generate

import com.google.common.graph.GraphBuilder
import graphs.SimpleGraph
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultEdge
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class AdapterTest {

    @Nested
    internal inner class ToJGraphT {

        @Test
        fun `convert empty graph`() {
            val g = SimpleGraph<Int>()
            val jGraphTGraph = Adapter.toJGraphT(g)

            assertEquals(0, jGraphTGraph.vertexSet().size)
            assertEquals(0, jGraphTGraph.edgeSet().size)
        }

        @Test
        fun `convert star5`() {
            val g = Factory.createStar(5)
            val jGraphTGraph = Adapter.toJGraphT(g)

            assertEquals(5, jGraphTGraph.vertexSet().size)
            assertEquals(4, jGraphTGraph.edgeSet().size)
            for (i in 2..5) assertTrue { jGraphTGraph.containsEdge(1, i) }
        }
    }

    @Nested
    internal inner class FromJGraphT {

        @Test
        fun `convert empty graph`() {
            val jGraphTGraph = org.jgrapht.graph.SimpleGraph<Int, DefaultEdge>(DefaultEdge::class.java)
            val g = Adapter.fromJGraphT(jGraphTGraph)

            assertEquals(0, g.n)
            assertEquals(0, g.m)
        }

        @Test
        fun `convert star5`() {
            val jGraphTGraph = org.jgrapht.graph.SimpleGraph<Int, DefaultEdge>(DefaultEdge::class.java)
            for (v in 2..5) Graphs.addEdgeWithVertices(jGraphTGraph, 1, v)

            val g = Adapter.fromJGraphT(jGraphTGraph)
            assertEquals(5, g.n)
            assertEquals(4, g.m)
            for (i in 2..5) assertTrue { g.hasEdge(1, i) }
        }
    }

    @Nested
    internal inner class ToGuava {

        @Test
        fun `convert empty graph`() {
            val g = SimpleGraph<Int>()
            val guavaGraph = Adapter.toGuava(g)

            assertEquals(0, guavaGraph.nodes().size)
            assertEquals(0, guavaGraph.edges().size)
        }

        @Test
        fun `convert star5`() {
            val g = Factory.createStar(5)
            val guavaGraph = Adapter.toGuava(g)

            assertEquals(5, guavaGraph.nodes().size)
            assertEquals(4, guavaGraph.edges().size)
            for (i in 2..5) assertTrue { guavaGraph.hasEdgeConnecting(1, i) }
        }
    }

    /**
     * Most graph-stuff from Guava is marked as Beta :/
     */
    @Nested
    internal inner class FromGuava {

        @Test
        fun `convert empty graph`() {
            val guavaGraph: com.google.common.graph.MutableGraph<Int> = GraphBuilder.undirected().build()
            val g = Adapter.fromGuava(guavaGraph)

            assertEquals(0, g.n)
            assertEquals(0, g.m)
        }

        @Test
        fun `convert star5`() {
            val guavaGraph: com.google.common.graph.MutableGraph<Int> = GraphBuilder.undirected().build()
            for (v in 2..5) guavaGraph.putEdge(1, v)

            val g = Adapter.fromGuava(guavaGraph)
            assertEquals(5, g.n)
            assertEquals(4, g.m)
            for (i in 2..5) assertTrue { g.hasEdge(1, i) }
        }
    }
}
