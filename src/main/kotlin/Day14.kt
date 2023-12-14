import utils.*

class Day14 : Day(14, 2023, "Parabolic Reflector Dish") {

    val p = inputAsGrid

    override fun part1(): Any? {
        return p.tiltNorth().totalLoad()
    }

    override fun part2(): Any? {
        val cache = mutableMapOf<Grid<Char>, Long>()

        var pc = p
        var count = 0L
        do {
            cache[pc] = count
            pc = pc.cycle()
            count++
        } while (pc !in cache)

        val startCycle = cache[pc]!!
        val cycleLength = count - startCycle
        println("Same after $count, was before like this at ${cache[pc]} - cycle length $cycleLength!")

        val d = 1000000000L

        val remain = d - count
        val cyclesIn = remain / cycleLength
        println("remain: $remain, that is $cyclesIn cycles")
        val at = count + cyclesIn * cycleLength
        println("then we're at $at")
        val left = d - at

        repeat(left.toInt()) {
            pc = pc.cycle()
        }
        return pc.totalLoad()
    }

    fun Grid<Char>.cycle(): Grid<Char> =
        tiltNorth().tiltWest().tiltSouth().tiltEast()


    fun Grid<Char>.tiltNorth(): Grid<Char> {
        val n = toMutableGrid()
        colIndices.forEach { colIdx ->
            val col = column(colIdx)
            col.compactLeft().forEachIndexed { idx, c ->
                n[idx][colIdx] = c
            }
        }
        return n
    }

    fun Grid<Char>.tiltWest(): Grid<Char> {
        val n = toMutableGrid()
        rowIndices.forEach { rowIdx ->
            val row = row(rowIdx)
            row.compactLeft().forEachIndexed { idx, c ->
                n[rowIdx][idx] = c
            }
        }
        return n
    }

    fun Grid<Char>.tiltEast(): Grid<Char> {
        val n = toMutableGrid()
        rowIndices.forEach { rowIdx ->
            val row = row(rowIdx)
            row.compactRight().forEachIndexed { idx, c ->
                n[rowIdx][idx] = c
            }
        }
        return n
    }

    fun Grid<Char>.tiltSouth(): Grid<Char> {
        val n = toMutableGrid()
        colIndices.forEach { colIdx ->
            val col = column(colIdx)
            col.compactRight().forEachIndexed { idx, c ->
                n[idx][colIdx] = c
            }
        }
        return n
    }

    fun List<Char>.compactLeft(): List<Char> =
        splitBy { it == '#' }.map {
            val count = it.count { it == 'O' }
            List(count) { 'O' } + List(it.size - count) { '.' } + '#'
        }.flatten().take(size)

    fun List<Char>.compactRight(): List<Char> =
        splitBy { it == '#' }.map {
            val count = it.count { it == 'O' }
            List(it.size - count) { '.' } + List(count) { 'O' } + '#'
        }.flatten().take(size)


    fun Grid<Char>.totalLoad(): Long {
        return rowIndices.sumOf {
            val f = area.height - it
            row(it).count { it == 'O' }.toLong() * f
        }
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