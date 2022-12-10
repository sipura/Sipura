package sipura.utils

import sipura.utils.Selection.takeTopK
import java.util.Collections
import java.util.LinkedList
import java.util.PriorityQueue

/**
 * Provides utility methods to work with collections, that are not provided by the standard library.
 * For example, [takeTopK] provides a way to take the top k elements of a collection in a short runtime by using a heap.
 */
object Selection {

    /**
     * @param [collection] The collection to take the top k elements from.
     * @param [k] The number of elements to take.
     *
     * @throws [NoSuchElementException] if [k] is greater than the size of [collection].
     *
     * @return the top [k] elements of a collection.
     */
    fun <E> takeTopK(collection: Collection<E>, k: Int): List<E> {
        val heap = PriorityQueue<E>(Collections.reverseOrder())
        heap.addAll(collection)

        val res = LinkedList<E>()
        repeat(k) { res.add(heap.remove()) }
        return res
    }
}
