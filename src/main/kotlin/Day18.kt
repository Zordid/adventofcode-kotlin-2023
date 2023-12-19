import utils.*
import utils.Direction4.Companion.DOWN
import utils.Direction4.Companion.LEFT
import utils.Direction4.Companion.RIGHT
import utils.Direction4.Companion.UP
import kotlin.math.abs

class Day18 : Day(18, 2023, "Lavaduct Lagoon") {

    private val p1Instructions = input.map {
        val (d, s, _) = it.split(' ')
        val steps = s.toInt()
        val dir = when (d) {
            "R" -> RIGHT
            "D" -> DOWN
            "L" -> LEFT
            else -> UP
        }
        dir to steps
    }

    private val p2Instructions = input.map {
        val (_, _, c) = it.split(' ')
        val steps = c.drop(2).dropLast(2).toInt(16)
        val dir = when (c.dropLast(1).last()) {
            '0' -> RIGHT
            '1' -> DOWN
            '2' -> LEFT
            else -> UP
        }
        dir to steps
    }

    override fun part1(): Int {
        val map = mutableMapOf<Point, String>()

        var pos = origin
        p1Instructions.forEach { (d, s) ->
            val end = pos + (d.vector * s)
            (pos..end).forEach { map[it] = "#" }
            pos = end
        }

        val area = map.area
        val bigger = area + 1

        println(bigger.size)
        alog { bigger }
        println(map.size)

        val q = dequeOf(bigger.upperLeft)

        val seen = mutableSetOf<Point>()
        while (q.isNotEmpty()) {
            val c = q.removeFirst()
            seen += c
            q += c.directNeighbors().filter { it !in q && it in bigger && it !in map && it !in seen }
        }

        log {
            (map.formatted(area = bigger) { x, c ->
                when {
                    x in seen -> "~"
                    else -> "#"
                }
            })
        }

        println(seen.size)

        return bigger.size - seen.size
    }

    override fun part2(): Long {
        val boundary = p2Instructions.sumOf { it.second.toLong() }
        val corners = p2Instructions.runningFold(origin) { acc, instruction ->
            acc + (instruction.first * instruction.second)
        }
        require(corners.first() == corners.last()) { "not a closed loop" }

        // area by Shoelace algorithm https://en.wikipedia.org/wiki/Shoelace_formula
        // https://youtu.be/0KjG8Pg6LGk?si=qC_1iX1YhQlGvI1o
        val area = abs(corners.zipWithNext().sumOf { (ci, cj) ->
            ci.x.toLong() * cj.y - cj.x.toLong() * ci.y
        }) / 2

        // according to Pick's theorem: A = i + b / 2 - 1
        // https://en.wikipedia.org/wiki/Pick%27s_theorem
        val inside = area - boundary / 2 + 1
        return inside + boundary
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