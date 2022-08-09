import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ConnectivityTest {

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
    internal inner class CheckIfConnected {

        @Test
        fun `two leafs of a path5 are connected `() {
            assertTrue { Connectivity.checkIfConnected(path5, 1, 5) }
        }

        @Test
        fun `isolated vertex is not connected to any other vertex`() {
            path5.addVertex(6)
            for (i in 1..5) {
                assertFalse { Connectivity.checkIfConnected(path5, i, 6) }
            }
        }

        @Test
        fun `throws error if first vertex does not exist`() {
            assertThrows<IllegalArgumentException> { Connectivity.checkIfConnected(path5, 13, 1) }
        }

        @Test
        fun `throws error if second vertex does not exist`() {
            assertThrows<IllegalArgumentException> { Connectivity.checkIfConnected(path5, 1, 13) }
        }

        @Test
        fun `throws error if both vertices do not exist`() {
            assertThrows<IllegalArgumentException> { Connectivity.checkIfConnected(path5, 13, 42) }
        }

        @Test
        fun `vertex is connected to itself`() {
            assertTrue { Connectivity.checkIfConnected(path5, 2,2) }
        }
    }

    @Nested
    internal inner class GetConnectedComponent {

        @Test
        fun `component of a vertex in path5 is the whole graph`() {
            val component = Connectivity.getConnectedComponent(path5, 1)
            assertEquals(setOf(1, 2, 3, 4, 5), component)
        }

        @Test
        fun `result can be modified`() {
            val component: MutableSet<Int> = Connectivity.getConnectedComponent(path5, 1)
            assertTrue { 3 in component }
            component.remove(3)
            assertFalse { 3 in component }
            assertTrue { 3 in path5 } // graph is unchanged
        }

        @Test
        fun `does not include other components`() {
            var component: MutableSet<Int> = Connectivity.getConnectedComponent(star4plus1, 2)
            assertEquals(setOf(1, 2, 3, 4), component) // vertex 5 is not included

            component= Connectivity.getConnectedComponent(star4plus1, 5)
            assertEquals(setOf(5), component) // vertex 5 is isolated
        }
    }

    @Nested
    internal inner class ListAllConnectedComponents {

        @Test
        fun `empty graph`() {
            val g = SimpleGraph<Int>() // is empty
            assertEquals(HashMap(), Connectivity.listAllConnectedComponents(g))
        }

        @Test
        fun `check for union of path3, path2 and one isolated vertex`() {

            val g = SimpleGraph<Int>()
            for (v in 1..6) g.addVertex(v)
            g.addEdge(1, 2)
            g.addEdge(2, 3)
            g.addEdge(4, 5)

            val componentA = setOf(1, 2, 3)
            val componentB = setOf(4, 5)
            val componentC = setOf(6)
            val correctMap = mapOf(
                1 to componentA,
                2 to componentA,
                3 to componentA,
                4 to componentB,
                5 to componentB,
                6 to componentC,
            )

            val res = Connectivity.listAllConnectedComponents(g)
            assertEquals(correctMap, res)

            // the set they point to are also the same object
            assertSame(res[1], res[2])
            assertSame(res[2], res[3])
            assertSame(res[4], res[5])
        }

    }

    @Nested
    internal inner class IsConnected {

        @Test
        fun `exception for empty graph`() {
            assertThrows<IllegalArgumentException> { Connectivity.isConnected(SimpleGraph<Int>()) }
        }

        @Test
        fun `path5 is connected`() {
            assertTrue { Connectivity.isConnected(path5) }
        }

        @Test
        fun `star4 with one isolated vertex is not connected`() {
            assertFalse { Connectivity.isConnected(star4plus1) }
        }
    }

    @Nested
    internal inner class IsTree {

        @Test
        fun `one isolated vertex is a (trivial) tree`() {
            val g = SimpleGraph<Int>()
            g.addVertex(7)
            assertTrue { Connectivity.isTree(g) }
        }

        @Test
        fun `triangle is not a tree`() {
            assertFalse { Connectivity.isTree(Factory.createCycle(3)) }
        }

        @Test
        fun `path5 is a tree`() {
            assertTrue { Connectivity.isTree(path5) }
        }

        @Test
        fun `star4 with one isolated vertex is not a tree`() {
            assertFalse { Connectivity.isTree(star4plus1) }
        }

        @Test
        fun `bipartite graph of size (4, 7) is not a tree`() {
            assertFalse { Connectivity.isTree(Factory.createBipartite(4, 7)) }
        }
    }

}
