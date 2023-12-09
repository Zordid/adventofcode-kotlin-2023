class Day09 : Day(9, 2023, "Mirage Maintenance") {

    private val histories = input.map { it.split(' ').map(String::toInt) }

    override fun part1() = histories.sumOf { it.predictNext() }

    override fun part2() = histories.sumOf { it.predictPrevious() }

    private fun List<Int>.predictNext(): Int {
        val lastValues = generateSequence(this) { v ->
            v.zipWithNext().map { (a, b) -> b - a }
        }.takeWhile { v -> v.any { it != 0 } }.map { it.last() }
        return lastValues.sum()
    }

    private fun List<Int>.predictPrevious(): Int {
        val firstValues = generateSequence(this) { v ->
            v.zipWithNext().map { (a, b) -> b - a }
        }.takeWhile { v -> v.any { it != 0 } }.map { it.first() }
        return firstValues.toList().reversed().fold(0) { acc, next -> next - acc }
    }

}

fun main() {
    solve<Day09> {
        """
            0 3 6 9 12 15
            1 3 6 10 15 21
            10 13 16 21 30 45
        """.trimIndent() part1 114 part2 2
    }
}