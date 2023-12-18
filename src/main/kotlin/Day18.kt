import utils.*
import kotlin.math.abs

class Day18 : Day(18, 2023, "Lavaduct Lagoon") {

    val p = input.map {
        val (d, s, c) = it.split(' ')
        Triple(
            when (d) {
                "R" -> Direction4.RIGHT
                "L" -> Direction4.LEFT
                "U" -> Direction4.UP
                "D" -> Direction4.DOWN
                else -> error(d)
            }, s.toInt(), c.drop(1).dropLast(1)
        )
    }

    val p2 = input.map {
        val (_, _, c) = it.split(' ')
        val steps = c.drop(2).dropLast(2).toInt(16)
        val dir = when (c.dropLast(1).last()) {
            '0' -> Direction4.RIGHT
            '1' -> Direction4.DOWN
            '2' -> Direction4.LEFT
            '3' -> Direction4.UP
            else -> error(c)
        }
        dir to steps
    }.show()

    override fun part1(): Any? {
        val map = mutableMapOf<Point, String>()

        var pos = origin
        p.forEach { (d, s, c) ->
            val end = pos + (d.vector * s)
            (pos..end).forEach { map[it] = c }
            pos = end
        }

        val xr = map.keys.map { it.x }.minMax()
        val yr = map.keys.map { it.y }.minMax()
        val area = (xr.min to yr.min) to (xr.max to yr.max)
        val bigger = area.grow(1)

        println(bigger.size)
        alog { bigger }
        println(map.size)

        val q = queueOf(bigger.upperLeft)

        val seen = mutableSetOf<Point>()
        while (q.isNotEmpty()) {
            val c = q.removeFirst()
            seen += c
            q += c.directNeighbors().filter { it !in q && it in bigger && it !in map && it !in seen }
        }

        log {
            (map.formatted(restrictArea = bigger) { x, c ->
                when {
                    x in seen -> "~"
                    c != "." -> "#"
                    else -> c
                }
            })
        }

        println(seen.size)

        return bigger.size - seen.size
    }

    override fun part2(): Any? {
        val perimeter = p2.sumOf { it.second.toLong() }
        val corners = p2.runningFold(origin) { acc, instruction ->
            acc + (instruction.first * instruction.second)
        }
        require(corners.first() == corners.last()) { "not a closed loop" }

        val area = abs(corners.zipWithNext().sumOf { (ci, cj) ->
            ci.x.toLong() * cj.y - cj.x.toLong() * ci.y
        }).also { require(it % 2 == 0L) { "shoelace area $it cannot be halved" } } / 2
        require(perimeter % 2 == 0L) { "perimeter of $perimeter cannot be halved" }

        val inside = area - perimeter / 2 + 1
        return inside + perimeter
    }

}

fun main() {
    solve<Day18> {
        """
            R 6 (#70c710)
            D 5 (#0dc571)
            L 2 (#5713f0)
            D 2 (#d2c081)
            R 2 (#59c680)
            D 2 (#411b91)
            L 5 (#8ceee2)
            U 2 (#caa173)
            L 1 (#1b58a2)
            U 2 (#caa171)
            R 2 (#7807d2)
            U 3 (#a77fa3)
            L 2 (#015232)
            U 2 (#7a21e3)
        """.trimIndent() part1 62 part2 952408144115
    }
}