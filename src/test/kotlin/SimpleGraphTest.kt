import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


internal class SimpleGraphTest {

    lateinit var g: SimpleGraph<Int>
    lateinit var map: HashMap<Int, MutableSet<Int>>

    @BeforeEach
    fun setup() {
        g = SimpleGraph()
        // create test graph with vertices { 1, 2, 3, 4, 5, 6 }
        // and edges { (1,2), (1,3), (3,4), (3,5) }
        map = HashMap()
        map[1] = mutableSetOf(2, 3)
        map[2] = mutableSetOf(1)
        map[3] = mutableSetOf(1, 4, 5)
        map[4] = mutableSetOf(3)
        map[5] = mutableSetOf(3)
        map[6] = mutableSetOf() // isolated vertex
        setFieldValue("map", g, g.javaClass, map)
        setFieldValue("m", g, g.javaClass, 4)
    }

    private fun assertVerticesExist(vararg verts: Int) {
        for (v in verts) {
            assertContains(map, v)
        }
    }

    private fun assertVerticesDontExist(vararg verts: Int) {
        for (v in verts) {
            assertTrue { v !in map }
        }
    }

    private fun assertEdgesExist(vararg edges: Pair<Int, Int>) {
        for ((v1, v2) in edges) {
            assertContains(map, v1)
            assertContains(map, v2)
            assertTrue { v2 in map[v1]!! }
            assertTrue { v1 in map[v2]!! }
        }
    }

    private fun assertEdgesDontExist(vararg edges: Pair<Int, Int>) {
        for ((v1, v2) in edges) {
            if (v1 in map) {
                assertTrue { v2 !in map[v1]!! }
            }
            if (v2 in map) {
                assertTrue { v1 !in map[v2]!! }
            }
        }
    }

    private fun assertEdgeCountEquals(expectedEdgeCount: Int) {
        assertEquals(expectedEdgeCount, map.values.sumOf { it.size } / 2)
    }

