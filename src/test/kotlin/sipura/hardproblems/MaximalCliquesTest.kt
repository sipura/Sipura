package sipura.hardproblems

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import sipura.generate.Factory
import sipura.graphs.SimpleGraph

internal class MaximalCliquesTest {

    @Test
    fun `does maximalCliqueIterator iterate over all maximal cliques 1`() {
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
            setOf(4, 6),
        )
        assertEquals(correctCliques, MaximalCliques.maximalCliqueIterator(g).asSequence().toSet())
    }

    @Test
    fun `does maximalCliqueIterator iterate over all maximal cliques 2 star graph`() {
        val g = Factory.createStarGraph(5)
        val correctCliques = setOf(
            setOf(1, 2),
            setOf(1, 3),
            setOf(1, 4),
            setOf(1, 5),
        )
        assertEquals(correctCliques, MaximalCliques.maximalCliqueIterator(g).asSequence().toSet())
    }

    @Test
    fun `does maximalCliqueIterator iterate over all maximal cliques 3 complete graph`() {
        val g = Factory.createCompleteGraph(10)
        val correctCliques = setOf(
            g.V.toSet(),
        )
        assertEquals(correctCliques, MaximalCliques.maximalCliqueIterator(g).asSequence().toSet())
    }

    @Test
    fun `does maximalCliqueIterator iterate over all maximal cliques 4 complete bipartite graph`() {
        val g = Factory.createBipartiteGraph(4, 4)
        val correctCliques = setOf(
            setOf(1, 5), setOf(1, 6), setOf(1, 7), setOf(1, 8),
            setOf(2, 5), setOf(2, 6), setOf(2, 7), setOf(2, 8),
            setOf(3, 5), setOf(3, 6), setOf(3, 7), setOf(3, 8),
            setOf(4, 5), setOf(4, 6), setOf(4, 7), setOf(4, 8),
        )
        assertEquals(correctCliques, MaximalCliques.maximalCliqueIterator(g).asSequence().toSet())
    }

    @Test
    fun `does maximalCliqueIterator iterate over all maximal cliques 5 complete split graph`() {
        val g = Factory.createBipartiteGraph(4, 4)
        g.addEdge(1, 2)
        g.addEdge(1, 3)
        g.addEdge(1, 4)
        g.addEdge(2, 3)
        g.addEdge(2, 4)
        g.addEdge(3, 4)
        val correctCliques = setOf(
            setOf(1, 2, 3, 4, 5),
            setOf(1, 2, 3, 4, 6),
            setOf(1, 2, 3, 4, 7),
            setOf(1, 2, 3, 4, 8),
        )
        assertEquals(correctCliques, MaximalCliques.maximalCliqueIterator(g).asSequence().toSet())
    }
}
