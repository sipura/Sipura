package sipura.alg

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import sipura.Samples
import sipura.generate.Factory
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TraversalTest {

    @Nested
    internal inner class BreadthFirstSearchLevelIterator {

        @Test
        fun `BFS iterator reaches every vertex of path5 in the correct order`() {
            var expectedNext = 1
            for (v in Traversal.breadthFirstSearchLayerIterator(Factory.createLine(5), 1)) {
                assertEquals(setOf(expectedNext), v)
                expectedNext++
            }
        }

        @Test
        fun `BFS iterator does not reach isolated vertex`() {
            for (layer in Traversal.breadthFirstSearchLayerIterator(Samples.star4Plus1IsolatedVertex(), 1)) {
                assertTrue { layer != setOf(5) }
            }
        }

        @Test
        fun `BFS iterator throws exception if start-vertex does not exist`() {
            assertThrows<IllegalArgumentException> {
                Traversal.breadthFirstSearchLayerIterator(
                    Factory.createLine(5),
                    6
                )
            }
        }

        @Test
        fun `next() throws exception in BFS iterator if hasNext is false`() {
            val iter = Traversal.breadthFirstSearchLayerIterator(Samples.star4Plus1IsolatedVertex(), 2)
            while (iter.hasNext()) iter.next()
            assertThrows<NoSuchElementException> { iter.next() }
        }

        @Test
        fun `start BFS in the center of a line`() {
            val iter = Traversal.breadthFirstSearchLayerIterator(Factory.createLine(5), 3)
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
            for (v in Traversal.breadthFirstSearchIterator(Factory.createLine(5), 1)) {
                assertEquals(expectedNext, v)
                expectedNext++
            }
            assertEquals(6, expectedNext)
        }

        @Test
        fun `BFS iterator does not reach isolated vertex`() {
            for (v in Traversal.breadthFirstSearchIterator(Samples.star4Plus1IsolatedVertex(), 1)) {
                assertTrue { v != 5 }
            }
        }

        @Test
        fun `BFS iterator throws exception if start-vertex does not exist`() {
            assertThrows<IllegalArgumentException> { Traversal.breadthFirstSearchIterator(Factory.createLine(5), 6) }
        }

        @Test
        fun `next() throws exception in BFS iterator if hasNext is false`() {
            val iter = Traversal.breadthFirstSearchIterator(Samples.star4Plus1IsolatedVertex(), 2)
            while (iter.hasNext()) iter.next()
            assertThrows<NoSuchElementException> { iter.next() }
        }

        @Test
        fun `start BFS in the center of a path`() {
            val iter = Traversal.breadthFirstSearchIterator(Factory.createLine(5), 3)
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
            for (v in Traversal.depthFirstSearchIterator(Factory.createLine(5), 1)) {
                assertEquals(expectedNext, v)
                expectedNext++
            }
            assertEquals(6, expectedNext)
        }

        @Test
        fun `DFS iterator does not reach isolated vertex`() {
            for (v in Traversal.depthFirstSearchIterator(Samples.star4Plus1IsolatedVertex(), 1)) {
                assertTrue { v != 5 }
            }
        }

        @Test
        fun `DFS iterator throws exception if start-vertex does not exist`() {
            assertThrows<IllegalArgumentException> { Traversal.depthFirstSearchIterator(Factory.createLine(5), 6) }
        }

        @Test
        fun `next() throws exception in DFS iterator if hasNext is false`() {
            val iter = Traversal.depthFirstSearchIterator(Samples.star4Plus1IsolatedVertex(), 2)
            while (iter.hasNext()) iter.next()
            assertThrows<NoSuchElementException> { iter.next() }
        }

        @Test
        fun `start DFS in the center of a path`() {
            val iter: Iterator<Int> = Traversal.depthFirstSearchIterator(Factory.createLine(5), 3)
            val result = iter.asSequence().toList()

            // which neighbor gets picked first is arbitrary, so we need to check both valid orders
            assertTrue { result == listOf(3, 2, 1, 4, 5) || result == listOf(3, 4, 5, 2, 1) }
            assertFalse { iter.hasNext() }
        }
    }
}
