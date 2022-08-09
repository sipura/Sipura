import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GraphTraitTest {

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
    internal inner class IsTree {

        @Test
        fun `one isolated vertex is a (trivial) tree`() {
            val g = SimpleGraph<Int>()
            g.addVertex(7)
            kotlin.test.assertTrue { GraphTrait.isTree(g) }
        }

        @Test
        fun `triangle is not a tree`() {
            assertFalse { GraphTrait.isTree(Factory.createCycle(3)) }
        }

        @Test
        fun `path5 is a tree`() {
            kotlin.test.assertTrue { GraphTrait.isTree(path5) }
        }

        @Test
        fun `star4 with one isolated vertex is not a tree`() {
            assertFalse { GraphTrait.isTree(star4plus1) }
        }

        @Test
        fun `bipartite graph of size (4, 7) is not a tree`() {
            assertFalse { GraphTrait.isTree(Factory.createBipartite(4, 7)) }
        }
    }

    @Nested
    internal inner class IsAcyclic {

        @Test
        fun `empty graph is acyclic`() {
            assertTrue { GraphTrait.isAcyclic(SimpleGraph<Int>()) }
        }

        @Test
        fun `graph with single connectivity component is acyclic`() {
            assertTrue { GraphTrait.isAcyclic(path5) }
        }

        @Test
        fun `graph with single connectivity component is not acyclic`() {
            path5.addEdge(5,1)
            assertFalse { GraphTrait.isAcyclic(path5) }
        }

        @Test
        fun `graph with multiple connectivity components is acyclic`() {
            assertTrue { GraphTrait.isAcyclic(star4plus1) }
        }

        @Test
        fun `graph with multiple connectivity components is not acyclic if one of them is not acyclic`() {
            star4plus1.addEdge(2,3)
            assertFalse { GraphTrait.isAcyclic(star4plus1) }
        }

        @Test
        fun `graph with multiple connectivity components is not acyclic if all of them are not acyclic`() {
            star4plus1.addEdge(2,3)
            star4plus1.addVertex(6)
            star4plus1.addVertex(7)
            star4plus1.addEdge(5,6)
            star4plus1.addEdge(5,7)
            star4plus1.addEdge(6,7)
            assertFalse { GraphTrait.isAcyclic(star4plus1) }
        }


    }

    @Nested
    internal inner class IsKRegular {

        @Test
        fun `path5 is not 2-regular`() {
            assertFalse { GraphTrait.iskRegular(path5, 2) }
        }

        @Test
        fun `normal cube is 3-regular aka cubic`() {
            val cube = SimpleGraph<Int>()
            (1..8).forEach { cube.addVertex(it) }

            cube.addEdge(1,2)
            cube.addEdge(1,3)
            cube.addEdge(2,4)
            cube.addEdge(3,4)

            cube.addEdge(1,5)
            cube.addEdge(2,6)
            cube.addEdge(3,7)
            cube.addEdge(4,8)

            cube.addEdge(5,6)
            cube.addEdge(5,7)
            cube.addEdge(6,8)
            cube.addEdge(7,8)

            assertTrue  { GraphTrait.iskRegular(cube, 3) }
        }

        @Test
        fun `cycle5 is 2-regular`() {
            val g = Factory.createCycle(5)
            assertTrue  { GraphTrait.iskRegular(g, 2) }
        }

        @Test
        fun `throws exception for k equal -2`() {
            val g = Factory.createCycle(5)
            assertThrows<IllegalArgumentException>  { GraphTrait.iskRegular(g, -2) }
        }

    }


    @Nested
    internal inner class IsCubic {

        @Test
        fun `path5 is not cubic`() {
            assertFalse { GraphTrait.isCubic(path5) }
        }

        @Test
        fun `normal cube is cubic`() {
            val cube = SimpleGraph<Int>()
            (1..8).forEach { cube.addVertex(it) }

            cube.addEdge(1,2)
            cube.addEdge(1,3)
            cube.addEdge(2,4)
            cube.addEdge(3,4)

            cube.addEdge(1,5)
            cube.addEdge(2,6)
            cube.addEdge(3,7)
            cube.addEdge(4,8)

            cube.addEdge(5,6)
            cube.addEdge(5,7)
            cube.addEdge(6,8)
            cube.addEdge(7,8)

            assertTrue  { GraphTrait.isCubic(cube) }
        }

        @Test
        fun `complete4 is cubic`() {
            val g = SimpleGraph<Int>()
            (1..4).forEach { g.addVertex(it) }

            g.addEdge(1,2)
            g.addEdge(1,3)
            g.addEdge(1,4)
            g.addEdge(2,3)
            g.addEdge(2,4)
            g.addEdge(3,4)

            assertTrue  { GraphTrait.isCubic(g) }
        }

        @Test
        fun `empty graph is not cubic`() {
            assertThrows<IllegalArgumentException>{GraphTrait.isCubic(SimpleGraph<Int>())}
        }
    }
}