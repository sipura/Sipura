package sipura.hardproblems

import sipura.graphs.SimpleGraph
import java.util.LinkedList

object SubgraphIsomorphism {

    /**
     * Return every induced isomorphism of the (potential) subgraph [pattern] of the data graph [data].
     *
     * An induced subgraph isomorphism is a bijection B between [pattern] and a subgraph of [data] such that:
     * - there is an edge {v, w} in [pattern] if and only if {B(v), B(w)} is also an edge in the subgraph of [data].
     * - for every vertex v the labels of v and B(v) are equal
     *
     * Uses a recursive implementation of the VF3 algorithm.
     *
     * @return a [List] including every induced subgraph isomorphism.
     */
    fun <V, W> getAllSubgraphIsomorphism(
        pattern: SimpleGraph<V>,
        data: SimpleGraph<W>,
        labelsPattern: HashMap<V, Int>,
        labelsData: HashMap<W, Int>,
    ): ArrayList<HashMap<V, W>> {
        val state = AlgoState(pattern, data, labelsPattern, labelsData)
        match(state)
        return state.solutions
    }

    /**
     * Iterates over every induced isomorphism of the (potential) subgraph [pattern] of the data graph [data].
     *
     * An induced subgraph isomorphism is a bijection B between [pattern] and a subgraph of [data] such that:
     * - there is an edge {v, w} in [pattern] if and only if {B(v), B(w)} is also an edge in the subgraph of [data].
     * - for every vertex v the labels of v and B(v) are equal
     *
     * Uses an iterative implementation of the VF3 algorithm.
     *
     * @return an [Iterator] that iterates over induced subgraph isomorphism.
     */
    fun <V, W> subgraphIsomorphismIterator(
        pattern: SimpleGraph<V>,
        data: SimpleGraph<W>,
        labelsPattern: HashMap<V, Int>,
        labelsData: HashMap<W, Int>,
    ): VF3Iterator<V, W> {
        return VF3Iterator(pattern, data, labelsPattern, labelsData)
    }

