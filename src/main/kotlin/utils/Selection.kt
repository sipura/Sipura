package utils

import java.util.*

object Selection {

    fun <E> takeTopK(collection: Collection<E>, k : Int): List<E> {
        val heap = PriorityQueue<E>(  Collections.reverseOrder())
        heap.addAll(collection)

        val res = LinkedList<E>()
        repeat(k) { res.add(heap.remove())}
        return res
    }

}