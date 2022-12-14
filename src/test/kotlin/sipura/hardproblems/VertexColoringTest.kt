package sipura.hardproblems

import org.junit.jupiter.api.Test
import sipura.generate.Factory
import sipura.hardproblems.VertexColoring.hasKColoring
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class VertexColoringTest {

    @Test
    fun `line4 has 2-coloring`() {
        for (k in 2..10)
            assertTrue { hasKColoring(Factory.createPathGraph(4), k) }
    }

    @Test
    fun `cycle5 has no 2-coloring`() {
        assertFalse { hasKColoring(Factory.createCycleGraph(5), 2) }
    }

    @Test
    fun `complete6 has only 6 coloring`() {
        for (k in 0..5)
            assertFalse { hasKColoring(Factory.createCompleteGraph(6), k) }
        for (k in 6..15)
            assertTrue { hasKColoring(Factory.createCompleteGraph(6), k) }
    }

    @Test
    fun `line25 has 2-coloring`() {
        for (k in 0..1)
            assertFalse { hasKColoring(Factory.createPathGraph(25), k) }
        for (k in 2..10)
            assertTrue { hasKColoring(Factory.createPathGraph(25), k) }
    }
}
