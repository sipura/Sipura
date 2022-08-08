import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class FactoryTest {

    private val mapName = "map"
    
    @Nested
    internal inner class Path {


        @Test
        fun `factory-method throws exception for path of negative length`() {
            assertThrows<IllegalArgumentException> { Factory.createPath(-7) }
        }

        @Test
        fun `path of length 0 is just empty graph`() {
            val g = Factory.createPath(0)
            val correctMap = mapOf<Int, Set<Int>>()
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `path of length 1 is one isolated vertex`() {
            val g = Factory.createPath(1)
            val correctMap = mapOf(1 to emptySet<Int>())
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `path of length 5`() {
            val g = Factory.createPath(5)
            val correctMap = mapOf(
                1 to setOf(2),
                2 to setOf(1, 3),
                3 to setOf(2, 4),
                4 to setOf(3, 5),
                5 to setOf(4)
            )
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `really long path`() {
            val n = 500_000
            val g = Factory.createPath(n)
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
                assertThrows<IllegalArgumentException> { Factory.createCycle(n) }
            }
        }

        @Test
        fun `cycle of length 3 is a triangle`() {
            val g = Factory.createCycle(3)
            val correctMap = mapOf(
                1 to setOf(2,3),
                2 to setOf(1,3),
                3 to setOf(1,2),
            )
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `cycle of length 5 is correct`() {
            val g = Factory.createCycle(5)
            val correctMap = mapOf(
                1 to setOf(2,5),
                2 to setOf(1,3),
                3 to setOf(2,4),
                4 to setOf(3,5),
                5 to setOf(1,4),
            )
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `really big cycle`() {
            val n = 500_000
            val g = Factory.createCycle(n)
            val correctMap = mutableMapOf(
                1 to setOf(2, n),
                n to setOf(1, n-1),
            )
            for (i in 2 until n) correctMap[i] = setOf(i-1, i+1)

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
            val g = Factory.createCycle(3)
            val correctMap = mapOf(
                1 to setOf(2,3),
                2 to setOf(1,3),
                3 to setOf(1,2),
            )
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

    }

    @Nested
    internal inner class BipartiteGraph {

        @Test
        fun `error if both sides have negative size`() {
            assertThrows<IllegalArgumentException> { Factory.createBipartite(-3, -5) }
        }

        @Test
        fun `if both sides have size 0, you get the empty graph`() {
            val g = Factory.createBipartite(0, 0)
            assertEquals(HashMap<Int, Set<Int>>(), getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `if one side has size 0, the other side has isolated vertices`() {
            val g = Factory.createBipartite(13, 0)
            val correctMap = HashMap<Int, Set<Int>>()
            for(i in 1..13) correctMap[i] = emptySet()
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }
    }

    @Nested
    internal inner class Star {

        @Test
        fun `factory-method throws exception for negative size`() {
            assertThrows<IllegalArgumentException> { Factory.createStar(-7) }
        }

        @Test
        fun `create star of two vertices`() {
            val g = Factory.createStar(2)
            val correctMap = mapOf(1 to setOf(2), 2 to setOf(1))
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

        @Test
        fun `create star with 5 corners`() {
            val g = Factory.createStar(6)
            val correctMap = HashMap<Int, MutableSet<Int>>()
            correctMap[1] = mutableSetOf()
            for (i in 2..6) {
                correctMap[1]!!.add(i)
                correctMap[i] = mutableSetOf(1)
            }
            assertEquals(correctMap, getFieldValue(mapName, g, g.javaClass))
        }

    }
}