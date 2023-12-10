import PipeMaze.PIPES
import PipeMaze.correctStartPipe
import utils.*
import utils.Direction4.*

object PipeMaze {
    val PIPES = mapOf(
        '|' to setOf(NORTH, SOUTH),
        '-' to setOf(EAST, WEST),
        'L' to setOf(NORTH, EAST),
        'J' to setOf(NORTH, WEST),
        '7' to setOf(SOUTH, WEST),
        'F' to setOf(SOUTH, EAST),
    )

    fun Map<Point, Char>.correctStartPipe(startPoint: Point): Map<Point, Char> =
        toMutableMap().apply {
            val neighbors = startPoint.directNeighbors().filter { neighbor ->
                PIPES[this[neighbor]]?.any { neighbor + it == startPoint } ?: false
            }.toSet()
            require(neighbors.size == 2)

            val requiredElement = PIPES.entries.single { (_, connects) ->
                connects == neighbors.map { Direction4.ofVector(startPoint, it) }.toSet()
            }.key

            put(startPoint, requiredElement)
        }
}

class Day10 : Day(10, 2023, "Pipe Maze") {
    val maze = inputAsGrid
    val startPoint = maze.searchIndices('S').single()
    val mazeArea = maze.area
    val piping =
        maze.searchIndices { it != '.' }.associateWith { maze[it] }.correctStartPipe(startPoint)

    override fun part1(): Int = traversePipes().first

    override fun part2(): Int = findEnclosedPoints().count()

    fun findEnclosedPoints(): Sequence<Point> {
        val relevant = traversePipes().second

        // scale maze by 3 to allow gaps to appear
        val scaledUpMaze = mutableMapOf<Point, Char>()
        relevant.keys.forEach { p ->
            val newP = p * 3 + (1 to 1)

            val element = piping[p]!!
            val dirs = PIPES[element]!!
            scaledUpMaze[newP] = 'X'
            dirs.forEach { scaledUpMaze[newP + it] = 'X' }
        }

        val bigArea = mazeArea.scale(3)
        val fill = MutableGrid(bigArea, scaledUpMaze) { '.' }

        val queue = minPriorityQueueOf(origin to 0)
        while (queue.isNotEmpty()) {
            val visit = queue.extractMin()
            fill[visit] = 'o'
            visit.directNeighbors(bigArea).filter { fill[it] == '.' }.forEach {
                queue.insertOrUpdate(it, 0)
            }
        }

        return mazeArea.allPoints().filter { o ->
            val t = o * 3 + (1 to 1)
            fill[t] == '.'
        }
    }

    fun traversePipes(): Pair<Int, Map<Point, Int>> {
        val distances = mutableMapOf(startPoint to 0)
        val queue = minPriorityQueueOf(startPoint to 0)

        var highest = 0
        while (queue.isNotEmpty()) {
            val visit = queue.extractMin()
            val nextLevel = distances[visit]!! + 1

            val pipe = piping[visit]!!
            val connectedTo = PIPES[pipe]!!.map { visit + it }
            connectedTo.filter { it in mazeArea }.forEach {
                if ((distances[it] ?: Int.MAX_VALUE) > nextLevel) {
                    if (nextLevel > highest) highest = nextLevel
                    distances[it] = nextLevel
                    queue.insertOrUpdate(it, nextLevel)
                }
            }
        }

        return highest to distances
    }

}

fun main() {
    solve<Day10> {
        """
            .....
            .S-7.
            .|.|.
            .L-J.
            .....
        """.trimIndent() part1 4

        """
            ...........
            .S-------7.
            .|F-----7|.
            .||.....||.
            .||.....||.
            .|L-7.F-J|.
            .|..|.|..|.
            .L--J.L--J.
            ...........
        """.trimIndent() part2 4

        """
            .F----7F7F7F7F-7....
            .|F--7||||||||FJ....
            .||.FJ||||||||L7....
            FJL7L7LJLJ||LJ.L-7..
            L--J.L7...LJS7F-7L7.
            ....F-J..F7FJ|L7L7L7
            ....L7.F7||L7|.L7L7|
            .....|FJLJ|FJ|F7|.LJ
            ....FJL-7.||.||||...
            ....L---J.LJ.LJLJ...
        """.trimIndent() part2 8

        """
            FF7FSF7F7F7F7F7F---7
            L|LJ||||||||||||F--J
            FL-7LJLJ||||||LJL-77
            F--JF--7||LJLJ7F7FJ-
            L---JF-JLJ.||-FJLJJ7
            |F|F-JF---7F7-L7L|7|
            |FFJF7L7F-JF7|JL---7
            7-L-JL7||F7|L7F-7F7|
            L.L7LFJ|||||FJL7||LJ
            L7JLJL-JLJLJL--JLJ.L
        """.trimIndent() part2 10
    }
}