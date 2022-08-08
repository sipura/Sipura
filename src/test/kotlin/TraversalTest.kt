import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.NoSuchElementException
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TraversalTest {

    lateinit var path5: SimpleGraph<Int>
    lateinit var star4plus1: SimpleGraph<Int>

    @BeforeEach
    fun setup() {
        path5 = SimpleGraph()
        path5.addVertex(1)
        path5.addVertex(2)
        path5.addVertex(3)
        path5.addVertex(4)
        path5.addVertex(5)
        path5.addEdge(1, 2)
        path5.addEdge(2, 3)
        path5.addEdge(3, 4)
        path5.addEdge(4, 5)

        star4plus1 = SimpleGraph()
        star4plus1.addVertex(1)
        star4plus1.addVertex(2)
        star4plus1.addVertex(3)
        star4plus1.addVertex(4)
        star4plus1.addVertex(5)
        star4plus1.addEdge(1, 2)
        star4plus1.addEdge(1, 3)
        star4plus1.addEdge(1, 4)
    }

    @Test
    fun `bfs iterator reaches every vertex in the correct order`() {
        var expectedNext = 1
        for (v in Traversal.bfsIterator(path5, 1)) {
            assertEquals(expectedNext, v)
            expectedNext++
        }
        assertEquals(6, expectedNext)
    }

    @Test
    fun `bfs iterator does not reach isolated vertex`() {
        for (v in Traversal.bfsIterator(star4plus1, 1)) {
            assertTrue { v != 5 }
        }
    }

    @Test
    fun `bfs iterator throws exception if vertex does not exist`() {
        assertThrows<IllegalArgumentException> { Traversal.bfsIterator(path5, 6) }
    }

    @Test
    fun `next throws exception in bfs iterator if hasNext is false`() {
        val iter = Traversal.bfsIterator(star4plus1, 2)
        while (iter.hasNext()) iter.next()
        assertThrows<NoSuchElementException> { iter.next() }
    }

    @Test
    fun `start bfs in the center of a path`() {
        val iter = Traversal.bfsIterator(path5, 3)
        assertTrue { iter.next() == 3 }
        assertContains(setOf(2,4), iter.next())
        assertContains(setOf(2,4), iter.next())
        assertContains(setOf(1,5), iter.next())
        assertContains(setOf(1,5), iter.next())
        assertFalse { iter.hasNext() }
    }


}