package sipura.alg

import sipura.graphs.SimpleGraph
import java.util.*

/**
 * Provides different methods for traversing graphs like Breadth First Search (BFS) or Depth First Search (DFS).
 */
object Traversal {

    /**
     * Creates an iterator that iterates over the vertices in [g] in Breadth-first search (BFS) order starting in the
     * vertex [v].
     *
     * If the graph is not connected the iterator will only iterate over the vertices that are in the same connected
     * component as [v].
     *
     * @throws IllegalArgumentException if [v] is not a vertex in [g].
     *
     * @return the BFS iterator.
     *
     * @see <a href=https://en.wikipedia.org/wiki/Breadth-first_search>Wikipedia: Breadth-first search</a>
     */
    fun <V> breadthFirstSearchIterator(g: SimpleGraph<V>, v: V): Iterator<V> {
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

    /**
     * Creates an iterator that iterates over subsets of the vertices in [g] where all vertices in a subset have the
     * same distance to [v]. The iterator iterates over the subsets in the order of the smallest distance to the largest
     * distance.
     *
     * If the graph is not connected the iterator will only iterate over subsets of vertices that are in the same
     * connected component as [v].
     *
     * @throws IllegalArgumentException if [v] is not a vertex in [g].
     *
     * @return the BFS layer iterator.
     *
     * @see <a href=https://en.wikipedia.org/wiki/Breadth-first_search>Wikipedia: Breadth-first search</a>
     */
    fun <V> breadthFirstSearchLayerIterator(g: SimpleGraph<V>, v: V): Iterator<MutableSet<V>> {
        if (v !in g.V) throw IllegalArgumentException("Graph does not contain vertex v")

        return object : Iterator<MutableSet<V>> {

            var queue = LinkedList(listOf(v))
            val seen = mutableSetOf(v)

            override fun hasNext(): Boolean = queue.isNotEmpty()

            override fun next(): MutableSet<V> {
                if (!hasNext()) throw NoSuchElementException("Traversal has finished")

                val res = HashSet<V>(queue)
                val queueNew = LinkedList<V>()

                for (q in queue) {
                    for (nb in g.neighbors(q)) {
                        if (nb !in seen) {
                            queueNew.addLast(nb)
                            seen.add(nb)
                        }
                    }
                }
                queue = queueNew
                return res
            }
        }
    }

    /**
     * Creates an iterator that iterates over the vertices in [g] in Depth-first search (DFS) order starting in the
     * vertex [v].
     *
     * If the graph is not connected the iterator will only iterate over the vertices that are in the same connected
     * component as [v].
     *
     * @throws IllegalArgumentException if [v] is not a vertex in [g].
     *
     * @return the DFS iterator.
     *
     * @see <a href=https://en.wikipedia.org/wiki/Depth-first_search>Wikipedia: Depth-first search</a>
     */
    fun <V> depthFirstSearchIterator(g: SimpleGraph<V>, v: V): Iterator<V> {
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
                        queue.addFirst(nb)
                        seen.add(nb)
                    }
                }
                return next
            }
        }
    }
}
