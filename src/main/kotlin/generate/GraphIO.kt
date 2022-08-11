package generate

import graphs.SimpleGraph
import java.io.File
import java.io.PrintStream

object GraphIO {

    /**
     * Imports a [SimpleGraph] from the file located at [filePath].
     * Iterates over every line of the file and calls [lineParser]. Then adds the two vertices returned by [lineParser]
     * to the graph with an edge between them.
     * If [lineParser] throws an [IllegalArgumentException] the current line is skipped.
     */
    fun <V> importSimpleGraphFromFile(filePath: String, lineParser: (String) -> Pair<V, V>): SimpleGraph<V> {
        val g = SimpleGraph<V>()
        val inputFile = File(filePath)
        inputFile.forEachLine {
            try {
                val (v1, v2) = lineParser(it.trim())
                g.addVertex(v1)
                g.addVertex(v2)
                g.addEdge(v1, v2)
            } catch (e: IllegalArgumentException) {
                // skip this line because it cant be parsed
            }
        }
        return g
    }

    /**
     * Imports a [SimpleGraph] with [Int] as the vertex type from the file located at [filePath].
     * Each line of the file has to match the Regex [0-9]+[ \t]+[0-9]+. Any line that does not match this is skipped.
     */
    fun importSimpleGraphFromFileWithInts(filePath: String): SimpleGraph<Int> {
        return importSimpleGraphFromFile(filePath) {
            val regex = Regex("[0-9]+[ \t]+[0-9]+")
            if (it.matches(regex)) {
                val nums = it.split(' ', '\t')
                val v1 = Integer.parseInt(nums[0])
                val v2 = Integer.parseInt(nums[nums.size - 1])
                Pair(v1, v2)
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    /**
     * Imports a [SimpleGraph] with [String] as the vertex type from the file located at [filePath].
     * Each line of the file has to match the Regex [^ \t]+[ \t]+[^ \t]+. Any line that does not match this is skipped.
     */
    fun importSimpleGraphFromFileWithStrings(filePath: String): SimpleGraph<String> {
        return importSimpleGraphFromFile(filePath) {
            val regex = Regex("[^ \t]+[ \t]+[^ \t]+")
            if (it.matches(regex)) {
                val nums = it.split(' ', '\t')
                val v1 = nums[0]
                val v2 = nums[nums.size - 1]
                Pair(v1, v2)
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    /**
     * Exports the [SimpleGraph] [g] to a new file located at [filePath].
     * Iterates over the edges of [g] and calls [lineCreator] for each one. The resulting [String] is then appended
     * to the end of the output file as a new line.
     * @throws FileAlreadyExistsException if the file located at [filePath] already exists.
     */
    fun <V> exportSimpleGraphToFile(g: SimpleGraph<V>, filePath: String, lineCreator: (V, V) -> String) {
        val outputFile = File(filePath)
        if (outputFile.exists()) {
            throw FileAlreadyExistsException(outputFile)
        } else {
            outputFile.createNewFile()
            with(PrintStream(outputFile)) {
                g.forEachEdge { v1, v2 ->
                    this.println(lineCreator(v1, v2))
                }
            }
        }
    }

    /**
     * Exports the [SimpleGraph] [g] to a new file located at [filePath].
     * Iterates over the edges of [g] and appends a new line to the end of the output file for each one.
     * The lines are created by calling [toString] for each of the two vertices and then separating them by a single
     * space character.
     * @throws FileAlreadyExistsException if the file located at [filePath] already exists.
     */
    fun <V> exportSimpleGraphToFileSpaceSeperated(g: SimpleGraph<V>, filePath: String) {
        exportSimpleGraphToFile(g, filePath) { v1, v2 ->
            "$v1 $v2"
        }
    }

}