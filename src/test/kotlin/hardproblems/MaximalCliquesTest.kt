package hardproblems

import graphs.SimpleGraph
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MaximalCliquesTest {

    @Test
    fun `test 1`() {
        val g = SimpleGraph<Int>()
        for (v in 1..7) g.addVertex(v)
        g.addEdge(1, 2)
        g.addEdge(1, 3)
        g.addEdge(1, 4)
        g.addEdge(2, 3)
        g.addEdge(2, 4)
        g.addEdge(2, 5)
        g.addEdge(3, 4)
        g.addEdge(4, 5)
        g.addEdge(4, 6)
        g.addEdge(5, 7)

        val correctCliques = setOf(
            setOf(1, 2, 3, 4),
            setOf(2, 4, 5),
            setOf(5, 7),
            setOf(4, 6)
        )

        assertEquals(correctCliques, MaximalCliques.listMaximalCliques(g))
    }
}
