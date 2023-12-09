package day9

import Day
import solve
import utils.comb

// Have a look at this wonderful Mathologer video that explains almost everything about the math behind this puzzle:
// https://youtu.be/4AuV93LOPcE?si=52Kqi4HqxxeSadbr

class Day09Math : Day(9, 2023, "Mirage Maintenance") {

    private val histories = input.map { it.split(' ').map(String::toInt) }

    override fun part1() = histories.sumOf {
        it.test()
        it.next()
    }

    override fun part2() = histories.sumOf { it.prev() }

    fun List<Int>.test() {
        val f = generateF()
        withIndex().forEach { (index, expected) ->
            val calculated = f(index)
            if (calculated != expected.toLong()) error("wrong at index $index")
        }
    }

    fun List<Int>.next(): Long {
        val f = generateF()
        return f(this.lastIndex + 1)
    }

    fun List<Int>.prev(): Long {
        val f = generateF()
        return f(-1)
    }

}

/**
 * This generates the polynomial rule F for the given sequence of numbers!
 * Uses the Gregory-Newton interpolation formula.
 * See: [Mathologer](https://youtu.be/4AuV93LOPcE?si=UdgX4yg4Q3rZYdnQ&t=975)
 */
fun List<Int>.generateF(): (Int) -> Long {
    val firsts = differenceSequence().map { it.first() }.withIndex()

    return { n ->
        firsts.sumOf { (index, c) -> c * comb(n, index) }
    }
}

fun main() {
    solve<Day09Math> {
        """
            0 3 6 9 12 15
            1 3 6 10 15 21
            10 13 16 21 30 45
        """.trimIndent() part1 114 part2 2
    }
}