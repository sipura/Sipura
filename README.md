# [Sipura](https://www.google.com/maps/@-2.2124651,99.6788144,12.01z)

An easy-to-use, safe and transparent library for graph datastructures and algorithms.

### Minimal Kotlin example

```Kotlin
import alg.Connectivity
import alg.GraphProperty
import graphs.SimpleGraph

fun main() {
    val g = SimpleGraph<Int>()

    for (v in 1..4) g.addVertex(v)
    g.addEdge(1, 2)
    g.addEdge(1, 3)
    // looks like:   2--1--3   4
    
    println(g.m) // 2
    println(g.V) // {1, 2, 3, 4}

    println(g.neighbors(1)) // {2, 3}

    println(GraphProperty.isTree(g)) // false
    println(Connectivity.distance(g, 2, 3)) // 2
}
```
