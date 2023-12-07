import utils.*

class Day03 : Day(3, 2023, "Gear Ratios") {

    private val schematic = inputAsGrid

    override fun part1(): Int {
        val startOfNumbers = schematic.searchIndices {
            it != '.' && !it.isDigit()
        }.flatMap {
            it.surroundingNeighbors(schematic.area).filter { schematic[it].isDigit() }
        }.map {
            it.leftWhile { it in schematic.area && schematic[it].isDigit() }
        }.toSet()

        return startOfNumbers.sumOf { start -> schematic.numberAt(start) }
    }

    override fun part2(): Long {
        val startOfNumbers = schematic.searchIndices {
            it == '*'
        }.toList().map {
            it.surroundingNeighbors(schematic.area)
                .filter { schematic[it].isDigit() }
                .map { it.leftWhile { it in schematic.area && schematic[it].isDigit() } }.toSet()
        }.also { c ->
            println(schematic.formatted(transform = highlight(c.flatten())))
        }.filter {
            it.size == 2
        }

        return startOfNumbers.sumOf { numbers ->
            numbers.map { start -> schematic.numberAt(start) }.product()
        }
    }

    private fun Grid<Char>.numberAt(start: Point): Int {
        val end = start.rightWhile { it in area && get(it).isDigit() }
        return (start..end).fold("") { acc, p -> acc + get(p) }.toInt()
    }

}

fun main() {
    solve<Day03> {
        """
            467..114..
            ...*......
            ..35..633.
            ......#...
            617*......
            .....+.58.
            ..592.....
            ......755.
            ...${'$'}.*....
            .664.598..
        """.trimIndent()(4361, 467835)
    }
}