package sipura.alg

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import sipura.Samples
import sipura.alg.Connectivity.getConnectedComponent
import sipura.alg.Connectivity.shortestPath
import sipura.generate.Factory
import sipura.generate.Factory.createCompleteGraph
import sipura.generate.Factory.createPathGraph
import sipura.graphs.SimpleGraph
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ConnectivityTest {

    @Nested
    internal inner class CheckIfConnected {

        @Test
        fun `two leafs of a path5 are connected `() {
            assertTrue { Connectivity.checkIfConnected(createPathGraph(5), 1, 5) }
        }

        @Test
        fun `isolated vertex is not connected to any other vertex`() {
            val g = createPathGraph(5).apply { addVertex(6) }
            for (i in 1..5) {
                assertFalse { Connectivity.checkIfConnected(g, i, 6) }
            }
        }

        @Test
        fun `throws error if first vertex does not exist`() {
            assertThrows<IllegalArgumentException> { Connectivity.checkIfConnected(createPathGraph(5), 13, 1) }
        }

        @Test
        fun `throws error if second vertex does not exist`() {
            assertThrows<IllegalArgumentException> { Connectivity.checkIfConnected(createPathGraph(5), 1, 13) }
        }

        @Test
        fun `throws error if both vertices do not exist`() {
            assertThrows<IllegalArgumentException> { Connectivity.checkIfConnected(createPathGraph(5), 13, 42) }
        }

        @Test
        fun `vertex is connected to itself`() {
            assertTrue { Connectivity.checkIfConnected(createPathGraph(5), 2, 2) }
        }
    }

    @Nested
    internal inner class GetConnectedComponent {

        @Test
        fun `component of a vertex in path5 is the whole graph`() {
            val component = getConnectedComponent(createPathGraph(5), 1)
            assertEquals(setOf(1, 2, 3, 4, 5), component)
        }

        @Test
        fun `result can be modified`() {
            val component: MutableSet<Int> = getConnectedComponent(createPathGraph(5), 1)
            assertTrue { 3 in component }
            component.remove(3)
            assertFalse { 3 in component }
            assertTrue { 3 in createPathGraph(5) } // graph is unchanged
        }

        @Test
        fun `does not include other components`() {
            var component = getConnectedComponent(Samples.star4Plus1IsolatedVertex(), 2)
            assertEquals(setOf(1, 2, 3, 4), component) // vertex 5 is not included

            component = getConnectedComponent(Samples.star4Plus1IsolatedVertex(), 5)
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
                6 to componentC
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
    internal inner class NumberOfConnectedComponents {

        @Test
        fun `empty graph has 0 components`() {
            val g = SimpleGraph<Int>() // is empty
            assertEquals(0, Connectivity.numberOfConnectedComponents(g))
        }

        @Test
        fun `path5 has one component`() {
            assertEquals(1, Connectivity.numberOfConnectedComponents(createPathGraph(5)))
        }

        @Test
        fun `check that union of path3, path2 and one isolated vertex has 3 components`() {
            val g = SimpleGraph<Int>()
            for (v in 1..6) g.addVertex(v)
            g.addEdge(1, 2)
            g.addEdge(2, 3)
            g.addEdge(4, 5)

            val res = Connectivity.numberOfConnectedComponents(g)
            assertEquals(3, res)
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
            assertTrue { Connectivity.isConnected(createPathGraph(5)) }
        }

        @Test
        fun `star4 with one isolated vertex is not connected`() {
            assertFalse { Connectivity.isConnected(Samples.star4Plus1IsolatedVertex()) }
        }
    }

    @Nested
    internal inner class Distance {

        @Test
        fun `distance of leafs in path5 is 4`() {
            assertEquals(4, Connectivity.distance(createPathGraph(5), 1, 5))
        }

        @Test
        fun `distance of vertex to itself is 0`() {
            assertEquals(0, Connectivity.distance(createPathGraph(5), 2, 2))
        }

        @Test
        fun `distance of neighbours is 1`() {
            assertEquals(1, Connectivity.distance(createPathGraph(5), 3, 4))
        }

        @Test
        fun `distance of two corners of star is 2`() {
            assertEquals(2, Connectivity.distance(Samples.star4Plus1IsolatedVertex(), 2, 3))
        }

        @Test
        fun `distance of some inner vertices in path100 is correct`() {
            assertEquals(52 - 37, Connectivity.distance(createPathGraph(100), 37, 52))
        }

        @Test
        fun `distance of any two vertices in complete graph is 1`() {
            val g = createCompleteGraph(10)
            for (v1 in g.V) {
                for (v2 in g.V) {
                    if (v1 != v2) {
                        assertEquals(1, Connectivity.distance(g, v1, v2))
                    }
                }
            }
        }

        @Test
        fun `throws exception if no connecting path exists`() {
            assertThrows<IllegalStateException> {
                Connectivity.distance(
                    Samples.star4Plus1IsolatedVertex(),
                    1,
                    5
                )
            }
        }
    }

    @Nested
    internal inner class ShortestPath {

        @Test
        fun `shortest path between leafs in path5 is 1-2-3-4-5`() {
            assertEquals(listOf(1, 2, 3, 4, 5), shortestPath(createPathGraph(5), 1, 5))
        }

        @Test
        fun `shortest path between corners of star goes through center`() {
            assertEquals(
                listOf(2, 1, 3),
                shortestPath(
                    Samples.star4Plus1IsolatedVertex(),
                    2,
                    3
                )
            )
        }

        @Test
        fun `shortest path in complete graph is just one edge`() {
            assertEquals(listOf(2, 5), shortestPath(createCompleteGraph(6), 2, 5))
        }

        @Test
        fun `cycle5 has paths of length 2 and 3`() {
            assertEquals(listOf(5, 1, 2), shortestPath(Factory.createCycleGraph(5), 5, 2))
        }

        @Test
        fun `throws exception if disconnected`() {
            assertThrows<IllegalStateException> {
                shortestPath(
                    Samples.star4Plus1IsolatedVertex(),
                    1,
                    5
                )
            }
        }

        @Test
        fun `shortest path from a vertex to itself is just the vertex`() {
            assertEquals(listOf(2), shortestPath(createPathGraph(5), 2, 2))
        }
    }
}
