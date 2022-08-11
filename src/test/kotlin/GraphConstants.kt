import graphs.SimpleGraph

object GraphConstants {

    fun `star4 plus 1 isolated vertex`(): SimpleGraph<Int> {
        val star4plus1 = SimpleGraph<Int>()
        star4plus1.addVertex(1)
        star4plus1.addVertex(2)
        star4plus1.addVertex(3)
        star4plus1.addVertex(4)
        star4plus1.addVertex(5)
        star4plus1.addEdge(1, 2)
        star4plus1.addEdge(1, 3)
        star4plus1.addEdge(1, 4)
        return star4plus1
    }
}