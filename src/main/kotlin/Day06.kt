import utils.product

class Day06 : Day(6, 2023, "Wait For It") {

    private val times = input.first().extractAllIntegers()
    private val distances = input.drop(1).first().extractAllIntegers()

    override fun part1() = times.zip(distances).map { (time, dist) ->
        (0..time).count { charge ->
            val distance = (time - charge) * charge
            distance > dist
        }
    }.product()

    override fun part2(): Any? {
        val time = times.joinToString("").toLong()
        val dist = distances.joinToString("").toLong()
        return (0..time).count { charge ->
            val distance = (time - charge) * charge
            distance > dist
        }
    }

}

fun main() {
    solve<Day06> {
        """
            Time:      7  15   30
            Distance:  9  40  200
        """.trimIndent()(288, 71503)
    }
}