    class VF3Iterator<V, W>(
        pattern: SimpleGraph<V>,
        data: SimpleGraph<W>,
        labelsPattern: HashMap<V, Int>,
        labelsData: HashMap<W, Int>,
    ) {
        private val state = AlgoState(pattern, data, labelsPattern, labelsData)

        /**
         * @return the next subgraph isomorphism or null if there are no more isomorphisms
         */
        fun findNextIsomorphism(): HashMap<V, W>? {
            var res: HashMap<V, W>?
            do {
                if (state.currentLevel == -1) return null
                res = makeBranchStep()
            } while (res == null)
            return res
        }

        /**
         * @return all subgraph isomorphisms in a [List]
         */
        fun findAllIsomorphisms(): ArrayList<HashMap<V, W>> {
            while (true) {
                findNextIsomorphism() ?: break
            }
            return state.solutions
        }

        private fun makeBranchStep(): HashMap<V, W>? {
            val level = state.currentLevel
            if (level == -1) return null
            val levelVertex = state.nodeSequenceG1[level]

            if (state.pairingLists[level] == null) { // create pairing candidates if needed
                val pairingList = getParingList(levelVertex, state, state.partialMapping)
                if (pairingList == null) return null
                state.pairingLists[level] = pairingList
                state.pairingCandidatesIndices[level] = 0
            }
            val pList = state.pairingLists[level]
            val pIndex = state.pairingCandidatesIndices[level]
            if (pIndex >= pList!!.size) {
                // branch back
                branchBack(level, levelVertex)
            } else {
                // 'normal' branching
                val vertexToPair = pList[pIndex]
                if (state.partialMapping.contains(levelVertex)) {
                    redoMapping(level, levelVertex)
                }
                val (feasible, verticesAddToS2) = isFeasible(state, levelVertex, vertexToPair)
                if (feasible) {
                    if (level + 1 == state.pattern.n) { // isGoal function
                        val res = HashMap(state.partialMapping)
                        res[levelVertex] = vertexToPair
                        state.solutions.add(res)
                        state.pairingCandidatesIndices[level]++
                        return res
                    }
                    branchForward(level, levelVertex, vertexToPair, verticesAddToS2)
                } else {
                    state.pairingCandidatesIndices[level]++
                }
            }
            return null
        }

        private fun branchForward(level: Int, levelVertex: V, vertexToPair: W, verticesAddToS2: List<W>?) {
            state.partialMapping[levelVertex] = vertexToPair
            state.dataSplitByClasses[state.classifyFunction1[levelVertex]!!].remove(vertexToPair)
            state.s2RemovedPairedVertex[level] = state.s2.remove(vertexToPair)
            state.s2ListOfVerticesToAdd[level] = verticesAddToS2
            state.s2.addAll(verticesAddToS2!!)
            state.currentLevel++
        }

        private fun redoMapping(level: Int, levelVertex: V) {
            state.s2.removeAll(state.s2ListOfVerticesToAdd[level]!!)
            val vertexToPair = state.partialMapping[levelVertex]
            if (state.s2RemovedPairedVertex[level]) {
                state.s2.add(state.partialMapping[levelVertex]!!)
            }
            state.dataSplitByClasses[state.classifyFunction2[vertexToPair]!!].add(vertexToPair!!)
            state.partialMapping.remove(levelVertex)
            state.s2RemovedPairedVertex[level] = false
            state.s2ListOfVerticesToAdd[level] = null
        }

        private fun branchBack(level: Int, levelVertex: V) {
            state.pairingLists[level] = null
            state.pairingCandidatesIndices[level] = 0
            state.currentLevel--
            val vertexToPair = state.partialMapping[levelVertex]
            if (vertexToPair != null) {
                redoMapping(level, levelVertex)
            }
            if (level != 0) { // then currentLevel == -1
                state.pairingCandidatesIndices[state.currentLevel]++
            }
        }
    }

    private class AlgoState<V, W>(
        val pattern: SimpleGraph<V>,
        val data: SimpleGraph<W>,
        val labelsPattern: HashMap<V, Int>,
        val labelsData: HashMap<W, Int>,
    ) {
        val pf = computeProbabilities(pattern, data, labelsPattern, labelsData)
        val nodeSequenceG1 = generateNodeSequence(pattern, pf)

        private val classifyNodesPair = classifyNodes(labelsPattern, labelsData)
        val classifyFunction1 = classifyNodesPair.first
        val classifyFunction2 = classifyNodesPair.second
        val numberOfClasses = classifyNodesPair.third
        val dataSplitByClasses = getDataClassified(data, classifyFunction2, numberOfClasses)
        val preprocessPatternGraphPair =
            preprocessPatternGraph(pattern, nodeSequenceG1, classifyFunction1, numberOfClasses)
        val parent = preprocessPatternGraphPair.first

        //        val feasibilitySetsPattern = preprocessPatternGraphPair.second
//        val peripherySetsPattern = preprocessPatternGraphPair.third
        private val lookAheadLevelNode = lookAheadData()
        val feasSetsLevelNode = lookAheadLevelNode.first
        val peripherySetsLevelNode = lookAheadLevelNode.second
        fun lookAheadData(): Pair<Array<Array<Int>>, Array<Array<Int>>> {
            val feasSetsLevelNode = Array(pattern.n) { Array(numberOfClasses) { 0 } }
            val peripherySetsLevelNode = Array(pattern.n) { Array(numberOfClasses) { 0 } }
            val included = HashSet<V>()
            val s = HashSet<V>()
            for ((level, v) in nodeSequenceG1.withIndex()) {
                for (neighbor in pattern.neighbors(v)) {
                    if (included.contains(neighbor)) continue
                    if (s.contains(neighbor)) {
                        feasSetsLevelNode[level][classifyFunction1[neighbor]!!]++
                    } else {
                        peripherySetsLevelNode[level][classifyFunction1[neighbor]!!]++
                        s.add(neighbor)
                    }
                }
                s.remove(v)
                included.add(v)
            }
            return Pair(feasSetsLevelNode, peripherySetsLevelNode)
        }

        val solutions = ArrayList<HashMap<V, W>>()
        val partialMapping = HashMap<V, W>()
        val s2 = HashSet<W>()

        var currentLevel = 0
        val pairingLists = Array<List<W>?>(pattern.n) { null }
        val pairingCandidatesIndices = Array(pattern.n) { 0 }
        val s2RemovedPairedVertex = Array(pattern.n) { false }
        val s2ListOfVerticesToAdd = Array<List<W>?>(pattern.n) { null }
    }

