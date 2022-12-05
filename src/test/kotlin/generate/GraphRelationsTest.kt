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
            assertTrue { isSubgraph(line4, line7) }
        }

        @Test
        fun `triangle is not subgraph of line4`() {
            val line4 = createCycle(3)
            val line7 = createLine(4)
            assertFalse { isSubgraph(line4, line7) }
        }

        @Test
        fun `two isolated vertices are subgraph of single edge`() {
            val twoSingles = SimpleGraph<Int>()
            twoSingles.addVertex(1)
            twoSingles.addVertex(2)

            val line2 = createLine(2)
            assertTrue { isSubgraph(twoSingles, line2) }
        }

        @Test
        fun `single edge is not subgraph of two isolated vertices`() {
            val line2 = createLine(2)

            val twoSingles = SimpleGraph<Int>()
            twoSingles.addVertex(1)
            twoSingles.addVertex(2)

            assertFalse { isSubgraph(line2, twoSingles) }
        }

        @Test
        fun `triangle123 is not subgraph of triangle234`() {
            val triangle123 = createCycle(3)

            val triangle234 = SimpleGraph<Int>()
            for (v in 2..4) triangle234.addVertex(v)
            triangle234.addEdge(2, 3)
            triangle234.addEdge(2, 4)
            triangle234.addEdge(3, 4)

            assertFalse { isSubgraph(triangle123, triangle234) }
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

    @Nested
    internal inner class DisjointUnion {

        @Test
        fun `disjoint union of two graphs with overlapping vertices`() {
            val line1 = createLine(3)
            val line2 = createLine(4)
            val res = GraphRelations.disjointUnion(line1, line2)

            assertEquals(line1.n + line2.n, res.n)
            assertEquals(line1.m + line2.m, res.m)
        }

        @Test
        fun `disjoint union of two graphs with different vertex types`() {
            val line1 = createLine(3) // has vertex type Int
            val line2 = SimpleGraph<Float>() // has vertex type Float
            line2.addVertex(1f)
            line2.addVertex(2f)
            line2.addVertex(3f)
            line2.addVertex(4f)
            line2.addEdge(1f, 2f)
            line2.addEdge(2f, 3f)
            line2.addEdge(3f, 4f)
            val res = GraphRelations.disjointUnion(line1, line2)

            assertEquals(line1.n + line2.n, res.n)
            assertEquals(line1.m + line2.m, res.m)
        }
    }

    @Nested
    internal inner class MappedUnion {

        @Test
        fun `mapped union of two graphs with disjoint mappings`() {
            val g1 = createLine(3)
            val map1 = HashMap<Int, Int>()
            map1[1] = 10; map1[2] = 20; map1[3] = 30
            val g2 = createLine(3)
            val map2 = HashMap<Int, Int>()
            map2[1] = 100; map2[2] = 200; map2[3] = 300
            val res = GraphRelations.mappedUnion(g1, map1, g2, map2)

            assertEquals(g1.n + g2.n, res.n)
            assertEquals(g1.m + g2.m, res.m)
            for (v in setOf(10, 20, 30, 100, 200, 300)) {
                assertTrue { res.contains(v) }
            }
            assertTrue { res.hasEdge(10, 20) }
            assertTrue { res.hasEdge(20, 30) }
            assertTrue { res.hasEdge(100, 200) }
            assertTrue { res.hasEdge(200, 300) }
        }

        @Test
        fun `mapped union of two graphs with partially disjoint mappings`() {
            val g1 = createLine(3)
            val map1 = HashMap<Int, Int>()
            map1[1] = 10; map1[2] = 20; map1[3] = 30
            val g2 = createLine(3)
            val map2 = HashMap<Int, Int>()
            map2[1] = 10; map2[2] = 200; map2[3] = 300
            val res = GraphRelations.mappedUnion(g1, map1, g2, map2)

            assertEquals(g1.n + g2.n - 1, res.n)
            assertEquals(g1.m + g2.m, res.m)
            for (v in setOf(10, 20, 30, 200, 300)) {
                assertTrue { res.contains(v) }
            }
            assertTrue { res.hasEdge(10, 20) }
            assertTrue { res.hasEdge(20, 30) }
            assertTrue { res.hasEdge(10, 200) }
            assertTrue { res.hasEdge(200, 300) }
        }

        @Test
        fun `mapped union of two graphs with incomplete mappings`() {
            val g1 = createLine(3)
            val map1 = HashMap<Int, Int>()
            map1[1] = 10; map1[2] = 20
            val g2 = createLine(3)
            val map2 = HashMap<Int, Int>()
            map2[1] = 100; map2[3] = 300
            val res = GraphRelations.mappedUnion(g1, map1, g2, map2)

            assertEquals(4, res.n)
            assertEquals(1, res.m)
            for (v in setOf(10, 20, 100, 300)) {
                assertTrue { res.contains(v) }
            }
            assertTrue { res.hasEdge(10, 20) }
        }
    }

    @Nested
    internal inner class NecessaryConditionsForGraphIsomorphism {

        @Test
        fun `two graphs with different number of vertices are not isomorphic`() {
            val g1 = createLine(3)
            val g2 = createLine(4)
            assertFalse { GraphRelations.checkNecessaryConditionsForIsomorphism(g1, g2) }
        }

        @Test
        fun `two graphs with different number of edges are not isomorphic`() {
            val g1 = createLine(3)
            val g2 = createLine(3)
            g2.addEdge(1, 3)
            assertFalse { GraphRelations.checkNecessaryConditionsForIsomorphism(g1, g2) }
        }

        @Test
        fun `two graphs with the same vertices and edges are isomorphic`() {
            val g1 = createLine(3)
            val g2 = createLine(3)
            assertTrue { GraphRelations.checkNecessaryConditionsForIsomorphism(g1, g2) }
        }

        @Test
        fun `two paths of length 3 with different vertices are isomorphic`() {
            val g1 = createLine(3)
            val g2 = SimpleGraph<Int>()
            for (v in 5..7) {
                g2.addVertex(v)
            }
            g2.addEdge(5, 6)
            g2.addEdge(6, 7)
            assertTrue { GraphRelations.checkNecessaryConditionsForIsomorphism(g1, g2) }
        }

        @Test
        fun `two graphs with the same vertices but different degrees are not isomorphic`() {
            val g1 = createLine(3)
            val g2 = createLine(3)
            g2.addEdge(1, 2)
            g2.addEdge(2, 3)
            g2.addEdge(1, 3)
            assertFalse { GraphRelations.checkNecessaryConditionsForIsomorphism(g1, g2) }
        }
    }
}
