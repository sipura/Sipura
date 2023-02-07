package sipura.graphs

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import sipura.generate.Factory
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SimpleGraphUndoTest {

    @Nested
    internal inner class Constructor {

        @Test
        fun `created graph is equal to used graph`() {
            val g1 = Factory.createStarGraph(10)
            val g2 = SimpleGraphUndo(g1)
            assertEquals(g1, g2)
        }

        @Test
        fun `constructor does not add undo operations`() {
            val g = SimpleGraphUndo(Factory.createCompleteGraph(10))
            assertEquals(0, g.undoSize)
        }
    }

    @Nested
    internal inner class AddVertex {

        @Test
        fun `add new vertex and undo results in the same graph`() {
            val g1 = Factory.createStarGraph(5)
            val g2 = SimpleGraphUndo(g1)
            assertTrue(g2.addVertex(6))
            g2.undo()
            assertEquals(g1, g2)
        }

        @Test
        fun `add existing vertex does not add undo operation`() {
            val g = SimpleGraphUndo(Factory.createPathGraph(5))
            assertFalse(g.addVertex(2))
            assertEquals(0, g.undoSize)
        }

        @Test
        fun `add new vertex adds exactly one undo operation`() {
            val g = SimpleGraphUndo(Factory.createPathGraph(5))
            assertTrue(g.addVertex(6))
            assertEquals(1, g.undoSize)
        }
    }

    @Nested
    internal inner class RemoveVertex {
        @Test
        fun `remove existing vertex and undo results in the same graph`() {
            val g1 = Factory.createStarGraph(5)
            val g2 = SimpleGraphUndo(g1)
            assertTrue(g2.removeVertex(1))
            g2.undo()
            assertEquals(g1, g2)
        }

        @Test
        fun `remove new vertex does not add undo operation`() {
            val g = SimpleGraphUndo(Factory.createPathGraph(5))
            assertFalse(g.removeVertex(6))
            assertEquals(0, g.undoSize)
        }

        @Test
        fun `remove existing vertex adds exactly one undo operation`() {
            val g = SimpleGraphUndo(Factory.createPathGraph(5))
            assertTrue(g.removeVertex(2))
            assertEquals(1, g.undoSize)
        }
    }

    @Nested
    internal inner class AddEdge {

        @Test
        fun `add new edge and undo results in the same graph`() {
            val g1 = Factory.createStarGraph(5)
            val g2 = SimpleGraphUndo(g1)
            assertTrue(g2.addEdge(4, 5))
            g2.undo()
            assertEquals(g1, g2)
        }

        @Test
        fun `add existing edge does not add undo operation`() {
            val g = SimpleGraphUndo(Factory.createPathGraph(5))
            assertFalse(g.addEdge(2, 3))
            assertEquals(0, g.undoSize)
        }

        @Test
        fun `add new edge adds exactly one undo operation`() {
            val g = SimpleGraphUndo(Factory.createPathGraph(5))
            assertTrue(g.addEdge(1, 5))
            assertEquals(1, g.undoSize)
        }
    }

    @Nested
    internal inner class RemoveEdge {

        @Test
        fun `remove existing edge and undo results in the same graph`() {
            val g1 = Factory.createStarGraph(5)
            val g2 = SimpleGraphUndo(g1)
            assertTrue(g2.removeEdge(1, 2))
            g2.undo()
            assertEquals(g1, g2)
        }

        @Test
        fun `remove new edge does not add undo operation`() {
            val g = SimpleGraphUndo(Factory.createPathGraph(5))
            assertFalse(g.removeEdge(1, 5))
            assertEquals(0, g.undoSize)
        }

        @Test
        fun `remove existing edge adds exactly one undo operation`() {
            val g = SimpleGraphUndo(Factory.createPathGraph(5))
            assertTrue(g.removeEdge(1, 2))
            assertEquals(1, g.undoSize)
        }
    }
}
