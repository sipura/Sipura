import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


internal class SimpleGraphTest {

    lateinit var g: SimpleGraph<Int>
    lateinit var m: HashMap<Int, MutableSet<Int>>

    @BeforeEach
    fun setup() {
        g = SimpleGraph()
        // create test graph with vertices { 1, 2, 3, 4, 5, 6 }
        // and edges { (1,2), (1,3), (3,4), (3,5) }
        m = HashMap()
        m[1] = mutableSetOf(2, 3)
        m[2] = mutableSetOf(1)
        m[3] = mutableSetOf(1, 4, 5)
        m[4] = mutableSetOf(3)
        m[5] = mutableSetOf(3)
        m[6] = mutableSetOf() // isolated vertex
        setFieldValue("m", g, g.javaClass, m)
    }

    private fun assertVerticesExist(vararg verts: Int) {
        for (v in verts) {
            assertContains(m, v)
        }
    }

    private fun assertVerticesDontExist(vararg verts: Int) {
        for (v in verts) {
            assertTrue { v !in m }
        }
    }

    private fun assertEdgesExist(vararg edges: Pair<Int, Int>) {
        for ((v1, v2) in edges) {
            assertContains(m, v1)
            assertContains(m, v2)
            assertTrue { v2 in m[v1]!! }
            assertTrue { v1 in m[v2]!! }
        }
    }

    private fun assertEdgesDontExist(vararg edges: Pair<Int, Int>) {
        for ((v1, v2) in edges) {
            if (v1 in m) {
                assertTrue { v2 !in m[v1]!! }
            }
            if (v2 in m) {
                assertTrue { v1 !in m[v2]!! }
            }
        }
    }

    private fun assertEdgeCountEquals(expectedEdgeCount: Int) {
        assertEquals(expectedEdgeCount, m.values.sumOf { it.size } / 2)
    }

    @Test
    fun `add new vertex`() {
        assertTrue { g.addVertex(7) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6, 7)
        assertEquals(7, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
        assertEquals(0, m[7]!!.size)
    }

    @Test
    fun `add existing vertex`() {
        assertFalse { g.addVertex(1) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove existing isolated vertex`() {
        assertTrue { g.removeVertex(6) }
        // check state of the graph
        assertVerticesExist(1, 2, 3, 4, 5)
        assertEquals(5, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove existing vertex that has neighbors`() {
        assertTrue { g.removeVertex(3) }
        // check state of the graph
        assertVerticesExist(1, 2, 4, 5, 6)
        assertEquals(5, m.size)
        assertEdgesExist(Pair(1,2))
        assertEdgeCountEquals(1)
    }

    @Test
    fun `remove vertex that is not part of the graph`() {
        assertFalse { g.removeVertex(7) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `check if vertex is contained that is not there`() {
        assertFalse { 7 in g }
    }

    @Test
    fun `check if vertex is contained that is present`() {
        assertTrue{ 1 in g }
    }

    @Test
    fun `checking degree of missing vertex throws exception`() {
        assertThrows<IllegalArgumentException> { g.degreeOf(7) }
    }

    @Test
    fun `degree of isolated vertex is 0`() {
        assertEquals(0, g.degreeOf(6))
    }

    @Test
    fun `check degrees of vertices with neighbors`() {
        assertEquals(2, g.degreeOf(1))
        assertEquals(1, g.degreeOf(2))
        assertEquals(3, g.degreeOf(3))
        assertEquals(1, g.degreeOf(4))
        assertEquals(1, g.degreeOf(5))
    }

    @Test
    fun `add edge between existing vertices`() {
        assertTrue { g.addEdge(1, 6) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5), Pair(1,6))
        assertEdgeCountEquals(5)
    }

    @Test
    fun `add edge between vertices that both do not exist`() {
        assertThrows<IllegalArgumentException> { g.addEdge(7, 8) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `add edge between vertices where the first one does not exist`() {
        assertThrows<IllegalArgumentException> { g.addEdge(7, 1) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `add edge between vertices where the second one does not exist`() {
        assertThrows<IllegalArgumentException> { g.addEdge(1, 7) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `add edge that already exists`() {
        assertFalse { g.addEdge(1, 2) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove existing edge`() {
        assertTrue { g.removeEdge(1, 2) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(3)
    }

    @Test
    fun `remove edge that does not exist between two existing vertices`() {
        assertFalse { g.removeEdge(1, 6) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove edge that does not exist between two vertices that do not exist`() {
        assertThrows<IllegalArgumentException> { g.removeEdge(7, 8) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove edge that does not exist between two vertices where the first one does not exist`() {
        assertThrows<IllegalArgumentException> { g.removeEdge(7, 1) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove edge that does not exist between two vertices where the second one does not exist`() {
        assertThrows<IllegalArgumentException> { g.removeEdge(1, 7) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, m.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }
}