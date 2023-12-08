import utils.asInfiniteSequence
import utils.lcm

class Day08 : Day(8, 2023, "Haunted Wasteland") {

    private val instructions = input[0].toList().asInfiniteSequence()

    private val network = inputAsGroups[1].associate {
        it.filter { it in 'A'..'Z' || it.isDigit() }.chunked(3)
            .let { (node, left, right) ->
                node to (left to right)
            }
    }

    override fun part1(): Int =
        instructions.runningFold("AAA") { node, turn ->
            when (turn) {
                'L' -> network[node]?.first
                else -> network[node]?.second
            } ?: error("could not walk $turn from $node")
        }.takeWhile { it != "ZZZ" }.count()

    override fun part2(): Long {
        val startNodes = network.keys.filter { it.endsWith('A') }

        return startNodes.map { startNode ->
            instructions.runningFold(startNode) { node, turn ->
                when (turn) {
                    'L' -> network[node]?.first
                    else -> network[node]?.second
                } ?: error("could not walk $turn from $node")
            }.takeWhile { !it.endsWith('Z') }.count()
        }.lcm()
    }

}

fun main() {
    solve<Day08> {
        """
            RL

            AAA = (BBB, CCC)
            BBB = (DDD, EEE)
            CCC = (ZZZ, GGG)
            DDD = (DDD, DDD)
            EEE = (EEE, EEE)
            GGG = (GGG, GGG)
            ZZZ = (ZZZ, ZZZ)
        """.trimIndent()(2)

        """
            LLR

            AAA = (BBB, BBB)
            BBB = (AAA, ZZZ)
            ZZZ = (ZZZ, ZZZ)
        """.trimIndent()(6)

        """
            LR

            11A = (11B, XXX)
            11B = (XXX, 11Z)
            11Z = (11B, XXX)
            22A = (22B, XXX)
            22B = (22C, 22C)
            22C = (22Z, 22Z)
            22Z = (22B, 22B)
            XXX = (XXX, XXX)
        """.trimIndent()(part2 = 6)
    }
}