package sipura.hardproblems

import org.junit.jupiter.api.Test
import sipura.graphs.SimpleGraph
import kotlin.test.assertEquals

class SubgraphIsomorphismTest {
    @Test
    fun `small subgraph test`() {
        val pattern = SimpleGraph<Int>()
        for (i in 1..3) {
            pattern.addVertex(i)
        }
        pattern.addEdge(1, 2)
        pattern.addEdge(1, 3)
        pattern.addEdge(2, 3)
        val data = SimpleGraph<Int>()
        for (i in 1..7) {
            data.addVertex(i)
        }
        data.addEdge(1, 3)
        data.addEdge(2, 3)
        data.addEdge(3, 5)
        data.addEdge(3, 6)
        data.addEdge(4, 5)
        data.addEdge(5, 6)
        data.addEdge(5, 7)
        data.addEdge(6, 7)

        val l1 = HashMap<Int, Int>()
        val l2 = HashMap<Int, Int>()
        for (i in 1..3) l1[i] = 1
        for (i in 1..7) l2[i] = 1
        l1[1] = 2
        l2[3] = 2

        val res = SubgraphIsomorphism.getAllSubgraphIsomorphism(pattern, data, l1, l2)
        assertEquals(res.size, 2)
        val it = SubgraphIsomorphism.subgraphIsomorphismIterator(pattern, data, l1, l2).asSequence()
        assertEquals(res.size, it.count())
    }

    @Test
    fun `small subgraph test with two components`() {
        val pattern = SimpleGraph<Int>()
        for (i in 1..3) {
            pattern.addVertex(i)
        }
        pattern.addVertex(4)
        pattern.addEdge(1, 2)
        pattern.addEdge(1, 3)
        pattern.addEdge(2, 3)
        val data = SimpleGraph<Int>()
        for (i in 1..7) {
            data.addVertex(i)
        }
        data.addEdge(1, 3)
        data.addEdge(2, 3)
        data.addEdge(3, 5)
        data.addEdge(3, 6)
        data.addEdge(4, 5)
        data.addEdge(5, 6)
        data.addEdge(5, 7)
        data.addEdge(6, 7)

        val l1 = HashMap<Int, Int>()
        val l2 = HashMap<Int, Int>()
        for (i in 1..4) l1[i] = 1
        for (i in 1..7) l2[i] = 1
        l1[1] = 2
        l2[7] = 2

        val res = SubgraphIsomorphism.getAllSubgraphIsomorphism(pattern, data, l1, l2)
        assertEquals(res.size, 4)
        val it = SubgraphIsomorphism.subgraphIsomorphismIterator(pattern, data, l1, l2).asSequence()
        assertEquals(res.size, it.count())
    }

    @Test
    fun `undirected vf3-paper graph test`() {
        val g1 = SimpleGraph<Int>()
        for (i in 1..5) g1.addVertex(i)
        g1.addEdge(1, 2)
        g1.addEdge(1, 5)
        g1.addEdge(2, 3)
        g1.addEdge(2, 4)
        g1.addEdge(2, 5)
        g1.addEdge(3, 4)
        g1.addEdge(4, 5)
        val l1 = HashMap<Int, Int>()
        val l1Labels = intArrayOf(4, 1, 2, 4, 3)
        for (i in 1..5) l1[i] = l1Labels[i - 1]

        val g2 = SimpleGraph<Int>()
        for (i in 1..13) g2.addVertex(i)
        g2.addEdge(1, 2)
        g2.addEdge(1, 12)
        g2.addEdge(2, 3)
        g2.addEdge(2, 12)
        g2.addEdge(2, 13)
        g2.addEdge(3, 4)
        g2.addEdge(3, 13)
        g2.addEdge(4, 5)
        g2.addEdge(4, 6)
        g2.addEdge(4, 13)
        g2.addEdge(5, 6)
        g2.addEdge(6, 7)
        g2.addEdge(6, 13)
        g2.addEdge(7, 8)
        g2.addEdge(8, 9)
        g2.addEdge(8, 13)
        g2.addEdge(9, 13)
        g2.addEdge(9, 10)
        g2.addEdge(10, 11)
        g2.addEdge(10, 12)
        g2.addEdge(11, 12)
        g2.addEdge(12, 13)
        val l2 = HashMap<Int, Int>()
        val l2Labels = intArrayOf(3, 1, 2, 1, 4, 3, 4, 1, 2, 1, 4, 3, 4)
        for (i in 1..13) l2[i] = l2Labels[i - 1]

        val res = SubgraphIsomorphism.getAllSubgraphIsomorphism(g1, g2, l1, l2)
        assertEquals(res.size, 1)
        val it = SubgraphIsomorphism.subgraphIsomorphismIterator(g1, g2, l1, l2).asSequence()
        assertEquals(res.size, it.count())
    }
}
