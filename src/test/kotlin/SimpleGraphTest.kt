import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


internal class SimpleGraphTest {

    @BeforeEach
    fun setup() {

    }

    @Test
    fun `add New Vertex`() {
        val g = SimpleGraph<Int>()
        assertTrue { g.addVertex(1) }
        val m = getFieldValue<SimpleGraph<Int>, HashMap<Int, MutableSet<Int>>>("m", g, g.javaClass)
        assertContains(m, 1)
        assertEquals(1, m.size)
    }

    @Test
    fun `add Existing Vertex`() {
        val g = SimpleGraph<Int>()
        g.addVertex(1)
        assertFalse { g.addVertex(1) }
        val m = getFieldValue<SimpleGraph<Int>, HashMap<Int, MutableSet<Int>>>("m", g, g.javaClass)
        assertContains(m, 1)
        assertEquals(1, m.size)
    }

    @Test
    fun `remove Existing Vertex`() {
        val g = SimpleGraph<Int>()
        g.addVertex(1)
        assertTrue { g.removeVertex(1) }
        val m = getFieldValue<SimpleGraph<Int>, HashMap<Int, MutableSet<Int>>>("m", g, g.javaClass)
        assertTrue { 1 !in m }
        assertEquals(0, m.size)
    }

    @Test
    fun `remove vertex that is not part of the graph`() {
        val g = SimpleGraph<Int>()
        g.addVertex(1)
        assertFalse { g.removeVertex(2) }
        val m = getFieldValue<SimpleGraph<Int>, HashMap<Int, MutableSet<Int>>>("m", g, g.javaClass)
        assertTrue { 1 in m }
        assertTrue { 2 !in m }
        assertEquals(1, m.size)
    }
}