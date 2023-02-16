package sipura.graphs

import java.util.LinkedList

/**
 * A SimpleGraph implementation that keeps track of all [addVertex], [removeVertex], [addEdge] and [removeEdge]
 * operations and has the option of undoing them in reverse order.
 */
open class SimpleGraphUndo<V>() : SimpleGraph<V>() {

    /**
     * Returns the amount of undo operations that can be performed on this graph at this moment.
     */
    open val undoSize: Int
        get() = undoStack.size

    protected val undoStack: LinkedList<() -> Unit> = LinkedList<() -> Unit>()

    /**
     * Adds all vertices and edges in [g] to this graph without adding the necessary operations to the undo stack.
     */
    constructor(g: SimpleGraph<V>) : this() {
        for (v in g.V) {
            super.addVertex(v)
        }
        for ((v1, v2) in g.edgeIterator()) {
            super.addEdge(v1, v2)
        }
    }

    /**
     * Reverts the last operation that has been performed on this graph [times] many times. Does nothing if no
     * operation exists that can be reverted.
     */
    open fun undo(times: Int = 1) {
        repeat(times) {
            if (undoStack.isNotEmpty()) {
                undoStack.removeFirst()()
            }
        }
    }

    /**
     * Clears the undo stack.
     */
    open fun clearStack() {
        undoStack.clear()
    }

    /**
     * Returns the amount of undo operations that are currently available.
     */
    open fun stackSize(): Int = undoStack.size

    override fun addVertex(v: V): Boolean {
        if (super.addVertex(v)) {
            undoStack.addFirst { super.removeVertex(v) }
            return true
        }
        return false
    }

    override fun removeVertex(v: V): Boolean {
        val neighborSet = if (v in super.V) {
            super.neighbors(v).toSet()
        } else {
            emptySet()
        }
        if (super.removeVertex(v)) {
            undoStack.addFirst {
                super.addVertex(v)
                for (n in neighborSet) {
                    super.addEdge(v, n)
                }
            }
            return true
        }
        return false
    }

    override fun addEdge(v1: V, v2: V): Boolean {
        if (super.addEdge(v1, v2)) {
            undoStack.addFirst { super.removeEdge(v1, v2) }
            return true
        }
        return false
    }

    override fun removeEdge(v1: V, v2: V): Boolean {
        if (super.removeEdge(v1, v2)) {
            undoStack.addFirst { super.addEdge(v1, v2) }
            return true
        }
        return false
    }

    /**
     * Only copies the vertices and edges of this graph. Does NOT copy the undo operations.
     */
    override fun copy(): SimpleGraphUndo<V> {
        return SimpleGraphUndo(super.copy())
    }
}