    private fun <V, W> match(state: AlgoState<V, W>): Boolean {
        if (isGoal(state.partialMapping, state.pattern)) {
            state.solutions.add(HashMap(state.partialMapping))
            return true
        }
        val levelVertex = state.nodeSequenceG1[state.currentLevel]
        val pairingList = getParingList(levelVertex, state, state.partialMapping)
        if (pairingList == null) return false

        val classUN = state.classifyFunction1[levelVertex]

        var result = false
        for (vertexToPair in pairingList) {
            val (feasible, verticesAddToS2) = isFeasible(state, levelVertex, vertexToPair)
            if (feasible) {
                state.partialMapping[levelVertex] = vertexToPair
                state.dataSplitByClasses[classUN!!].remove(vertexToPair)
                val removed = state.s2.remove(vertexToPair)
                state.s2.addAll(verticesAddToS2!!)
                state.currentLevel++
                if (match(state)) {
                    result = true
                }
                state.currentLevel--
                if (removed) state.s2.add(vertexToPair)
                state.s2.removeAll(verticesAddToS2)
                state.dataSplitByClasses[classUN].add(vertexToPair)
                state.partialMapping.remove(levelVertex)
            }
        }
        return result
    }

    private fun <V, W> getParingList(un: V, state: AlgoState<V, W>, partialMapping: Map<V, W>): List<W>? {
        val pairingList: List<W>
        val p = state.parent.getOrDefault(un, null)
        if (p == null) {
            val classUN = state.classifyFunction1[un]!!
            pairingList = ArrayList(state.dataSplitByClasses[classUN])
        } else {
            val parentData = partialMapping[p]
            if (parentData == null) {
                return null
            }
            val classUN = state.classifyFunction1[un]!!
            val candidates = state.dataSplitByClasses[classUN]
            pairingList = candidates.filter { state.data.hasEdge(parentData, it) }
        }
        return pairingList
    }

