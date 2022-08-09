import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class VertexSetsTest {

    @Nested
    internal inner class GetLeafs {

        @Test
        fun `path5 has two leafs`() {
            val g = Factory.createPath(5)
            assertEquals(setOf(1,5), VertexSets.getLeafs(g))
        }

        @Test
        fun `cycle10 has no leafs`() {
            val g = Factory.createCycle(10)
            assertEquals(setOf<Int>(), VertexSets.getLeafs(g))
        }
    }

    @Nested
    internal inner class TreeCenter {

        @Test
        fun `path5 has one center vertex`() {
            val g = Factory.createPath(5)
            assertEquals(setOf(3), VertexSets.treeCenter(g))
        }

        @Test
        fun `center of star is the only vertex that is not a leaf`() {
            val g = Factory.createStar(10)
            assertEquals(setOf(1), VertexSets.treeCenter(g))
        }
    }
}