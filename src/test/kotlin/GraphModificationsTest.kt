import Factory.createCycle
import Factory.createLine
import GraphModifications.inducedSubgraph
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class GraphModificationsTest {

    @Nested
    internal inner class Complement {

        @Test
        fun `complement of triangle is empty`() {
            val triangle = createCycle(3)
            val complement = GraphModifications.complementGraph(triangle)
            assertEquals(3, complement.n)
            assertEquals(0, complement.m)
        }

        @Test
        fun `complement of line4 is one edge and one isolated vertex`() {
            val g = createLine(3)
            val complement = GraphModifications.complementGraph(g)
            assertEquals(3, complement.n)
            assertEquals(1, complement.m)
            assertTrue { complement.hasEdge(1, 3) }
        }

    }

    @Nested
    internal inner class InducedSubgraph {

        @Test
        fun `throws error if not a subset of vertices`() {
            assertThrows<IllegalArgumentException> { inducedSubgraph(createLine(5), setOf(3, 8)) }
        }

        @Test
        fun `induced subgraph of two vertices in triangle is just one edge`() {
            val triangle = createCycle(3)
            val sub = inducedSubgraph(triangle, setOf(1, 2))
            assertEquals(2, sub.n)
            assertEquals(1, sub.m)
            assertTrue { sub.hasEdge(1, 2) }
        }

    }

}