object VertexSets {

    fun <V> getLeafs(g: SimpleGraph<V>): MutableSet<V> =
        g.V.filterTo(HashSet()) { g.degreeOf(it) == 1 }

    /**
     * assumes that [g] is a tree.
     *
     * Works by creating a copy of [g] and then repeatedly
     * removing all leafs of [g], until only the center remains.
     */
    fun <V> treeCenter(g: SimpleGraph<V>): MutableSet<V> {
        val copy = g.copy()
        var leafs = HashSet(getLeafs(copy))

        while (copy.n > 2) {
            val newLeafs = HashSet<V>()
            for (leaf in leafs) {
                newLeafs.addAll(copy.neighbors(leaf)) // has just 1 neighbour ...
                copy.removeVertex(leaf)
            }
            leafs = newLeafs
        }

        return copy.V.toMutableSet()
    }
}