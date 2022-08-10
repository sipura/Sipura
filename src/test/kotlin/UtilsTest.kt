import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class UtilsTest {

    @Nested
    internal inner class IsDisjoint {

        @Test
        fun `{1 2 3} and {6 7} are disjoint`() {
            assertTrue { Utils.isDisjoint(setOf(1, 2, 3), setOf(6, 7)) }
        }

        @Test
        fun `{1 2 3} and {3 4 5} are not disjoint`() {
            assertFalse { Utils.isDisjoint(setOf(1, 2, 3), setOf(3, 4, 5)) }
        }

        @Test
        fun `{ } and {3 4 5} are disjoint`() {
            assertTrue { Utils.isDisjoint(setOf(), setOf(3, 4, 5)) }
        }

        @Test
        fun `{3 4} and {3 4 5} are not disjoint`() {
            assertFalse { Utils.isDisjoint(setOf(3, 4), setOf(3, 4, 5)) }
        }
    }

    @Nested
    internal inner class IntersectionSize {

        @Test
        fun `{1 2 3} and {6 7} have intersection of size 0`() {
            assertEquals(0, Utils.intersectionSize(setOf(1, 2, 3), setOf(6, 7)))
        }

        @Test
        fun `{1 2 3} and {3 4 5} have intersection of size 1`() {
            assertEquals(1, Utils.intersectionSize(setOf(1, 2, 3), setOf(3, 4, 5)))
        }

        @Test
        fun `{ } and {3 4 5} have intersection of size 0`() {
            assertEquals(0, Utils.intersectionSize(setOf(), setOf(3, 4, 5)))
        }

        @Test
        fun `{3 4} and {3 4 5} have intersection of size 2`() {
            assertEquals(2, Utils.intersectionSize(setOf(3, 4), setOf(3, 4, 5)))
        }
    }

}