package alg

import Samples
import generate.Factory.createBipartite
import generate.Factory.createCompleteGraph
import generate.Factory.createCycle
import generate.Factory.createLine
import generate.Factory.createStar
import graphs.SimpleGraph
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class GraphPropertyTest {

    @Nested
    internal inner class IsTree {

        @Test
        fun `one isolated vertex is a (trivial) tree`() {
            val g = SimpleGraph<Int>()
            g.addVertex(7)
            kotlin.test.assertTrue { GraphProperty.isTree(g) }
        }

        @Test
        fun `triangle is not a tree`() {
            assertFalse { GraphProperty.isTree(createCycle(3)) }
        }

        @Test
        fun `path5 is a tree`() {
            kotlin.test.assertTrue { GraphProperty.isTree(createLine(5)) }
        }

        @Test
        fun `star4 with one isolated vertex is not a tree`() {
            assertFalse { GraphProperty.isTree(Samples.star4Plus1IsolatedVertex()) }
        }

        @Test
        fun `bipartite graph of size (4, 7) is not a tree`() {
            assertFalse { GraphProperty.isTree(createBipartite(4, 7)) }
        }
    }

    @Nested
    internal inner class IsComplete {

        @Test
        fun `triangle is complete`() {
            assertTrue { GraphProperty.isComplete(createCompleteGraph(3)) }
        }

        @Test
        fun `path4 is not complete`() {
            assertFalse { GraphProperty.isComplete(createLine(4)) }
        }

        @Test
        fun `complete graph of size 10 is complete`() {
            assertTrue { GraphProperty.isComplete(createCompleteGraph(10)) }
        }
    }

    @Nested
    internal inner class IsAcyclic {

        @Test
        fun `empty graph is acyclic`() {
            assertTrue { GraphProperty.isAcyclic(SimpleGraph<Int>()) }
        }

        @Test
        fun `graph with single connectivity component is acyclic`() {
            assertTrue { GraphProperty.isAcyclic(createLine(5)) }
        }

        @Test
        fun `graph with single connectivity component is not acyclic`() {
            val g = createLine(5)
            g.addEdge(5, 1)
            assertFalse { GraphProperty.isAcyclic(g) }
        }

        @Test
        fun `graph with multiple connectivity components is acyclic`() {
            assertTrue { GraphProperty.isAcyclic(Samples.star4Plus1IsolatedVertex()) }
        }

        @Test
        fun `graph with multiple connectivity components is not acyclic if one of them is not acyclic`() {
            val g = Samples.star4Plus1IsolatedVertex()
            g.addEdge(2, 3)
            assertFalse { GraphProperty.isAcyclic(g) }
        }

        @Test
        fun `graph with multiple connectivity components is not acyclic if all of them are not acyclic`() {
            val g = Samples.star4Plus1IsolatedVertex()
            g.addEdge(2, 3)
            g.addVertex(6)
            g.addVertex(7)
            g.addEdge(5, 6)
            g.addEdge(5, 7)
            g.addEdge(6, 7)
            assertFalse { GraphProperty.isAcyclic(g) }
        }
    }

    @Nested
    internal inner class IsKRegular {

        @Test
        fun `path5 is not 2-regular`() {
            assertFalse { GraphProperty.isKRegular(createLine(5), 2) }
        }

        @Test
        fun `normal cube is 3-regular aka cubic`() {
            val cube = SimpleGraph<Int>()
            (1..8).forEach { cube.addVertex(it) }

            cube.addEdge(1, 2)
            cube.addEdge(1, 3)
            cube.addEdge(2, 4)
            cube.addEdge(3, 4)

            cube.addEdge(1, 5)
            cube.addEdge(2, 6)
            cube.addEdge(3, 7)
            cube.addEdge(4, 8)

            cube.addEdge(5, 6)
            cube.addEdge(5, 7)
            cube.addEdge(6, 8)
            cube.addEdge(7, 8)

            assertTrue { GraphProperty.isKRegular(cube, 3) }
        }

        @Test
        fun `cycle5 is 2-regular`() {
            val g = createCycle(5)
            assertTrue { GraphProperty.isKRegular(g, 2) }
        }

        @Test
        fun `throws exception for k equal -2`() {
            val g = createCycle(5)
            assertThrows<IllegalArgumentException> { GraphProperty.isKRegular(g, -2) }
        }
    }

    @Nested
    internal inner class IsBipartite {

        @Test
        fun `path5 is bipartite`() {
            assertTrue { GraphProperty.isBipartite(createLine(5)) }
        }

        @Test
        fun `cycle4 is not bipartite`() {
            assertTrue { GraphProperty.isBipartite(createCycle(4)) }
        }

        @Test
        fun `cycle5 is not bipartite`() {
            assertFalse { GraphProperty.isBipartite(createCycle(5)) }
        }

        @Test
        fun `star4 and 1 isolated is bipartite`() {
            assertTrue { GraphProperty.isBipartite(Samples.star4Plus1IsolatedVertex()) }
        }

        @Test
        fun `complete7 is not bipartite`() {
            assertFalse { GraphProperty.isBipartite(createCompleteGraph(8)) }
        }

        @Test
        fun `single vertex is bipartite`() {
            assertTrue { GraphProperty.isBipartite(createLine(1)) }
        }

        @Test
        fun `bipartite graph of size (10, 13) is bipartite (surprisingly)`() {
            assertTrue { GraphProperty.isBipartite(createBipartite(10, 13)) }
        }
    }

    @Nested
    internal inner class IsCubic {

        @Test
        fun `path5 is not cubic`() {
            assertFalse { GraphProperty.isCubic(createLine(5)) }
        }

        @Test
        fun `normal cube is cubic`() {
            val cube = SimpleGraph<Int>()
            (1..8).forEach { cube.addVertex(it) }

            cube.addEdge(1, 2)
            cube.addEdge(1, 3)
            cube.addEdge(2, 4)
            cube.addEdge(3, 4)

            cube.addEdge(1, 5)
            cube.addEdge(2, 6)
            cube.addEdge(3, 7)
            cube.addEdge(4, 8)

            cube.addEdge(5, 6)
            cube.addEdge(5, 7)
            cube.addEdge(6, 8)
            cube.addEdge(7, 8)

            assertTrue { GraphProperty.isCubic(cube) }
        }

        @Test
        fun `complete4 is cubic`() {
            val g = SimpleGraph<Int>()
            (1..4).forEach { g.addVertex(it) }

            g.addEdge(1, 2)
            g.addEdge(1, 3)
            g.addEdge(1, 4)
            g.addEdge(2, 3)
            g.addEdge(2, 4)
            g.addEdge(3, 4)

            assertTrue { GraphProperty.isCubic(g) }
        }

        @Test
        fun `empty graph is not cubic`() {
            assertThrows<IllegalArgumentException> { GraphProperty.isCubic(SimpleGraph<Int>()) }
        }
    }

    @Nested
    internal inner class HIndex {
        @Test
        fun star4() {
            assertEquals(1, GraphProperty.hIndex(createStar(4)))
        }

        @Test
        fun path5() {
            assertEquals(2, GraphProperty.hIndex(createLine(5)))
        }

        @Test
        fun clique8() {
            assertEquals(7, GraphProperty.hIndex(createCompleteGraph(8)))
        }

        @Test
        fun emptyGraph() {
            assertThrows<IllegalArgumentException> { GraphProperty.hIndex(SimpleGraph<Int>()) }
        }
    }

    @Nested
    internal inner class IsTriangleFree {

        @Test
        fun `triangle is not triangle free`() {
            val g = SimpleGraph<Int>()
            g.addVertex(1)
            g.addVertex(2)
            g.addVertex(3)
            g.addEdge(1, 2)
            g.addEdge(1, 3)
            g.addEdge(2, 3)
            assertFalse { GraphProperty.isTriangleFree(g) }
        }

        @Test
        fun `path5 is triangle free`() {
            assertTrue { GraphProperty.isTriangleFree(createLine(5)) }
        }

        @Test
        fun `empty graph is triangle free`() {
            assertTrue { GraphProperty.isTriangleFree(SimpleGraph<Int>()) }
        }

        @Test
        fun `star4 is triangle free`() {
            assertTrue { GraphProperty.isTriangleFree(createStar(4)) }
        }

        @Test
        fun `complete4 is not triangle free`() {
            assertFalse { GraphProperty.isTriangleFree(createCompleteGraph(4)) }
        }

        @Test
        fun `bipartite graph is triangle free`() {
            assertTrue { GraphProperty.isTriangleFree(createBipartite(10, 13)) }
        }

        @Test
        fun `cube is not triangle free`() {
            val cube = SimpleGraph<Int>()
            (1..8).forEach { cube.addVertex(it) }

            cube.addEdge(1, 2)
            cube.addEdge(1, 3)
            cube.addEdge(2, 4)
            cube.addEdge(3, 4)

            cube.addEdge(1, 5)
            cube.addEdge(2, 6)
            cube.addEdge(3, 7)
            cube.addEdge(4, 8)

            cube.addEdge(5, 6)
            cube.addEdge(5, 7)
            cube.addEdge(6, 8)
            cube.addEdge(7, 8)

            assertTrue { GraphProperty.isTriangleFree(cube) }
        }
    }
}
