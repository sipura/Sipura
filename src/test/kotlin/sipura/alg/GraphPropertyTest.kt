package sipura.alg

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import sipura.Samples
import sipura.generate.Factory.createBipartiteGraph
import sipura.generate.Factory.createCompleteGraph
import sipura.generate.Factory.createCycleGraph
import sipura.generate.Factory.createPathGraph
import sipura.generate.Factory.createSplitGraph
import sipura.generate.Factory.createStarGraph
import sipura.generate.GraphRelations
import sipura.graphs.SimpleGraph
import kotlin.math.max
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
            assertFalse { GraphProperty.isTree(createCycleGraph(3)) }
        }

        @Test
        fun `path5 is a tree`() {
            kotlin.test.assertTrue { GraphProperty.isTree(createPathGraph(5)) }
        }

        @Test
        fun `star4 with one isolated vertex is not a tree`() {
            assertFalse { GraphProperty.isTree(Samples.star4Plus1IsolatedVertex()) }
        }

        @Test
        fun `bipartite graph of size (4, 7) is not a tree`() {
            assertFalse { GraphProperty.isTree(createBipartiteGraph(4, 7)) }
        }

        @Test
        fun `empty graph is not a tree`() {
            assertFalse { GraphProperty.isTree(SimpleGraph<Int>()) }
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
            assertFalse { GraphProperty.isComplete(createPathGraph(4)) }
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
            assertTrue { GraphProperty.isAcyclic(createPathGraph(5)) }
        }

        @Test
        fun `graph with single connectivity component is not acyclic`() {
            val g = createPathGraph(5)
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
            assertFalse { GraphProperty.isKRegular(createPathGraph(5), 2) }
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
            val g = createCycleGraph(5)
            assertTrue { GraphProperty.isKRegular(g, 2) }
        }

        @Test
        fun `throws exception for k equal -2`() {
            val g = createCycleGraph(5)
            assertThrows<IllegalArgumentException> { GraphProperty.isKRegular(g, -2) }
        }
    }

    @Nested
    internal inner class IsBipartite {

        @Test
        fun `path5 is bipartite`() {
            assertTrue { GraphProperty.isBipartite(createPathGraph(5)) }
        }

        @Test
        fun `cycle4 is not bipartite`() {
            assertTrue { GraphProperty.isBipartite(createCycleGraph(4)) }
        }

        @Test
        fun `cycle5 is not bipartite`() {
            assertFalse { GraphProperty.isBipartite(createCycleGraph(5)) }
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
            assertTrue { GraphProperty.isBipartite(createPathGraph(1)) }
        }

        @Test
        fun `bipartite graph of size (10, 13) is bipartite (surprisingly)`() {
            assertTrue { GraphProperty.isBipartite(createBipartiteGraph(10, 13)) }
        }
    }

    @Nested
    internal inner class IsCubic {

        @Test
        fun `path5 is not cubic`() {
            assertFalse { GraphProperty.isCubic(createPathGraph(5)) }
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
            assertEquals(1, GraphProperty.hIndex(createStarGraph(4)))
        }

        @Test
        fun path5() {
            assertEquals(2, GraphProperty.hIndex(createPathGraph(5)))
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
            assertTrue { GraphProperty.isTriangleFree(createPathGraph(5)) }
        }

        @Test
        fun `empty graph is triangle free`() {
            assertTrue { GraphProperty.isTriangleFree(SimpleGraph<Int>()) }
        }

        @Test
        fun `star4 is triangle free`() {
            assertTrue { GraphProperty.isTriangleFree(createStarGraph(4)) }
        }

        @Test
        fun `complete4 is not triangle free`() {
            assertFalse { GraphProperty.isTriangleFree(createCompleteGraph(4)) }
        }

        @Test
        fun `bipartite graph is triangle free`() {
            assertTrue { GraphProperty.isTriangleFree(createBipartiteGraph(10, 13)) }
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

    @Nested
    internal inner class Diameter {

        @Test
        fun `diameter of star is 2`() {
            val g = createStarGraph(30)
            assertEquals(2, GraphProperty.diameter(g))
        }

        @Test
        fun `diameter of cycle graph is half half of n`() {
            val g = createCycleGraph(30)
            assertEquals(15, GraphProperty.diameter(g))
        }

        @Test
        fun `diameter of unconnected graph is -1`() {
            val g1 = createStarGraph(20)
            val g2 = createCycleGraph(10)
            val g = GraphRelations.disjointUnion(g1, g2)
            assertEquals(-1, GraphProperty.diameter(g))
        }

        @Test
        fun `diameter of empty graph is 0`() {
            val g = SimpleGraph<Int>()
            assertEquals(0, GraphProperty.diameter(g))
        }
    }

    @Nested
    internal inner class Degree {

        @Test
        fun `minDegree of cycle is 2`() {
            val g = createCycleGraph(10)
            assertEquals(2, GraphProperty.minDegree(g))
        }

        @Test
        fun `averageDegree of cycle is 2`() {
            val g = createCycleGraph(10)
            assertEquals(2f, GraphProperty.averageDegree(g))
        }

        @Test
        fun `minDegree of complete graph is n-1`() {
            val g = createCompleteGraph(20)
            assertEquals(19, GraphProperty.minDegree(g))
        }

        @Test
        fun `averageDegree of complete graph is n-1`() {
            val g = createCompleteGraph(20)
            assertEquals(19f, GraphProperty.averageDegree(g))
        }

        @Test
        fun `minDegree of empty graph leads to exception`() {
            val g = SimpleGraph<Int>()
            assertThrows<NoSuchElementException> { GraphProperty.minDegree(g) }
        }

        @Test
        fun `averageDegree of empty graph leads to exception`() {
            val g = SimpleGraph<Int>()
            assertThrows<NoSuchElementException> { GraphProperty.averageDegree(g) }
        }
    }

    @Nested
    internal inner class Degeneracy {

        /**
         * Asserts that the given degeneracy ordering [ordering] is correct for the given graph [g] assuming
         * that [g] has degeneracy [d].
         */
        private fun <V> assertOrderingCorrect(g: SimpleGraph<V>, d: Int, ordering: List<V>) {
            val indices = HashMap<V, Int>()
            ordering.forEachIndexed { index, v -> indices[v] = index }
            for (v in ordering) {
                assertTrue { d >= g.neighbors(v).count { indices[it]!! > indices[v]!! } }
            }
        }

        @Test
        fun `cycle graph has degeneracy of 2`() {
            val g = createCycleGraph(10)
            val (d, order) = GraphProperty.degeneracyOrdering(g)
            assertEquals(2, d)
            assertOrderingCorrect(g, d, order)
        }

        @Test
        fun `star graph has degeneracy of 1`() {
            val g = createStarGraph(15)
            val (d, order) = GraphProperty.degeneracyOrdering(g)
            assertEquals(1, d)
            assertOrderingCorrect(g, d, order)
        }

        @Test
        fun `complete graph has degeneracy of n-1`() {
            val g = createCompleteGraph(30)
            val (d, order) = GraphProperty.degeneracyOrdering(g)
            assertEquals(29, d)
            assertOrderingCorrect(g, d, order)
        }

        @Test
        fun `degeneracy of graph with multiple components is max degeneracy of components`() {
            val g1 = createCycleGraph(20)
            val g2 = createBipartiteGraph(10, 12)
            val g = GraphRelations.disjointUnion(g1, g2)
            val (d1, order1) = GraphProperty.degeneracyOrdering(g1)
            val (d2, order2) = GraphProperty.degeneracyOrdering(g2)
            val (d, order) = GraphProperty.degeneracyOrdering(g)
            assertEquals(max(d1, d2), d)
            assertOrderingCorrect(g1, d1, order1)
            assertOrderingCorrect(g2, d2, order2)
            assertOrderingCorrect(g, d, order)
        }

        @Test
        fun `degeneracyOrdering throws exception for empty graph`() {
            val g = SimpleGraph<Int>()
            assertThrows<NoSuchElementException> { GraphProperty.degeneracyOrdering(g) }
        }
    }

    @Nested
    internal inner class SplitGraphAndSplittance {

        @Test
        fun `empty graph has splittance 0 and is split graph`() {
            val g = SimpleGraph<Int>()
            assertEquals(0, GraphProperty.minSplittance(g))
            assertTrue { GraphProperty.isSplitGraph(g) }
        }

        @Test
        fun `graph with one vertex has splittance 0 and is split graph`() {
            val g = SimpleGraph<Int>()
            g.addVertex(1)
            assertEquals(0, GraphProperty.minSplittance(g))
            assertTrue { GraphProperty.isSplitGraph(g) }
        }

        @Test
        fun `independent set has splittance 0 and is split graph`() {
            val g = SimpleGraph<Int>()
            g.addVertex(1)
            g.addVertex(2)
            g.addVertex(3)
            g.addVertex(4)
            g.addVertex(5)
            g.addVertex(6)
            assertEquals(0, GraphProperty.minSplittance(g))
            assertTrue { GraphProperty.isSplitGraph(g) }
        }

        @Test
        fun `complete graph has splittance 0 and is split graph`() {
            val g = createCompleteGraph(10)
            assertEquals(0, GraphProperty.minSplittance(g))
            assertTrue { GraphProperty.isSplitGraph(g) }
        }

        @Test
        fun `split graph has splittance 0 and is split graph`() {
            var g = createSplitGraph(10, 15)
            assertEquals(0, GraphProperty.minSplittance(g))
            assertTrue { GraphProperty.isSplitGraph(g) }
            g = createSplitGraph(23, 2)
            assertEquals(0, GraphProperty.minSplittance(g))
            assertTrue { GraphProperty.isSplitGraph(g) }
            g = createSplitGraph(1, 5)
            assertEquals(0, GraphProperty.minSplittance(g))
            assertTrue { GraphProperty.isSplitGraph(g) }
        }

        @Test
        fun `graph has correct non zero splittance 1`() {
            val g = createSplitGraph(10, 10)
            g.removeEdge(11, 14)
            assertEquals(1, GraphProperty.minSplittance(g))
        }

        @Test
        fun `graph has correct non zero splittance 2`() {
            val g = createSplitGraph(10, 10)
            g.removeEdge(11, 14)
            g.removeEdge(12, 17)
            g.addEdge(5, 6) // this edge does not have to be removed since 5 and 6 can now be part of the clique
            assertEquals(2, GraphProperty.minSplittance(g))
        }
    }
}
