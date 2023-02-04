package sipura.graphs

import java.util.*

/**
 * A SimpleGraph implementation that keeps track of all [addVertex], [removeVertex], [addEdge] and [removeEdge]
 * operations and has the option of undoing them in reverse order.
 */
open class SimpleGraphUndo<V>() : SimpleGraph<V>() {

    protected val undoStack: Stack<() -> Unit> = Stack<() -> Unit>()

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
     * Reverts the last operation that has been performed on this graph.
     */
    open fun undo() {
        if (undoStack.isNotEmpty()) {
            undoStack.pop()()
        }
    }

    /**
     * Clears the undo stack.
     */
    open fun clearStack() {
        undoStack.clear()
    }

    override fun addVertex(v: V): Boolean {
        if (super.addVertex(v)) {
            undoStack.push { super.removeVertex(v) }
            return true
        }
        return false
    }

    override fun removeVertex(v: V): Boolean {
        val neighborSet = super.neighbors(v).toSet()
        if (super.removeVertex(v)) {
            undoStack.push {
                addVertex(v)
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
            undoStack.push { super.removeEdge(v1, v2) }
            return true
        }
        return false
    }

    override fun removeEdge(v1: V, v2: V): Boolean {
        if (super.removeEdge(v1, v2)) {
            undoStack.push { super.addEdge(v1, v2) }
            return true
        }
        return false
    }

    override fun copy(): SimpleGraph<V> {
        val copy = SimpleGraphUndo(this)
        for (f in undoStack) {
            copy.undoStack.push(f)
        }
        copy.undoStack.reverse()
        return copy
    }
}
