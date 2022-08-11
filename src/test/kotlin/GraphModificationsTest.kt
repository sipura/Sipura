import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class GraphModificationsTest {

    @Nested
    internal inner class Complement {

        @Test
        fun `complement of triangle is empty`() {
            val triangle = Factory.createCycle(3)
            val complement = GraphModifications.complementGraph(triangle)
            assertEquals(3, complement.n)
            assertEquals(0, complement.m)
        }

        @Test
        fun `complement of line4 is one edge and one isolated vertex`() {
            val triangle = Factory.createLine(3)
            val complement = GraphModifications.complementGraph(triangle)
            assertEquals(3, complement.n)
            assertEquals(1, complement.m)
            assertTrue { complement.hasEdge(1, 3) }
        }

    }


}