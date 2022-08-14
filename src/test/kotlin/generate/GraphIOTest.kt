package generate

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.FileNotFoundException
import kotlin.test.Ignore


internal class GraphIOTest {

    val dirPath = "testFiles"

    val nonExistingFilePath = "$dirPath\\testGraph0.graph"
    val testFile1Path = "$dirPath\\testGraph1.graph"
    val testFile2Path = "$dirPath\\testGraph2.graph"

    @BeforeEach
    fun setup() {

    }

    @AfterEach
    fun after() {

    }

    @Nested
    internal inner class ImportSimpleGraph {

        @Test
        fun `importSimpleGraphFromFile imports graph correctly`() {
            var vertexCounter = 1
            val g = GraphIO.importSimpleGraphFromFile(testFile1Path) {
                vertexCounter++
                Pair(vertexCounter - 1, vertexCounter)
            } // should add vertices {1,2,3,4,5} and edges (1,2), (2,3), (3,4) and (4,5)
            assertEquals(5, g.n)
            assertEquals(4, g.m)
            assertTrue { 1 in g }
            assertTrue { 2 in g }
            assertTrue { 3 in g }
            assertTrue { 4 in g }
            assertTrue { 5 in g }
            assertTrue { g.hasEdge(1, 2) }
            assertTrue { g.hasEdge(2, 3) }
            assertTrue { g.hasEdge(3, 4) }
            assertTrue { g.hasEdge(4, 5) }
        }

        @Test
        fun `importSimpleGraphFromFileWithInts imports graph correctly`() {
            val g = GraphIO.importSimpleGraphFromFileWithInts(testFile1Path)
            assertEquals(5, g.n)
            assertEquals(4, g.m)
            assertTrue { 1 in g }
            assertTrue { 2 in g }
            assertTrue { 3 in g }
            assertTrue { 4 in g }
            assertTrue { 5 in g }
            assertTrue { g.hasEdge(1, 2) }
            assertTrue { g.hasEdge(1, 3) }
            assertTrue { g.hasEdge(2, 4) }
            assertTrue { g.hasEdge(4, 5) }
        }

        @Test
        fun `importSimpleGraphFromFileWithStrings imports graph correctly`() {
            val g = GraphIO.importSimpleGraphFromFileWithStrings(testFile1Path)
            assertEquals(5, g.n)
            assertEquals(4, g.m)
            assertTrue { "1" in g }
            assertTrue { "2" in g }
            assertTrue { "3" in g }
            assertTrue { "4" in g }
            assertTrue { "5" in g }
            assertTrue { g.hasEdge("1", "2") }
            assertTrue { g.hasEdge("1", "3") }
            assertTrue { g.hasEdge("2", "4") }
            assertTrue { g.hasEdge("4", "5") }
        }

        @Test
        fun `importSimpleGraphFromFile throws no such file exception if file does not exist`() {
            assertThrows<FileNotFoundException> {
                GraphIO.importSimpleGraphFromFile(nonExistingFilePath) {
                    Pair(
                        1,
                        2
                    )
                }
            }
        }

        @Test
        fun `importSimpleGraphFromFileWithInts throws no such file exception if file does not exist`() {
            assertThrows<FileNotFoundException> { GraphIO.importSimpleGraphFromFileWithInts(nonExistingFilePath) }
        }

        @Test
        fun `importSimpleGraphFromFileWithStrings throws no such file exception if file does not exist`() {
            assertThrows<FileNotFoundException> { GraphIO.importSimpleGraphFromFileWithStrings(nonExistingFilePath) }
        }

        @Test
        fun `importSimpleGraphFromFile ignores comments`() {
            var counter = 0
            GraphIO.importSimpleGraphFromFile(testFile2Path) {
                assertFalse { it.startsWith("#") }
                counter++
                Pair(1, 2)
            } // lambda should be executed exactly twice and not receive any lines that start with #
            assertEquals(2, counter)
        }

        @Test
        fun `importSimpleGraphFromFileWithInts ignores comments`() {
            val g = GraphIO.importSimpleGraphFromFileWithInts(testFile2Path)
            // function add vertices {1,2,3} and edges (1,3) and (2,3)
            // it should not add vertices {4,5} and edges (1,2) and (4,5)
            assertEquals(3, g.n)
            assertEquals(2, g.m)
            assertTrue { 1 in g }
            assertTrue { 2 in g }
            assertTrue { 3 in g }
            assertFalse { 4 in g }
            assertFalse { 5 in g }
            assertTrue { g.hasEdge(1, 3) }
            assertTrue { g.hasEdge(2, 3) }
        }

        @Test
        fun `importSimpleGraphFromFileWithStrings ignores comments`() {
            val g = GraphIO.importSimpleGraphFromFileWithStrings(testFile2Path)
            // function add vertices {"1","2","3"} and edges ("1","3") and ("2","3")
            // it should not add vertices {"#1","#4","5"} and edges ("#1","2") and ("#4","5")
            assertEquals(3, g.n)
            assertEquals(2, g.m)
            assertTrue { "1" in g }
            assertTrue { "2" in g }
            assertTrue { "3" in g }
            assertFalse { "#1" in g }
            assertFalse { "#4" in g }
            assertFalse { "5" in g }
            assertTrue { g.hasEdge("1", "3") }
            assertTrue { g.hasEdge("2", "3") }
        }

    }

    @Nested
    internal inner class ExportSimpleGraph {

        // TODO: figure out how to properly use temporary folders in tests

        @Test
        @Ignore
        fun `exportSimpleGraphToFile exports graph correctly`(@TempDir tempDir: File) {
            val g = Factory.createLine(5)
            val outputFile = File(tempDir, "output.graph")
            GraphIO.exportSimpleGraphToFile(g, outputFile.absolutePath) { v1, v2 ->
                "${v1 + v2}"
            }
            val expected = setOf("3", "5", "7", "9")
            val actual = mutableSetOf<String>()
            outputFile.forEachLine { actual.add(it) }
            assertEquals(expected, actual)
        }

        @Test
        @Ignore
        fun `exportSimpleGraphToFileSpaceSeperated exports graph correctly`(@TempDir tempDir: File) {
            val g = Factory.createLine(4)
            val outputFile = File(tempDir, "output.graph")
            GraphIO.exportSimpleGraphToFileSpaceSeperated(g, outputFile.absolutePath)
            val expected = mutableSetOf(setOf("1 2", "2 1"), setOf("2 3", "3 2"), setOf("3 4", "4 3"))
            var lineCounter = 0
            outputFile.forEachLine { line ->
                lineCounter++
                assertTrue { expected.any { it.contains(line) } }
                expected.removeIf { it.contains(line) } // guarantees that only one of the two options in each set appears
            }
            assertEquals(3, lineCounter)
        }

    }

}

