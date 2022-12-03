package utils

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class SetTheoryTest {

    @Nested
    internal inner class IsDisjoint {

        @Test
        fun `{1 2 3} and {6 7} are disjoint`() {
            assertTrue { SetTheory.isDisjoint(setOf(1, 2, 3), setOf(6, 7)) }
        }

        @Test
        fun `{1 2 3} and {3 4 5} are not disjoint`() {
            assertFalse { SetTheory.isDisjoint(setOf(1, 2, 3), setOf(3, 4, 5)) }
        }

        @Test
        fun `{ } and {3 4 5} are disjoint`() {
            assertTrue { SetTheory.isDisjoint(setOf(), setOf(3, 4, 5)) }
        }

        @Test
        fun `{3 4} and {3 4 5} are not disjoint`() {
            assertFalse { SetTheory.isDisjoint(setOf(3, 4), setOf(3, 4, 5)) }
        }

        @Test
        fun `{ } and { } are disjoint`() {
            assertFalse { SetTheory.isDisjoint(setOf<Int>(), setOf()) }
        }
    }

    @Nested
    internal inner class IntersectionSize {

        @Test
        fun `{1 2 3} and {6 7} have intersection of size 0`() {
            assertEquals(0, SetTheory.intersectionSize(setOf(1, 2, 3), setOf(6, 7)))
        }

        @Test
        fun `{1 2 3} and {3 4 5} have intersection of size 1`() {
            assertEquals(1, SetTheory.intersectionSize(setOf(1, 2, 3), setOf(3, 4, 5)))
        }

        @Test
        fun `{ } and {3 4 5} have intersection of size 0`() {
            assertEquals(0, SetTheory.intersectionSize(setOf(), setOf(3, 4, 5)))
        }

        @Test
        fun `{3 4} and {3 4 5} have intersection of size 2`() {
            assertEquals(2, SetTheory.intersectionSize(setOf(3, 4), setOf(3, 4, 5)))
        }
    }

    @Nested
    internal inner class UnionSize {

        @Test
        fun `{1 2 3} and {6 7} have union of size 5`() {
            assertEquals(5, SetTheory.unionSize(setOf(1, 2, 3), setOf(6, 7)))
        }

        @Test
        fun `{1 2 3} and {3 4 5} have union of size 5`() {
            assertEquals(5, SetTheory.unionSize(setOf(1, 2, 3), setOf(3, 4, 5)))
        }

        @Test
        fun `{ } and {3 4 5} have union of size 3`() {
            assertEquals(3, SetTheory.unionSize(setOf(), setOf(3, 4, 5)))
        }

        @Test
        fun `{3 4} and {3 4 5} have union of size 3`() {
            assertEquals(3, SetTheory.unionSize(setOf(3, 4), setOf(3, 4, 5)))
        }
    }

}