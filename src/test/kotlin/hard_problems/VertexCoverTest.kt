package hard_problems

import generate.Factory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class VertexCoverTest {

    @Nested
    internal inner class Decision {

        @Test
        fun `path4 and k = 2 results in true`() {
            val g = Factory.createLine(4)
            val k = 2
            assertTrue { VertexCover.decisionVertexCover(g, k) }
        }

        @Test
        fun `graph doesn't get modified`() {
            val g = Factory.createLine(4)
            val k = 2
            assertTrue { VertexCover.decisionVertexCover(g, k) }
            assertEquals(4, g.n)
        }

        @Test
        fun `path9 is false for all k less than 4`() {
            val g = Factory.createLine(9)
            for (k in 0..3) {
                assertFalse { VertexCover.decisionVertexCover(g, k) }
            }
            assertTrue { VertexCover.decisionVertexCover(g, k = 4) }
        }

        @Test
        fun `path39 is false for all k less than 9`() {
            val n = 39
            val g = Factory.createLine(n)
            val optK = (n - 1) / 2
            for (k in 0 until optK) {
                assertFalse { VertexCover.decisionVertexCover(g, k) }
            }
            assertTrue { VertexCover.decisionVertexCover(g, k = optK) }
        }

    }
}