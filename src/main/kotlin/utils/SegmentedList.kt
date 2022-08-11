package utils

/**This data structure is a list that is partitioned into continuous segments.
 * Each segment can contain any number of elements (including 0).
 *
 * You can't remove elements at an arbitrary index. It's only possible to remove all elements in the last segment.
 * This is intentionally similar to a stack. For this reason, this data-structure can also be interpreted as stack,
 * whose individual elements are lists (but this "stack" supports some further operations like continuous indexing,
 * that's why we have it).
 *
 * The example ((1, 2), (4), (), (6, 5, 4)) contains 4 segments. The first segment is (1, 2), the second segment
 * is (4), the third element is the empty list ( ) and the fourth segment is (6, 5, 4).
 * The indexing is continuous, meaning that the entry at index *4* in the example is 5.
 * Reading of entries at an arbitrary index is allowed, which is not a regular property of a stack.
 *
 * Additionally, it tracks the frequency of the elements in a [HashMap]. Therefore, the method [contains] has a constant runtime.
 *
 *
 * In the case of this project, it is used to track information related to the search-tree in the algorithm, mostly
 * for each vertex in the subgraph what its exclusive neighbours are (neighbours first discovered by this vertex).
 * The continuous indexing is needed so that you can iterate through the neighbours with pointers. The method [contains]
 * is needed frequently to check which neighbours of a vertex are *exclusive* (not already discovered, so not in the [SegmentedList] yet).
 *
 * @param T The type of the elements stored in this object.*/
class SegmentedList<T> {

    private val freq = HashMap<T, Int>().withDefault { 0 } // how often some element is contained

    /**entry at index *i* contains how many elements are stored in the segments 0,1,...,i-1,i summed up.
     * For this class storing how many entries are just in segment i would also work, but we need the cumulative
     * number of entries until segment i for stuff in the Main-Algorithm.*/
    val segmentSizes = ArrayList<Int>()

    /**Used to provide continuous indexing. Works like you flattened all segments into one list.*/
    private val values = ArrayList<T>()

    val listView: List<T> get() = values
    val size: Int get() = values.size

    /**Get element by index*/
    operator fun get(index: Int): T = values[index]

    /**Has constant runtime*/
    operator fun contains(elem: T): Boolean = freq.getValue(elem) > 0       //uses default value 0

    /**Adds the element to to the end and creates a new segment for it*/
    operator fun plusAssign(elem: T) = plusAssign(listOf(elem))

    /**Appends all elements of [col] to the stack in *one* segment.*/
    operator fun plusAssign(col: Collection<T>) {
        segmentSizes.add((segmentSizes.lastOrNull() ?: 0) + col.size)
        for (elem in col) {
            freq[elem] = freq.getValue(elem) + 1    //use default-value of 0
            values.add(elem)
        }
    }

    /** Adds all elements of [col] to the last segment (so no new segment is created).*/
    fun addToLast(col: Collection<T>) {
        segmentSizes[segmentSizes.size - 1] += col.size
        for (elem in col) {
            freq[elem] = freq.getValue(elem) + 1    //use default-value of 0
            values.add(elem)
        }

    }

    /**Removes the last segment with all its entries.*/
    fun removeLastSegment() {
        val second_last_segmentSize = segmentSizes.getOrNull(segmentSizes.size - 2) ?: 0
        repeat(segmentSizes.last() - second_last_segmentSize) { // number of elements in last segment
            freq[values.last()] = freq[values.last()]!! - 1 //decrease its frequency by 1
            values.removeLast()    //remove last element
        }
        segmentSizes.removeAt(segmentSizes.size - 1)  //remove last element
    }
}