    private fun <V, W> isFeasible(state: AlgoState<V, W>, levelVertex: V, pairedVertex: W): Pair<Boolean, List<W>?> {
        // structural: checking labels, since classes were used, it as to be checked
        if (state.labelsPattern[levelVertex] != state.labelsData[pairedVertex] ||
            state.data.degreeOf(pairedVertex) < state.pattern.degreeOf(levelVertex)
        ) {
            return Pair(false, null)
        }
        // Fc rule
        val neighborsPattern = state.pattern.neighbors(levelVertex)
        val neighborsData = state.data.neighbors(pairedVertex)
        val m1 = state.partialMapping.keys
        if (neighborsPattern.size + neighborsData.size < m1.size) {
            val includedNeighbors = HashSet<W>()
            for (neighbor in neighborsPattern) {
                if (!m1.contains(neighbor)) continue
                val correspondingDataVertex = state.partialMapping[neighbor]
                includedNeighbors.add(correspondingDataVertex!!)
                if (!state.data.hasEdge(correspondingDataVertex, pairedVertex)) {
                    return Pair(false, null)
                }
            }
            val m2 = state.partialMapping.values
            for (neighbor in neighborsData) {
                if (m2.contains(neighbor) && !includedNeighbors.contains(neighbor)) {
                    return Pair(false, null) // pairedVertex is connected with vertices than levelVertex
                }
            }
        } else {
            for (includedVertex in m1) {
                val areNeighbors = state.pattern.hasEdge(includedVertex, levelVertex)
                val correspondingDataVertex = state.partialMapping[includedVertex]
                if (areNeighbors != state.data.hasEdge(correspondingDataVertex!!, pairedVertex)) {
                    return Pair(false, null)
                }
            }
        }
        // look ahead
        val verticesAddToS2 = LinkedList<W>()
        val feasSetsData = Array(state.numberOfClasses) { 0 }
        val peripheryData = Array(state.numberOfClasses) { 0 }

        val neighborsDataGraph = state.data.neighbors(pairedVertex)
        for (neighbor in neighborsDataGraph) {
            if (state.partialMapping.containsValue(neighbor)) continue
            if (state.s2.contains(neighbor)) {
                feasSetsData[state.classifyFunction2[neighbor]!!]++
            } else { // vertex is in periphery
                peripheryData[state.classifyFunction2[neighbor]!!]++
                verticesAddToS2.add(neighbor)
            }
        }
        for (classIndex in 0 until state.numberOfClasses) {
            if (feasSetsData[classIndex] < state.feasSetsLevelNode[state.currentLevel][classIndex] ||
                peripheryData[classIndex] < state.peripherySetsLevelNode[state.currentLevel][classIndex]
            ) {
                return Pair(false, null)
            }
        }
        return Pair(true, verticesAddToS2)
    }

    private fun <V, W> isGoal(state: HashMap<V, W>, pattern: SimpleGraph<V>): Boolean {
        return state.size == pattern.n
    }

    private fun <V, W> computeProbabilities(
        pattern: SimpleGraph<V>,
        data: SimpleGraph<W>,
        labelsPattern: HashMap<V, Int>,
        labelsData: HashMap<W, Int>,
    ): HashMap<V, Double> {
        val labelProbabilities = HashMap<Int, Double>()
        for (l in labelsData.values) labelProbabilities[l] = 0.0
        for (entry in labelsData) labelProbabilities.computeIfPresent(entry.value) { _, v -> v + 1 }
        for (l in labelProbabilities.keys) labelProbabilities.computeIfPresent(l) { _, v -> v / data.n }

        val degreeProbabilities = HashMap<Int, Double>()
        for (v in pattern.V) {
            degreeProbabilities.putIfAbsent(pattern.degreeOf(v), 0.0)
        }
        val degrees = ArrayList<Int>(degreeProbabilities.keys)
        degrees.sortDescending()
        val maxDegree = pattern.maxDegree()
        for (w in data.V) {
            val degree = data.degreeOf(w)
            if (degree >= maxDegree) {
                degreeProbabilities.computeIfPresent(maxDegree) { _, v -> v + 1 }
            } else {
                degreeProbabilities.computeIfPresent(degree) { _, v -> v + 1 }
            }
        }
        for (i in 1 until degrees.size)
            degreeProbabilities.computeIfPresent(degrees[i]) { _, v -> v + degreeProbabilities[degrees[i - 1]]!! }
        for (d in degreeProbabilities.keys) degreeProbabilities.computeIfPresent(d) { _, v -> v / data.n }

        val pf = HashMap<V, Double>()
        for (v in pattern.V) {
            pf[v] = labelProbabilities[labelsPattern[v]]!! * degreeProbabilities.getOrDefault(pattern.degreeOf(v), 0.0)
        }
        return pf
    }

