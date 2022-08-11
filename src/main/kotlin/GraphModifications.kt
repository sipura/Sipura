object GraphModifications {

    fun <V> complementGraph(g: SimpleGraph<V>): SimpleGraph<V> {
        val complement = SimpleGraph<V>()
        for (v in g.V) complement.addVertex(v)
        for (v1 in g.V) {
            for (v2 in g.V) {
                if (v1 != v2 && !g.hasEdge(v1, v2)) {
                    complement.addEdge(v1, v2)
                }
            }
        }
        return complement
    }
}