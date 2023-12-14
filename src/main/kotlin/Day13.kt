import utils.*

class Day13 : Day(13, 2023, "Point of Incidence") {

    private val patterns = inputAsGroups.map { it.map(String::toList) }

    override fun part1(): Int =
        patterns.sumOf { pattern ->
            pattern.findHorizontalReflections().singleOrNull()?.let { it * 100 }
                ?: pattern.findVerticalReflections().single()
        }

    override fun part2(): Int =
        patterns.sumOf { pattern ->
            val h = pattern.findHorizontalReflections().singleOrNull() ?: -1
            val v = pattern.findVerticalReflections().singleOrNull() ?: -1

            val mutablePattern = pattern.toMutableGrid()
            pattern.area.allPoints().firstNotNullOf { position ->
                // switch position
                mutablePattern[position] = pattern[position].flipped()

                (mutablePattern.findHorizontalReflections() - h).singleOrNull()?.let { it * 100 }
                    ?: (mutablePattern.findVerticalReflections() - v).singleOrNull()
                        .also {
                            // restore at position
                            mutablePattern[position] = pattern[position]
                        }
            }
        }

    private fun Grid<*>.findHorizontalReflections() =
        (1..lastIndex).filter { line ->
            val up = slice(0..<line).reversed()
            val down = slice(line..lastIndex)

            up.zip(down).all { (a, b) -> a == b }
        }

    private fun Grid<*>.findVerticalReflections() =
        transposed().findHorizontalReflections()

    private fun Char.flipped() = when (this) {
        '.' -> '#'
        else -> '.'
    }

}

fun main() {
    solve<Day13> {
        """
            #.##..##.
            ..#.##.#.
            ##......#
            ##......#
            ..#.##.#.
            ..##..##.
            #.#.##.#.

            #...##..#
            #....#..#
            ..##..###
            #####.##.
            #####.##.
            ..##..###
            #....#..#
        """.trimIndent() part1 405 part2 400
    }
}
