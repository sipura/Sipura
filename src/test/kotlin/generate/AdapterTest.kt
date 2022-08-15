package generate

import graphs.SimpleGraph
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
}