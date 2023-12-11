import utils.*

class Day11 : Day(11, 2023, "Cosmic Expansion") {

    private val u = inputAsGrid

    private val galaxies = u.searchIndices('#').toList()
    private val emptyRows = u.rowIndices.filter {
        u[it].count { it == '#' } == 0
    }
    private val emptyCols = u.colIndices.filter { col ->
        u.indices.count { row -> u[row][col] == '#' } == 0
    }

    override fun part1() = universeDistances(2)
    override fun part2() = universeDistances(1_000_000)

    private fun universeDistances(factor: Long): Long = galaxies.combinations(2).sumOf { (a, b) ->
        val xRange = listOf(a.x, b.x).minMax()
        val yRange = listOf(a.y, b.y).minMax()
        (a manhattanDistanceTo b) +
                (emptyCols.count { it in xRange } + emptyRows.count { it in yRange }) * (factor - 1)
    }

}

fun main() {
    solve<Day11> {
        """
            ...#......
            .......#..
            #.........
            ..........
            ......#...
            .#........
            .........#
            ..........
            .......#..
            #...#.....
        """.trimIndent() part1 374 part2 82000210
    }
}
