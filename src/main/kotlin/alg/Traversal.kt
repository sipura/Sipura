package alg

import graphs.SimpleGraph
import java.util.*

object Traversal {

    fun <V> breadthFirstSearchIterator(g: SimpleGraph<V>, v: V): Iterator<V> {

        if (v !in g.V) throw IllegalArgumentException("Graph does not contain vertex v")

        return object : Iterator<V> {

            val queue = LinkedList(listOf(v))
            val seen = mutableSetOf(v)

            override fun hasNext(): Boolean = queue.isNotEmpty()

            override fun next(): V {
                if (!hasNext()) throw NoSuchElementException("alg.Traversal has finished")

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

    fun <V> breadthFirstSearchLayerIterator(g: SimpleGraph<V>, v: V): Iterator<MutableSet<V>> {

        if (v !in g.V) throw IllegalArgumentException("Graph does not contain vertex v")

        return object : Iterator<MutableSet<V>> {

            var queue = LinkedList(listOf(v))
            val seen = mutableSetOf(v)

            override fun hasNext(): Boolean = queue.isNotEmpty()

            override fun next(): MutableSet<V> {
                if (!hasNext()) throw NoSuchElementException("alg.Traversal has finished")

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

    fun <V> depthFirstSearchIterator(g: SimpleGraph<V>, v: V): Iterator<V> {

        if (v !in g.V) throw IllegalArgumentException("Graph does not contain vertex v")

        return object : Iterator<V> {

            val queue = LinkedList(listOf(v))
            val seen = mutableSetOf(v)

            override fun hasNext(): Boolean = queue.isNotEmpty()

            override fun next(): V {
                if (!hasNext()) throw NoSuchElementException("alg.Traversal has finished")

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