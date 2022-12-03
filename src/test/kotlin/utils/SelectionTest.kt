package utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class SelectionTest {

    @Test
    fun `take top 3 elements from 1 2 3 4 5 6 7 8 9 10`() {
        val l = (1..10).toList()
        val res = Selection.takeTopK(l, 3)
        assertEquals(res.toSet(), setOf(8, 9, 10))
        assertEquals(3, res.size)
    }

    @Test
    fun `take top 100 elements from 1 to million`() {
        val l = (1..1_000_000).toList()
        val res = Selection.takeTopK(l, 100)
        assertEquals(res.toSet(), (999_901..1_000_000).toSet())
        assertEquals(100, res.size)
    }

    @Test
    fun `throws NoSuchElementException when trying to take 10 elements from 1 2 3`() {
        val l = listOf(1, 2, 3)
        assertThrows<NoSuchElementException> { Selection.takeTopK(l, 10) }
    }
}
