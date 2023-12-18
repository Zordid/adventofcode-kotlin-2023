import utils.*
import utils.Direction4.Companion.DOWN
import utils.Direction4.Companion.LEFT
import utils.Direction4.Companion.RIGHT
import utils.Direction4.Companion.UP

class Day16 : Day(16, 2023, "The Floor Will Be Lava") {

    val grid = inputAsGrid
    val area = grid.area

    override fun part1() = grid.countEnergizedTiles(origin, RIGHT)

    override fun part2() = area.border().maxOf { pos ->
        val dirs = Direction4.all.filter { (pos - it) !in area }
        dirs.maxOf { dir -> grid.countEnergizedTiles(pos, dir) }
    }

    private fun Grid<Char>.countEnergizedTiles(start: Point, direction: Direction4): Int {
        val beenHere = mutableSetOf<Pair<Point, Direction4>>()
        val activeBeams = dequeOf(start - direction to direction)
        while (activeBeams.isNotEmpty()) {
            val (pos, dir) = activeBeams.removeFirst()
            val nextPos = pos + dir
            if (nextPos in area && (nextPos to dir) !in beenHere) {
                beenHere += (nextPos to dir)
                activeBeams += when (grid[nextPos]) {
                    '/' -> listOf(
                        when (dir) {
                            RIGHT -> UP
                            DOWN -> LEFT
                            LEFT -> DOWN
                            else -> RIGHT
                        }
                    )

                    '\\' -> listOf(
                        when (dir) {
                            RIGHT -> DOWN
                            DOWN -> RIGHT
                            LEFT -> UP
                            else -> LEFT
                        }
                    )

                    '-' -> when (dir) {
                        UP, DOWN -> listOf(LEFT, RIGHT)
                        else -> listOf(dir)
                    }

                    '|' -> when (dir) {
                        LEFT, RIGHT -> listOf(UP, DOWN)
                        else -> listOf(dir)
                    }

                    else -> listOf(dir)
                }.map { nextPos to it }
            }
        }

        val energizedTiles = beenHere.map { it.first }.toSet()
        return energizedTiles.size
    }

}

fun main() {
    solve<Day16> {
        """
            .|...\....
            |.-.\.....
            .....|-...
            ........|.
            ..........
            .........\
            ..../.\\..
            .-.-/..|..
            .|....-|.\
            ..//.|....
        """.trimIndent() part1 46 part2 51
    }
}