    private fun <V> generateNodeSequence(pattern: SimpleGraph<V>, pf: HashMap<V, Double>): ArrayList<V> {
        val dm = HashMap<V, Int>()
        for (vertex in pattern.V) dm[vertex] = 0
        val order = ArrayList<V>(pattern.V)
        val included = HashSet<V>()
        for (iteration in 0 until pattern.n) {
            var bestVertexDM: V? = null
            var maxDM = 0
            var bestIndex = 0
            for (i in iteration until pattern.n) {
                val vertex = order[i]
                val valDM = dm[vertex]

                if (bestVertexDM == null || maxDM < valDM!!) {
                    bestVertexDM = vertex
                    maxDM = valDM!!
                    bestIndex = i
                } else {
                    if (maxDM == valDM) {
                        if (pf[bestVertexDM]!! > pf[vertex]!!) {
                            bestVertexDM = vertex
                            maxDM = valDM
                            bestIndex = i
                        } else {
                            if (pattern.degreeOf(bestVertexDM) > pattern.degreeOf(vertex)) { // TODO unsure whether < or >; search in original code
                                bestVertexDM = vertex
                                maxDM = valDM
                                bestIndex = i
                            } // no further criterion in paper
                        }
                    }
                }
            }
            order[bestIndex] = order[iteration]
            order[iteration] = bestVertexDM!!
            included.add(bestVertexDM)
            for (neighbor in pattern.neighbors(bestVertexDM)) dm.computeIfPresent(neighbor) { _, x -> x + 1 }
        }
        return order
    }

    private fun <V, W> classifyNodes(
        labelsPattern: HashMap<V, Int>,
        labelsData: HashMap<W, Int>,
    ): Triple<HashMap<V, Int>, HashMap<W, Int>, Int> {
        val classifyFunction1 = HashMap<V, Int>()
        val classifyFunction2 = HashMap<W, Int>()
        val allLabels = HashSet<Int>(labelsData.values)
        allLabels.addAll(labelsPattern.values)
        val newClasses = HashMap<Int, Int>()
        for ((ctr, label) in allLabels.withIndex()) {
            newClasses[label] = ctr
        }
        for (v in labelsPattern.keys) classifyFunction1[v!!] = newClasses[labelsPattern[v]]!!
        for (v in labelsData.keys) classifyFunction2[v!!] = newClasses[labelsData[v]]!!

        return Triple(classifyFunction1, classifyFunction2, allLabels.size)
    }

    private fun <W> getDataClassified(
        data: SimpleGraph<W>,
        classifyFunction2: HashMap<W, Int>,
        numberOfClasses: Int,
    ): Array<HashSet<W>> {
        val dataSplitByClasses = Array(numberOfClasses) { HashSet<W>() }
        for (w in data.V) dataSplitByClasses[classifyFunction2[w]!!].add(w)
        return dataSplitByClasses
    }

    private fun <V> preprocessPatternGraph(
        // function had some mistakes in paper
        pattern: SimpleGraph<V>,
        nodeSequenceG1: ArrayList<V>,
        classifyFunction1: HashMap<V, Int>,
        numberOfClasses: Int,
    ): Triple<HashMap<V, V>, Array<Array<HashSet<V>>>, Array<Array<Int>>> {
        // M matched vertices, P predecessor, S successor, V vertices not in M; every Variable as an ~ above its character
        // feasibility sets S (P has not to be done since it is undirected)
        val parent = HashMap<V, V>()
        val feasSets = Array(nodeSequenceG1.size) { Array<HashSet<V>>(numberOfClasses) { HashSet<V>() } }
        val peripherySets = Array(pattern.n) { Array(numberOfClasses) { 0 } }
        val included = HashSet<V>()
        for ((i, u) in nodeSequenceG1.withIndex()) {
            included.add(u)
            if (i != 0) {
                for (c in 0 until numberOfClasses) {
                    feasSets[i][c].addAll(feasSets[i][c])
                }
                val lastVertex = nodeSequenceG1[i - 1]
                val classIndex: Int = classifyFunction1[lastVertex]!!
                feasSets[i][classIndex].remove(lastVertex)
            }
            for (neighbor in pattern.neighbors(u)) {
                if (included.contains(neighbor)) continue
                val c = classifyFunction1[neighbor]
                feasSets[i + 1][c!!].add(neighbor)
                parent[neighbor] = u
                peripherySets[i][c]++
            }
        }
        return Triple(parent, feasSets, peripherySets)
    }
}
