import utils.digitToWord

class Day01 : Day(1, 2023, "Trebuchet?!") {

    private val calibrationList = input

    override fun part1() = calibrationList.sumOf {
        "${it.first(Char::isDigit)}${it.last(Char::isDigit)}".toInt()
    }

    override fun part2() = calibrationList.sumOf {
        it.firstDigit() * 10 + it.lastDigit()
    }

    private val digitsFromFront =
        (1..9).associateBy { it.toString() } +
                (1..9).associateBy { it.digitToWord() }

    private val digitsFromBack = digitsFromFront.mapKeys { (k, _) -> k.reversed() }

    fun <T : Any> String.first(digits: Map<String, T>): T =
        indices.firstNotNullOf { idx ->
            val sub = drop(idx)
            digits.firstNotNullOfOrNull { (k, v) ->
                v.takeIf { sub.startsWith(k) }
            }
        }

    fun String.firstDigit() = first(digitsFromFront)

    fun String.lastDigit() = reversed().first(digitsFromBack)

}

fun main() {
    solve<Day01> {
        """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent()(142)
        """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen
        """.trimIndent()(part2 = 281)
    }
}