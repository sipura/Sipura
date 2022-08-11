import Factory.createLine
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class NeighborsTest {

    @Nested
    internal inner class ClosedNeighbors {

        @Test
        fun `path of length 5 inside of path7`() {
            val correctNB = setOf(2, 3, 4, 5, 6)
            assertEquals(correctNB, Neighbors.closedNeighbors(createLine(7), v = 4, k = 2))
        }

    }

    @Nested
    internal inner class OpenNeighbors {

        @Test
        fun `path of length 5 inside of path7`() {
            val correctNB = setOf(2, 6)
            assertEquals(correctNB, Neighbors.openNeighbors(createLine(7), v = 4, k = 2))
        }

    }
}