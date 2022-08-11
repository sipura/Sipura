package alg

import generate.Factory.createLine
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class NeighborsTest {

    @Nested
    internal inner class ClosedNeighborsDistK {

        @Test
        fun `path of length 5 inside of path7`() {
            val correctNB = setOf(2, 3, 4, 5, 6)
            assertEquals(correctNB, Neighbors.closedNeighborsDistK(createLine(7), v = 4, k = 2))
        }

    }

    @Nested
    internal inner class OpenNeighborsDistK {

        @Test
        fun `path of length 5 inside of path7`() {
            val correctNB = setOf(2, 6)
            assertEquals(correctNB, Neighbors.openNeighborsDistK(createLine(7), v = 4, k = 2))
        }

    }

    @Nested
    internal inner class ClosedNeighbors {

        @Test
        fun `path of length 5 inside of path7`() {
            assertEquals(setOf(3, 4, 5), Neighbors.closedNeighbors(createLine(7), v = 4))
        }

        @Test
        fun `isolated vertex`() {
            assertEquals(setOf(1), Neighbors.closedNeighbors(createLine(1), v = 1))
        }

        @Test
        fun `throws exception if vertex is not in the graph`() {
            assertThrows<IllegalArgumentException> { Neighbors.closedNeighbors(createLine(1), v = 4) }
        }
    }
}