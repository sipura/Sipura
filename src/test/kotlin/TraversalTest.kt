import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Nested
    internal inner class BreadthFirstSearchLevelIterator {

        @Test
        fun `BFS iterator reaches every vertex of path5 in the correct order`() {
            var expectedNext = 1
            for (v in Traversal.breadthFirstSearchLayerIterator(path5, 1)) {
                assertEquals(setOf(expectedNext), v)
                expectedNext++
            }
        }

        @Test
        fun `BFS iterator does not reach isolated vertex`() {
            for (layer in Traversal.breadthFirstSearchLayerIterator(star4plus1, 1)) {
                assertTrue { layer != setOf(5) }
            }
        }

        @Test
        fun `BFS iterator throws exception if start-vertex does not exist`() {
            assertThrows<IllegalArgumentException> { Traversal.breadthFirstSearchLayerIterator(path5, 6) }
        }

        @Test
        fun `next() throws exception in BFS iterator if hasNext is false`() {
            val iter = Traversal.breadthFirstSearchLayerIterator(star4plus1, 2)
            while (iter.hasNext()) iter.next()
            assertThrows<NoSuchElementException> { iter.next() }
        }

        @Test
        fun `start BFS in the center of a line`() {
            val iter = Traversal.breadthFirstSearchLayerIterator(path5, 3)
            assertTrue { iter.next() == setOf(3) }
            assertEquals(setOf(2, 4), iter.next())
            assertEquals(setOf(1, 5), iter.next())
            assertFalse { iter.hasNext() }
        }

    }

    @Nested
    internal inner class BreadthFirstSearchIterator {

        @Test
        fun `BFS iterator reaches every vertex of path5 in the correct order`() {
            var expectedNext = 1
            for (v in Traversal.breadthFirstSearchIterator(path5, 1)) {
                assertEquals(expectedNext, v)
                expectedNext++
            }
            assertEquals(6, expectedNext)
        }

        @Test
        fun `BFS iterator does not reach isolated vertex`() {
            for (v in Traversal.breadthFirstSearchIterator(star4plus1, 1)) {
                assertTrue { v != 5 }
            }
        }

        @Test
        fun `BFS iterator throws exception if start-vertex does not exist`() {
            assertThrows<IllegalArgumentException> { Traversal.breadthFirstSearchIterator(path5, 6) }
        }

        @Test
        fun `next() throws exception in BFS iterator if hasNext is false`() {
            val iter = Traversal.breadthFirstSearchIterator(star4plus1, 2)
            while (iter.hasNext()) iter.next()
            assertThrows<NoSuchElementException> { iter.next() }
        }

        @Test
        fun `start BFS in the center of a path`() {
            val iter = Traversal.breadthFirstSearchIterator(path5, 3)
            assertTrue { iter.next() == 3 }
            assertContains(setOf(2, 4), iter.next())
            assertContains(setOf(2, 4), iter.next())
            assertContains(setOf(1, 5), iter.next())
            assertContains(setOf(1, 5), iter.next())
            assertFalse { iter.hasNext() }
        }

    }


    @Nested
    internal inner class DepthFirstSearchIterator {

        @Test
        fun `DFS iterator reaches every vertex of path5 in the correct order`() {
            var expectedNext = 1
            for (v in Traversal.depthFirstSearchIterator(path5, 1)) {
                assertEquals(expectedNext, v)
                expectedNext++
            }
            assertEquals(6, expectedNext)
        }

        @Test
        fun `DFS iterator does not reach isolated vertex`() {
            for (v in Traversal.depthFirstSearchIterator(star4plus1, 1)) {
                assertTrue { v != 5 }
            }
        }

        @Test
        fun `DFS iterator throws exception if start-vertex does not exist`() {
            assertThrows<IllegalArgumentException> { Traversal.depthFirstSearchIterator(path5, 6) }
        }

        @Test
        fun `next() throws exception in DFS iterator if hasNext is false`() {
            val iter = Traversal.depthFirstSearchIterator(star4plus1, 2)
            while (iter.hasNext()) iter.next()
            assertThrows<NoSuchElementException> { iter.next() }
        }

        @Test
        fun `start DFS in the center of a path`() {
            val iter: Iterator<Int> = Traversal.depthFirstSearchIterator(path5, 3)
            val result = iter.asSequence().toList()

            // which neighbor gets picked first is arbitrary, so we need to check both valid orders
            assertTrue { result == listOf(3, 2, 1, 4, 5) || result == listOf(3, 4, 5, 2, 1) }
            assertFalse { iter.hasNext() }
        }

    }
}