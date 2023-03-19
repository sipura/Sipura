package sipura.generate

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import sipura.utils.getFieldValue
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class FactoryTest {

    private val mapName = "map"

    @Nested
    internal inner class Path {

        @Test
        fun `factory-method throws exception for path of negative length`() {
            assertThrows<IllegalArgumentException> { Factory.createPathGraph(-7) }
        }

        @Test
        fun `path of length 0 is just empty graph`() {
            val g = Factory.createPathGraph(0)
            val correctMap = mapOf<Int, Set<Int>>()
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `path of length 1 is one isolated vertex`() {
            val g = Factory.createPathGraph(1)
            val correctMap = mapOf(1 to emptySet<Int>())
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `path of length 5`() {
            val g = Factory.createPathGraph(5)
            val correctMap = mapOf(
                1 to setOf(2),
                2 to setOf(1, 3),
                3 to setOf(2, 4),
                4 to setOf(3, 5),
                5 to setOf(4),
            )
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `really long path`() {
            val n = 500_000
            val g = Factory.createPathGraph(n)
            val correctMap = mutableMapOf(
                1 to setOf(2),
                n to setOf(n - 1),
            )
            for (i in 2 until n) correctMap[i] = setOf(i - 1, i + 1)

            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }
    }

    @Nested
    internal inner class Cycle {

        @Test
        fun `factory-method throws exception for cycles of length less than 3`() {
            for (n in listOf(-10, -5, -2, -1, 0, 1, 2)) {
                assertThrows<IllegalArgumentException> { Factory.createCycleGraph(n) }
            }
        }

        @Test
        fun `cycle of length 3 is a triangle`() {
            val g = Factory.createCycleGraph(3)
            val correctMap = mapOf(
                1 to setOf(2, 3),
                2 to setOf(1, 3),
                3 to setOf(1, 2),
            )
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `cycle of length 5 is correct`() {
            val g = Factory.createCycleGraph(5)
            val correctMap = mapOf(
                1 to setOf(2, 5),
                2 to setOf(1, 3),
                3 to setOf(2, 4),
                4 to setOf(3, 5),
                5 to setOf(1, 4),
            )
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `really big cycle`() {
            val n = 500_000
            val g = Factory.createCycleGraph(n)
            val correctMap = mutableMapOf(
                1 to setOf(2, n),
                n to setOf(1, n - 1),
            )
            for (i in 2 until n) correctMap[i] = setOf(i - 1, i + 1)

            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }
    }

    @Nested
    internal inner class CompleteGraph {

        @Test
        fun `factory-method throws exception for complete graphs of negative size`() {
            for (n in listOf(-10, -5, -2, -1)) {
                assertThrows<IllegalArgumentException> { Factory.createCompleteGraph(n) }
            }
        }

        @Test
        fun `complete graph of size 3 is a triangle`() {
            val g = Factory.createCompleteGraph(3)
            val correctMap = mapOf(
                1 to setOf(2, 3),
                2 to setOf(1, 3),
                3 to setOf(1, 2),
            )
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }
    }

    @Nested
    internal inner class BipartiteGraph {

        @Test
        fun `error if both sides have negative size`() {
            assertThrows<IllegalArgumentException> { Factory.createBipartiteGraph(-3, -5) }
        }

        @Test
        fun `if both sides have size 0, you get the empty graph`() {
            val g = Factory.createBipartiteGraph(0, 0)
            assertEquals(HashMap<Int, Set<Int>>(), getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `if one side has size 0, the other side has isolated vertices`() {
            val g = Factory.createBipartiteGraph(13, 0)
            val correctMap = HashMap<Int, Set<Int>>()
            for (i in 1..13) correctMap[i] = emptySet()
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }
    }

    @Nested
    internal inner class Star {

        @Test
        fun `factory-method throws exception for negative size`() {
            assertThrows<IllegalArgumentException> { Factory.createStarGraph(-7) }
        }

        @Test
        fun `create star of two vertices`() {
            val g = Factory.createStarGraph(2)
            val correctMap = mapOf(1 to setOf(2), 2 to setOf(1))
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `create star with 5 corners`() {
            val g = Factory.createStarGraph(6)
            val correctMap = HashMap<Int, MutableSet<Int>>()
            correctMap[1] = mutableSetOf()
            for (i in 2..6) {
                correctMap[1]!!.add(i)
                correctMap[i] = mutableSetOf(1)
            }
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `can't create star of size 0`() {
            assertThrows<IllegalArgumentException> { Factory.createStarGraph(0) }
        }

        @Test
        fun `create star with just one vertex`() {
            val g = Factory.createStarGraph(1)
            val correctMap = mapOf(1 to setOf<Int>())
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }
    }

    @Nested
    internal inner class SplitGraph {

        @Test
        fun `can't create split graph with negative size`() {
            assertThrows<IllegalArgumentException> { Factory.createSplitGraph(-1, 10) }
            assertThrows<IllegalArgumentException> { Factory.createSplitGraph(23, -123) }
            assertThrows<IllegalArgumentException> { Factory.createSplitGraph(-29, -5) }
        }

        @Test
        fun `create split graph with empty independent set`() {
            val g = Factory.createSplitGraph(0, 5)
            assertEquals(5, g.n)
            assertEquals(10, g.m)
        }

        @Test
        fun `create split graph with empty clique`() {
            val g = Factory.createSplitGraph(10, 0)
            assertEquals(10, g.n)
            assertEquals(0, g.m)
        }

        @Test
        fun `create split graph with both sides not empty`() {
            val g = Factory.createSplitGraph(5, 4)
            assertEquals(9, g.n)
            // check edges of independent set
            for (v1 in 1..4) {
                for (v2 in (v1 + 1)..5) {
                    assertFalse { g.hasEdge(v1, v2) }
                }
            }
            // check edges of clique
            for (v1 in 6..8) {
                for (v2 in (v1 + 1)..9) {
                    assertTrue { g.hasEdge(v1, v2) }
                }
            }
            // check edges between IS and Clique
            for (v1 in 1..5) {
                for (v2 in 6..9) {
                    assertTrue { g.hasEdge(v1, v2) }
                }
            }
        }
    }
}
