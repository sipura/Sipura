package utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class PartitioningTest {

    @Test
    fun `create partitioning of 3 distinct elements`() {
        val p = Partitioning<Char>()
        for (c in listOf('a', 'b', 'c')) {
            p.add(listOf(c))
        }

        assertEquals(setOf('a', 'b', 'c'), p.elements())

        assertEquals(3, p.size())

        assertEquals(listOf(setOf('a'), setOf('b'), setOf('c')), p.subsets())

        for (c in listOf('d', 'x', '#')) {
            assertFalse(c in p)
        }

        for (c in listOf('a', 'b', 'c')) {
            assertEquals(setOf(c), p[c])
        }

        p.addToSubset('c', 'd')
        assertEquals(setOf('c', 'd'), p['c'])

        val p2 = Partitioning<Char>()
        p2.add(listOf('e', 'f'))
        p.doDisjointUnion(p2)

        assertEquals(setOf('a', 'b', 'c', 'd', 'e', 'f'), p.elements())
    }
}