    @Test
    fun `add new vertex`() {
        assertTrue { g.addVertex(7) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6, 7)
        assertEquals(7, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
        assertEquals(0, map[7]!!.size)
    }

    @Test
    fun `add existing vertex`() {
        assertFalse { g.addVertex(1) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove existing isolated vertex`() {
        assertTrue { g.removeVertex(6) }
        // check state of the graph
        assertVerticesExist(1, 2, 3, 4, 5)
        assertEquals(5, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove existing vertex that has neighbors`() {
        assertTrue { g.removeVertex(3) }
        // check state of the graph
        assertVerticesExist(1, 2, 4, 5, 6)
        assertEquals(5, map.size)
        assertEdgesExist(Pair(1,2))
        assertEdgeCountEquals(1)
    }

    @Test
    fun `remove vertex that is not part of the graph`() {
        assertFalse { g.removeVertex(7) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, map.size)
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
        assertEquals(6, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5), Pair(1,6))
        assertEdgeCountEquals(5)
    }

    @Test
    fun `add edge between vertices that both do not exist`() {
        assertThrows<IllegalArgumentException> { g.addEdge(7, 8) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `add edge between vertices where the first one does not exist`() {
        assertThrows<IllegalArgumentException> { g.addEdge(7, 1) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `add edge between vertices where the second one does not exist`() {
        assertThrows<IllegalArgumentException> { g.addEdge(1, 7) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `add edge that already exists`() {
        assertFalse { g.addEdge(1, 2) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove existing edge`() {
        assertTrue { g.removeEdge(1, 2) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, map.size)
        assertEdgesExist(Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(3)
    }

    @Test
    fun `remove edge that does not exist between two existing vertices`() {
        assertFalse { g.removeEdge(1, 6) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove edge that does not exist between two vertices that do not exist`() {
        assertThrows<IllegalArgumentException> { g.removeEdge(7, 8) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove edge that does not exist between two vertices where the first one does not exist`() {
        assertThrows<IllegalArgumentException> { g.removeEdge(7, 1) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `remove edge that does not exist between two vertices where the second one does not exist`() {
        assertThrows<IllegalArgumentException> { g.removeEdge(1, 7) }
        // check state of graph
        assertVerticesExist(1, 2, 3, 4, 5, 6)
        assertEquals(6, map.size)
        assertEdgesExist(Pair(1,2), Pair(1,3), Pair(3,4), Pair(3,5))
        assertEdgeCountEquals(4)
    }

    @Test
    fun `m is increased by 1 after adding a new edge`() {
        assertEquals(4, g.m)
        g.addEdge(1,6)
        assertEquals(5, g.m)
    }

    @Test
    fun `m is not increased when an edge is added that already exists`() {
        assertEquals(4, g.m)
        g.addEdge(1,2)
        assertEquals(4, g.m)
    }

    @Test
    fun `m is decreased by 1 after an existing edge gets removed`() {
        assertEquals(4, g.m)
        g.removeEdge(1,2)
        assertEquals(3, g.m)
    }

    @Test
    fun `m is not decreased when an edge is removed that does not exist`() {
        assertEquals(4, g.m)
        g.removeEdge(1,6)
        assertEquals(4, g.m)
    }

    @Test
    fun `m is decreased when a vertex with neighbours is removed`() {
        assertEquals(4, g.m)
        g.removeVertex(3)
        assertEquals(1, g.m)
    }

    @Test
    fun `m stays the same when an isolated vertex is removed`() {
        assertEquals(4, g.m)
        g.removeVertex(6)
        assertEquals(4, g.m)
    }

    @Test
    fun `m stays the same when an isolated vertex is added`() {
        assertEquals(4, g.m)
        g.addVertex(7)
        assertEquals(4, g.m)
    }

    @Test
    fun `edge can be checked after it is added`() {
        assertFalse { g.hasEdge(1,6) }
        g.addEdge(1,6)
        assertTrue { g.hasEdge(1,6) }
    }

    @Test
    fun `edge is gone after it gets removed`() {
        assertTrue { g.hasEdge(1,3) }
        g.removeEdge(1,3)
        assertFalse { g.hasEdge(1,3) }
    }

    @Test
    fun `edge still exists after unsuccessful addition`() {
        assertTrue { g.hasEdge(1,3) }
        g.addEdge(1,3)
        assertTrue { g.hasEdge(1,3) }
    }

    @Test
    fun `edge still does not exist after unsuccessful removal`() {
        assertFalse { g.hasEdge(1,6) }
        g.removeEdge(1,6)
        assertFalse { g.hasEdge(1,6) }
    }

    @Test
    fun `vertex count increases by 1 after a vertex is added`() {
        assertEquals(6, g.n)
        g.addVertex(7)
        assertEquals(7, g.n)
    }

    @Test
    fun `vertex count decreases by 1 after a vertex is removed`() {
        assertEquals(6, g.n)
        g.removeVertex(1)
        assertEquals(5, g.n)
    }

    @Test
    fun `adding a new vertex also adds it to V`() {
        assertFalse { 7 in g.V }
        g.addVertex(7)
        assertTrue { 7 in g.V }
    }

    @Test
    fun `adding an existing vertex does not change V`() {
        assertTrue { 6 in g.V }
        g.addVertex(6)
        assertTrue { 6 in g.V }
    }

    @Test
    fun `removing an existing vertex also removes it from V`() {
        assertTrue { 6 in g.V }
        g.removeVertex(6)
        assertFalse { 6 in g.V }
    }

    @Test
    fun `removing a vertex that does not exist does not change V`() {
        assertFalse { 7 in g.V }
        g.removeVertex(7)
        assertFalse { 7 in g.V }
    }

    @Test
    fun `forEachEdge iterates over m edges`() {
        var count = 0
        g.forEachEdge { _, _ -> count++ }
        assertEquals(g.m, count)
    }

    @Test
    fun `neighbor set of isolated vertex is empty`() {
        assertTrue { g.neighbors(6).isEmpty() }
    }

    @Test
    fun `neighbor set of leaf has one vertex`() {
        assertEquals(setOf(3), g.neighbors(5))
    }

    @Test
    fun `neighbor set of vertex with multiple neighbors contains all neighbors`() {
        assertEquals(setOf(1, 4, 5), g.neighbors(3))
    }

    @Test
    fun `neighbors throws exception if vertex does not exist`() {
        assertThrows<IllegalArgumentException> { g.neighbors(7) }
    }
}