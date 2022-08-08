import java.util.LinkedList

object Traversal {

    fun <V> bfsIterator(g: SimpleGraph<V>, v: V): Iterator<V> {

        if (v !in g.V) throw IllegalArgumentException("Graph does not contain vertex v")

        return object : Iterator<V> {

            val queue = LinkedList(listOf(v))
            val seen = mutableSetOf(v)

            override fun hasNext(): Boolean = queue.isNotEmpty()

            override fun next(): V {
                if (!hasNext()) throw NoSuchElementException("Traversal has finished")

                val next: V = queue.removeFirst()
                for (nb in g.neighbors(next)) {
                    if (nb !in seen) {
                        queue.addLast(nb)
                        seen.add(nb)
                    }
                }
                return next
            }

        }
    }
}