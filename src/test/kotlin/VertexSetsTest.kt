import Factory.createCycle
import Factory.createPath
import Factory.createStar
import VertexSets.countCoveredEdges
import VertexSets.cutSize
import VertexSets.getLeafs
import VertexSets.isDominatingSet
import VertexSets.isIndependentSet
import VertexSets.isVertexCover
import VertexSets.treeCenter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class VertexSetsTest {

    @Nested
    internal inner class GetLeafs {

        @Test
        fun `path5 has two leafs`() {
            val g = createPath(5)
            assertEquals(setOf(1,5), getLeafs(g))
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
            val g = createPath(5)
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
            assertTrue {isIndependentSet(setOf(2, 3, 4, 5, 6), g)}
            assertFalse { isIndependentSet(setOf(1, 4), g) }
            assertFalse { isIndependentSet(setOf(1, 2, 5, 7), g)}
        }

        @Test
        fun path() {
            val g = createPath(10)
            assertTrue{isIndependentSet(setOf(1, 3, 6, 8), g)}
            assertFalse{isIndependentSet(setOf(1, 2), g)}
            assertFalse{isIndependentSet(setOf(1, 4, 5, 7), g)}
        }
    }

    @Nested
    internal inner class IsVertexCover {
        @Test
        fun star() {
            val g = createStar(10)
            assertTrue{isVertexCover(setOf(1), g)}
            assertTrue{isVertexCover((2..10).toSet(), g)}
            assertFalse{isVertexCover(setOf(2, 4), g)}
        }

        @Test
        fun path() {
            val g = createPath(7)
            assertFalse{isVertexCover(setOf(1, 5, 7), g)}

            assertTrue{isVertexCover((1..7).toSet(), g)}
            assertTrue{isVertexCover(setOf(1, 3, 5, 7), g)}
            assertFalse{isVertexCover(setOf(1), g)}
        }
    }

    @Nested
    internal inner class IsDominatingSet {

        @Test
        fun star() {
            val g = createStar(10)
            assertTrue{isDominatingSet(setOf(1), g)}
            assertTrue{isDominatingSet((2..10).toSet(), g)}
            assertFalse{isDominatingSet(setOf(2, 4), g)}
        }

        @Test
        fun path() {
            val g = createPath(7)
            assertFalse{isDominatingSet(setOf(1, 5, 7), g)}

            assertTrue{isDominatingSet((1..7).toSet(), g)}
            assertTrue{isDominatingSet(setOf(1, 4, 7), g)}
            assertFalse{isDominatingSet(setOf(1), g)}
        }
    }

    @Nested
    internal inner class CountCoveredEdges {

        @Test
        fun star() {
            val g = createStar(10)
            assertEquals(9, countCoveredEdges(setOf(1), g))
            assertEquals(3, countCoveredEdges(setOf(3, 6, 8), g))
        }

        @Test
        fun path() {
            val g = createPath(7)
            assertEquals(2, countCoveredEdges(setOf(3), g))
            assertEquals(3, countCoveredEdges(setOf(3, 4), g))
            assertEquals(6, countCoveredEdges(setOf(2, 4, 6), g))
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
            val g = createPath(4)
            assertEquals(2, cutSize(g, setOf(2,3)))
        }

        @Test
        fun `cutsize of the empty subset is 0`() {
            val g = createPath(4)
            assertEquals(0, cutSize(g, setOf()))
        }
    }
}