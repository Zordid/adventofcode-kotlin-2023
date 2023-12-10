import utils.*
import utils.Direction4.*

class Day10 : Day(10, 2023, "Pipe Maze") {
    val P = mapOf(
        '|' to setOf(NORTH, SOUTH),
        '-' to setOf(EAST, WEST),
        'L' to setOf(NORTH, EAST),
        'J' to setOf(NORTH, WEST),
        '7' to setOf(WEST, SOUTH),
        'F' to setOf(SOUTH, EAST),
    )

    val p = inputAsGrid
    val s = p.searchIndices('S').single().show()
    val a = p.area
    val pO = p.searchIndices { it != '.' }.map { it to p[it] }.toMap()
    val pipes = pO.let { m ->
        val neighbors = Direction4.all.map { s + it }.filter { it in a }.filter { p ->
            P[pO[p]]?.any { p + it == s } ?: false
        }.toSet()
        require(neighbors.size == 2)

        val element = P.entries.single { e ->
            e.value == neighbors.map { Direction4.ofVector(s, it) }.toSet()
        }

        m + (s to element.key)
    }

    override fun part1(): Any? {
        val posVisited = mutableMapOf(s to 0)
        val unvisited = pipes.keys.toMutableSet()

        while (unvisited.isNotEmpty()) {
            val lowest = posVisited.values.max()
            val next = posVisited.entries.filter { it.value == lowest }.map { it.key }

            var new = 0
            next.forEach { visit ->
                val pipe = pipes[visit]!!
                val canGoTo = P[pipe]!!.map { visit + it }.filter { it in a }
                canGoTo.forEach {
                    if ((posVisited[it] ?: Int.MAX_VALUE) > lowest + 1) {
                        posVisited[it] = lowest + 1
                        new++
                    }
                }
                unvisited -= visit
            }

            if (new == 0) break
        }

        return posVisited.values.max()
    }

    override fun part2(): Any? {
        val posVisited = mutableMapOf(s to 0)
        val unvisited = pipes.keys.toMutableSet()

        while (unvisited.isNotEmpty()) {
            val lowest = posVisited.values.max()
            val next = posVisited.entries.filter { it.value == lowest }.map { it.key }

            var new = 0
            next.forEach { visit ->
                val pipe = pipes[visit]!!
                val canGoTo = P[pipe]!!.map { visit + it }.filter { it in a }
                canGoTo.forEach {
                    if ((posVisited[it] ?: Int.MAX_VALUE) > lowest + 1) {
                        posVisited[it] = lowest + 1
                        new++
                    }
                }
                unvisited -= visit
            }

            if (new == 0) break
        }

        val big = mutableMapOf<Point, Char>()
        posVisited.keys.forEach { p ->
            val newP = p * 3 + (1 to 1)
            big[newP] = 'X'

            val e = pipes[p]!!

            when (e) {
                '|' -> {
                    big[newP + SOUTH] = 'X'
                    big[newP + NORTH] = 'X'
                }

                '-' -> {
                    big[newP + EAST] = 'X'
                    big[newP + WEST] = 'X'
                }

                'L' -> {
                    big[newP + EAST] = 'X'
                    big[newP + NORTH] = 'X'
                }

                'J' -> {
                    big[newP + WEST] = 'X'
                    big[newP + NORTH] = 'X'
                }

                '7' -> {
                    big[newP + WEST] = 'X'
                    big[newP + SOUTH] = 'X'
                }

                'F' -> {
                    big[newP + EAST] = 'X'
                    big[newP + SOUTH] = 'X'
                }
            }
        }

        val bigA = a.scale(3)
        val g = MutableGrid(bigA, big) { '.' }

        val queue = minPriorityQueueOf(origin to 0)
        while(queue.isNotEmpty()) {
            val n = queue.extractMin()
            g[n] = 'o'
            n.directNeighbors(bigA).filter { g[it] == '.' }.forEach {
                queue.insertOrUpdate(it, 0)
            }
        }
        //println(g.formatted(bigA))

        return a.allPoints().count { o ->
            val t = o * 3 + (1 to 1)
            g[t] == '.'
        }
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