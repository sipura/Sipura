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
        m = getFieldValue("m", g, g.javaClass)
    }

    @Test
    fun `add new vertex`() {
        assertTrue { g.addVertex(1) }
        assertContains(m, 1)
        assertEquals(1, m.size)
        assertEquals(0, m[1]!!.size)
    }

    @Test
    fun `add existing vertex`() {
        g.addVertex(1)
        assertFalse { g.addVertex(1) }
        assertContains(m, 1)
        assertEquals(1, m.size)
    }

    @Test
    fun `remove existing vertex`() {
        g.addVertex(1)
        assertTrue { g.removeVertex(1) }
        assertTrue { 1 !in m }
        assertEquals(0, m.size)
    }

    @Test
    fun `remove vertex that is not part of the graph`() {
        g.addVertex(1)
        assertFalse { g.removeVertex(2) }
        assertTrue { 1 in m }
        assertTrue { 2 !in m }
        assertEquals(1, m.size)
    }

    @Test
    fun `check if vertex is contained that is not there`() {
        assertFalse { 1 in g }
    }

    @Test
    fun `check if vertex is contained that is present`() {
        g.addVertex(7)
        assertTrue{ 7 in g }
    }

    @Test
    fun `checking degree of missing vertex throws exception`() {
        assertThrows<IllegalArgumentException> { g.degreeOf(3) }
    }

    @Test
    fun `degree of isolated vertex is 0`() {
        g.addVertex(7)
        assertEquals(0, g.degreeOf(7))
    }

    @Test
    fun `check degrees in path of length 3`() {
        g.addVertex(1)
        g.addVertex(2)
        g.addVertex(3)
        g.addEdge(1,2)
        g.addEdge(2,3)

        assertEquals(1, g.degreeOf(1))
        assertEquals(2, g.degreeOf(2))
        assertEquals(1, g.degreeOf(3))

    }

    @Test
    fun `add edge between existing vertices`() {
        g.addVertex(1)
        g.addVertex(2)
        assertTrue { g.addEdge(1, 2) }
        assertContains(m[1]!!, 2)
        assertContains(m[2]!!, 1)
    }

    @Test
    fun `add edge between vertices that both do not exist`() {
        assertThrows<IllegalArgumentException> { g.addEdge(1, 2) }
        assertTrue { 1 !in m }
        assertTrue { 2 !in m }
        assertEquals(0, m.size)
    }

    @Test
    fun `add edge between vertices where the first one does not exist`() {
        g.addVertex(1)
        assertThrows<IllegalArgumentException> { g.addEdge(2, 1) }
        assertTrue { 1 in m }
        assertTrue { 2 !in m }
        assertEquals(1, m.size)
        assertTrue { 2 !in m[1]!! }
    }

    @Test
    fun `add edge between vertices where the second one does not exist`() {
        g.addVertex(1)
        assertThrows<IllegalArgumentException> { g.addEdge(1, 2) }
        assertTrue { 1 in m }
        assertTrue { 2 !in m }
        assertEquals(1, m.size)
        assertTrue { 2 !in m[1]!! }
    }

    @Test
    fun `add edge that already exists`() {
        g.addVertex(1)
        g.addVertex(2)
        g.addEdge(1, 2)
        assertFalse { g.addEdge(1, 2) }
        assertContains(m[1]!!, 2)
        assertContains(m[2]!!, 1)
    }

    @Test
    fun `remove existing edge`() {
        g.addVertex(1)
        g.addVertex(2)
        g.addEdge(1, 2)
        assertTrue { g.removeEdge(1, 2) }
        assertContains(m, 1)
        assertContains(m, 2)
        assertTrue { 2 !in m[1]!! }
        assertTrue { 1 !in m[2]!! }
    }

    @Test
    fun `remove edge that does not exist between two existing vertices`() {
        g.addVertex(1)
        g.addVertex(2)
        assertFalse { g.removeEdge(1, 2) }
        assertContains(m, 1)
        assertContains(m, 2)
        assertTrue { 2 !in m[1]!! }
        assertTrue { 1 !in m[2]!! }
    }

    @Test
    fun `remove edge that does not exist between two vertices that do not exist`() {
        assertThrows<IllegalArgumentException> { g.removeEdge(1, 2) }
        assertTrue { 1 !in m }
        assertTrue { 2 !in m }
    }

    @Test
    fun `remove edge that does not exist between two vertices where the first one does not exist`() {
        g.addVertex(2)
        assertThrows<IllegalArgumentException> { g.removeEdge(1, 2) }
        assertTrue { 1 !in m }
        assertContains(m, 2)
        assertTrue { 1 !in m[2]!! }
    }

    @Test
    fun `remove edge that does not exist between two vertices where the second one does not exist`() {
        g.addVertex(2)
        assertThrows<IllegalArgumentException> { g.removeEdge(2, 1) }
        assertTrue { 1 !in m }
        assertContains(m, 2)
        assertTrue { 1 !in m[2]!! }
    }
}