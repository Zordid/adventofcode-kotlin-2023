import utils.*

class Day14 : Day(14, 2023, "Parabolic Reflector Dish") {

    private val platform = inputAsGrid

    override fun part1() = platform.tiltNorth().totalLoad()

    override fun part2(): Long {
        val requiredCycles = 1_000_000_000

        val (startCycle, endCycle, endSituation) = cycleDetector(platform) { it.cycle() }
        val cycleLength = endCycle - startCycle

        alog { "Same after $endCycle, was before like this at $startCycle - cycle length $cycleLength!" }

        val remain = requiredCycles - endCycle
        val cyclesIn = remain / cycleLength
        alog { "remain: $remain, that is $cyclesIn cycles" }
        val at = endCycle + cyclesIn * cycleLength
        alog { "then we're at $at" }
        val left = requiredCycles - at

        return generateSequence(endSituation) { it.cycle() }.take(left + 1).last().totalLoad()
    }

    private fun <T> cycleDetector(seed: T, process: (T) -> T): Triple<Int, Int, T> {
        val cache = mutableMapOf<T, Int>()

        var current = seed
        var count = 0
        do {
            cache[current] = count
            current = process(current)
            count++
        } while (current !in cache)

        val startCycle = cache[current]!!
        val endCycle = count
        return Triple(startCycle, endCycle, current)
    }

    private fun Grid<Char>.cycle(): Grid<Char> =
        tiltNorth().tiltWest().tiltSouth().tiltEast()

    private fun Grid<Char>.tiltNorth(): Grid<Char> {
        val n = toMutableGrid()
        colIndices.forEach { colIdx ->
            val col = column(colIdx)
            col.compactLeft().forEachIndexed { idx, c ->
                n[idx][colIdx] = c
            }
        }
        return n
    }

    private fun Grid<Char>.tiltWest(): Grid<Char> {
        val n = toMutableGrid()
        rowIndices.forEach { rowIdx ->
            val row = row(rowIdx)
            row.compactLeft().forEachIndexed { idx, c ->
                n[rowIdx][idx] = c
            }
        }
        return n
    }

    private fun Grid<Char>.tiltEast(): Grid<Char> {
        val n = toMutableGrid()
        rowIndices.forEach { rowIdx ->
            val row = row(rowIdx)
            row.compactRight().forEachIndexed { idx, c ->
                n[rowIdx][idx] = c
            }
        }
        return n
    }

    private fun Grid<Char>.tiltSouth(): Grid<Char> {
        val n = toMutableGrid()
        colIndices.forEach { colIdx ->
            val col = column(colIdx)
            col.compactRight().forEachIndexed { idx, c ->
                n[idx][colIdx] = c
            }
        }
        return n
    }

    private fun List<Char>.compactLeft(): List<Char> =
        splitBy { it == '#' }.map {
            val count = it.count { it == 'O' }
            List(count) { 'O' } + List(it.size - count) { '.' } + '#'
        }.flatten().take(size)

    private fun List<Char>.compactRight(): List<Char> =
        splitBy { it == '#' }.map {
            val count = it.count { it == 'O' }
            List(it.size - count) { '.' } + List(count) { 'O' } + '#'
        }.flatten().take(size)


    private fun Grid<Char>.totalLoad(): Long =
        rowIndices.sumOf {
            val f = area.height - it
            row(it).count { it == 'O' }.toLong() * f
        }

}

fun main() {
    solve<Day14> {
        """
            O....#....
            O.OO#....#
            .....##...
            OO.#O....O
            .O.....O#.
            O.#..O.#.#
            ..O..#O..O
            .......O..
            #....###..
            #OO..#....
        """.trimIndent() part1 136 part2 64
    }
}