import java.io.File

// temporary file for testing

fun main() {
    val dirPath = "D:\\lucas\\OneDrive\\Uni\\6. Semester\\Bachelorarbeit\\Repositories\\data\\"
    val filePath = dirPath + "network repository\\brain\\bn-human-BNU_1_0025890_session_1.edges"
    val g = GraphIO.importSimpleGraphFromFileWithInts(filePath)
    println("n: ${g.n}, m: ${g.m}")
    val temp = System.currentTimeMillis()
    for (i in 1..100) {
        val v1 = g.V.random()
        val v2 = g.V.random()
        println("$i: ${Connectivity.checkIfConnected(g, v1, v2)}")
    }
    println("Time: ${prettyTime(System.currentTimeMillis() - temp)}")
}

fun prettyTime(millis: Long): String {
    var h = 0L
    var m = 0L
    var s = 0L
    var ms = millis
    if (ms >= 3600000L) {
        h = ms / 3600000L
        ms %= 3600000L
    }
    if (ms >= 60000L) {
        m = ms / 60000L
        ms %= 60000L
    }
    if (ms >= 1000L) {
        s = ms / 1000L
        ms %= 1000L
    }
    return "%d:%02d:%02d.%03d".format(h, m, s, ms)
}

fun importSimpleGraphFromFile(filePath: String): SimpleGraph<Int> {
    val result = SimpleGraph<Int>()
    val regex = Regex("[0-9]+ [0-9]+")
    val inputFile = File(filePath)
    inputFile.forEachLine {
        val line = cleanLine(it)
        if (line.matches(regex)) {
            val nums = line.split(' ', '\t')
            if (nums.size == 2) {
                try {
                    val first = Integer.parseInt(nums[0])
                    val second = Integer.parseInt(nums[1])
                    result.addVertex(first)
                    result.addVertex(second)
                    if (first != second) result.addEdge(first, second)
                } catch (e: NumberFormatException) {
                    println("Could not parse Int")
                }
            }
        }
    }
    return result
}

fun cleanLine(curLine: String): String {
    var result = curLine.replace('\t', ' ')
    while (result.contains("  ")) {
        result = result.replace("  ", " ")
    }
    while (result.startsWith(" ")) {
        result = result.removePrefix(" ")
    }
    while (result.endsWith(" ")) {
        result = result.removeSuffix(" ")
    }
    return result
}