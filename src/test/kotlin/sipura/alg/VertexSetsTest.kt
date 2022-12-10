package sipura.alg

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import sipura.alg.VertexSets.countCoveredEdges
import sipura.alg.VertexSets.cutSize
import sipura.alg.VertexSets.getLeafs
import sipura.alg.VertexSets.isIndependentSet
import sipura.alg.VertexSets.isVertexCover
import sipura.alg.VertexSets.treeCenter
import sipura.generate.Factory.createCycle
import sipura.generate.Factory.createLine
import sipura.generate.Factory.createStar
import kotlin.test.assertEquals

internal class VertexSetsTest {

    @Nested
    internal inner class GetLeafs {

        @Test
        fun `path5 has two leafs`() {
            val g = createLine(5)
            assertEquals(setOf(1, 5), getLeafs(g))
        }

        @Test
        fun `cycle10 has no leafs`() {
            val g = createCycle(10)
            assertEquals(setOf(), getLeafs(g))
        }
    }

    @Nested
    internal inner class TreeCenter {

        @Test
        fun `path5 has one center vertex`() {
            val g = createLine(5)
            assertEquals(setOf(3), treeCenter(g))
        }

        @Test
        fun `path4 has two center vertices`() {
            val g = createLine(4)
            assertEquals(setOf(2, 3), treeCenter(g))
        }

        @Test
        fun `path5 with extra vertex attached to middle has one center vertex`() {
            val g = createLine(5)
            g.addVertex(6)
            g.addEdge(3, 6)
            assertEquals(setOf(3), treeCenter(g))
        }

        @Test
        fun `center of star is the only vertex that is not a leaf`() {
            val g = createStar(10)
            assertEquals(setOf(1), treeCenter(g))
        }
    }

    @Nested
    internal inner class IsIndependentSet {

        @Test
        fun star() {
            val g = createStar(10)
            assertTrue { isIndependentSet(g, setOf(2, 3, 4, 5, 6)) }
            assertFalse { isIndependentSet(g, setOf(1, 4)) }
            assertFalse { isIndependentSet(g, setOf(1, 2, 5, 7)) }
        }

        @Test
        fun path() {
            val g = createLine(10)
            assertTrue { isIndependentSet(g, setOf(1, 3, 6, 8)) }
            assertFalse { isIndependentSet(g, setOf(1, 2)) }
            assertFalse { isIndependentSet(g, setOf(1, 4, 5, 7)) }
        }

        @Test
        fun `throws IllegalArgumentException if S contains vertices that are not in g`() {
            val g = createLine(4)
            assertThrows<IllegalArgumentException> { isIndependentSet(g, setOf(1, 5)) }
        }
    }

    @Nested
    internal inner class IsVertexCover {

        @Test
        fun star() {
            val g = createStar(10)
            assertTrue { isVertexCover(g, setOf(1)) }
            assertTrue { isVertexCover(g, (2..10).toSet()) }
            assertFalse { isVertexCover(g, setOf(2, 4)) }
        }

        @Test // 1-2-3-4
        fun `the endpoints of path4 are not a vertex cover`() {
            val g = createLine(4)
            assertFalse { isVertexCover(g, setOf(1, 4)) }
        }

        @Test
        fun `throws IllegalArgumentException if S contains vertices that are not in g`() {
            val g = createLine(4)
            assertThrows<IllegalArgumentException> { isVertexCover(g, setOf(1, 5)) }
        }
    }

    @Nested
    internal inner class CountCoveredEdges {

        @Test
        fun star() {
            val g = createStar(10)
            assertEquals(9, countCoveredEdges(g, setOf(1)))
            assertEquals(3, countCoveredEdges(g, setOf(3, 6, 8)))
        }

        @Test
        fun path() {
            val g = createLine(7)
            assertEquals(2, countCoveredEdges(g, setOf(3)))
            assertEquals(3, countCoveredEdges(g, setOf(3, 4)))
            assertEquals(6, countCoveredEdges(g, setOf(2, 4, 6)))
        }

        @Test
        fun `throws IllegalArgumentException if S contains vertices that are not in g`() {
            val g = createLine(4)
            assertThrows<IllegalArgumentException> { countCoveredEdges(g, setOf(1, 5)) }
        }
    }

    @Nested
    internal inner class CutSize {

        @Test
        fun `center of star4 has cut of 3`() {
            val g = createStar(4)
            assertEquals(3, cutSize(g, setOf(1)))
        }

        @Test
        fun `two middle vertices of path4 have cut of 2`() {
            val g = createLine(4)
            assertEquals(2, cutSize(g, setOf(2, 3)))
        }

        @Test
        fun `cut size of the empty subset is 0`() {
            val g = createLine(4)
            assertEquals(0, cutSize(g, setOf()))
        }

        @Test
        fun `throws IllegalArgumentException if S contains vertices that are not in g`() {
            val g = createLine(4)
            assertThrows<IllegalArgumentException> { cutSize(g, setOf(1, 5)) }
        }
    }
}
