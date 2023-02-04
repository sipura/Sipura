package sipura.graphs

import java.util.*

class SimpleGraphUndo<V> : SimpleGraph<V>() {

    private val undoStack: Stack<() -> Unit> = Stack<() -> Unit>()

    override fun addVertex(v: V): Boolean {
        return super.addVertex(v)
    }

    override fun removeVertex(v: V): Boolean {
        return super.removeVertex(v)
    }

    override fun addEdge(v1: V, v2: V): Boolean {
        return super.addEdge(v1, v2)
    }

    override fun removeEdge(v1: V, v2: V): Boolean {
        return super.removeEdge(v1, v2)
    }

    override fun copy(): SimpleGraph<V> {
        return super.copy()
    }
}
