package day9

import Day
import solve

class Day09Optimized : Day(9, 2023, "Mirage Maintenance") {

    private val histories = input.map { it.split(' ').map(String::toInt) }

    override fun part1() = histories.sumOf { it.predictNext() }

    override fun part2() = histories.sumOf { it.predictPrevious() }

}

fun List<Int>.predictNext() =
    differenceSequence().sumOf { it.last() }

fun List<Int>.predictPrevious(): Int {
    val firstValues = differenceSequence().map { it.first() }
    return firstValues.chunked(2).map { it.first() - it.getOrElse(1) { 0 } }.sum()
}

fun List<Int>.differenceSequence() =
    generateSequence(this) { v ->
        v.zipWithNext().map { (a, b) -> b - a }
    }.takeWhile { v -> v.any { it != 0 } }

fun main() {
    solve<Day09Optimized> {
        """
            0 3 6 9 12 15
            1 3 6 10 15 21
            10 13 16 21 30 45
        """.trimIndent() part1 114 part2 2
    }
}