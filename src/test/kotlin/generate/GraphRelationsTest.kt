package generate

import generate.Factory.createCycle
import generate.Factory.createLine
import generate.GraphRelations.inducedSubgraph
import generate.GraphRelations.isSubgraph
import graphs.SimpleGraph
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class GraphRelationsTest {

    @Nested
    internal inner class Complement {

        @Test
        fun `complement of triangle is empty`() {
            val triangle = createCycle(3)
            val complement = GraphRelations.complementGraph(triangle)
            assertEquals(3, complement.n)
            assertEquals(0, complement.m)
        }

        @Test
        fun `complement of line4 is one edge and one isolated vertex`() {
            val g = createLine(3)
            val complement = GraphRelations.complementGraph(g)
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

    @Nested
    internal inner class IsSubgraph {

        @Test
        fun `line4 is subgraph of line7`() {
            val line4 = createLine(4)
            val line7 = createLine(7)
            assertTrue { isSubgraph(line4,line7) }
        }

        @Test
        fun `triangle is not subgraph of line4`() {
            val line4 = createCycle(3)
            val line7 = createLine(4)
            assertFalse { isSubgraph(line4,line7) }
        }

        @Test
        fun `two isolated vertices are subgraph of single edge`() {
            val twoSingles = SimpleGraph<Int>()
            twoSingles.addVertex(1)
            twoSingles.addVertex(2)

            val line2 = createLine(2)
            assertTrue { isSubgraph(twoSingles,line2) }
        }

        @Test
        fun `single edge is not subgraph of two isolated vertices`() {
            val line2 = createLine(2)

            val twoSingles = SimpleGraph<Int>()
            twoSingles.addVertex(1)
            twoSingles.addVertex(2)

            assertFalse { isSubgraph(line2, twoSingles) }
        }
    }


    @Nested
    internal inner class Union {

        @Test
        fun `union of two disjoint lines`() {
            val line123 = createLine(3)
            val line45 = SimpleGraph<Int>()
            line45.addVertex(4)
            line45.addVertex(5)
            line45.addEdge(4, 5)
            val res = GraphRelations.union(line123, line45)

            assertEquals(5, res.n)
            assertEquals(3, res.m)
        }
    }
}