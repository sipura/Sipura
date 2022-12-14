package sipura.alg

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import sipura.generate.Factory.createPathGraph
import sipura.generate.Factory.createStarGraph
import kotlin.test.assertEquals

internal class NeighborsTest {

    @Nested
    internal inner class ClosedNeighborsDistK {

        @Test
        fun `path of length 5 inside of path7`() {
            val correctNB = setOf(2, 3, 4, 5, 6)
            assertEquals(correctNB, Neighbors.closedNeighborsDistK(createPathGraph(7), v = 4, k = 2))
        }

        @Test
        fun `k is bigger than maximum diameter`() {
            assertEquals(setOf(1, 2, 3, 4), Neighbors.closedNeighborsDistK(createPathGraph(4), v = 2, k = 10))
        }

        @Test
        fun `throws exception if v is not in graph`() {
            assertThrows<IllegalArgumentException> { Neighbors.closedNeighborsDistK(createPathGraph(1), v = 2, k = 1) }
        }
    }

    @Nested
    internal inner class OpenNeighborsDistK {

        @Test
        fun `path of length 5 inside of path7`() {
            val correctNB = setOf(2, 6)
            assertEquals(correctNB, Neighbors.openNeighborsDistK(createPathGraph(7), v = 4, k = 2))
        }

        @Test
        fun `k is bigger than maximum diameter`() {
            assertEquals(emptySet(), Neighbors.openNeighborsDistK(createStarGraph(10), v = 1, k = 3))
        }

        @Test
        fun `throws exception if v is not in graph`() {
            assertThrows<IllegalArgumentException> { Neighbors.openNeighborsDistK(createPathGraph(1), v = 2, k = 1) }
        }
    }

    @Nested
    internal inner class ClosedNeighbors {

        @Test
        fun `path of length 5 inside of path7`() {
            assertEquals(setOf(3, 4, 5), Neighbors.closedNeighbors(createPathGraph(7), v = 4))
        }

        @Test
        fun `isolated vertex`() {
            assertEquals(setOf(1), Neighbors.closedNeighbors(createPathGraph(1), v = 1))
        }

        @Test
        fun `throws exception if vertex is not in the graph`() {
            assertThrows<IllegalArgumentException> { Neighbors.closedNeighbors(createPathGraph(1), v = 4) }
        }
    }
}
