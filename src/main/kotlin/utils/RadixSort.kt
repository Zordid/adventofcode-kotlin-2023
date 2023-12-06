package utils

import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private const val MAX_DIGIT = 1000000000

fun List<Int>.radixSort(): List<Int> {

    fun Int.getDigit(digit: Int): Int = (this / digit) % 10

    val reversedIndices = indices.reversed()
    var input = toIntArray()
    var output = IntArray(size)

    val counts = IntArray(10)
    var digit = 1
    while (true) {
        var stop = true
        for (i in input) {
            if (i >= digit) stop = false
            counts[i.getDigit(digit)]++
        }
        if (stop) break

        var prefix = 0
        for (idx in 0..9) {
            prefix += counts[idx]
            counts[idx] = prefix
        }

        for (idx in reversedIndices) {
            val i = input[idx]
            val d = i.getDigit(digit)
            counts[d]--
            output[counts[d]] = i
        }
        input = output.also { output = input }

        if (digit == MAX_DIGIT) break
        digit *= 10
        counts.fill(0)
    }
    return input.asList()
}

@OptIn(ExperimentalTime::class)
fun main() {
    val l = listOf(277, 806, 681, 462, 787, 163, 284, 166, 905, 518, 263, 395, 988, 307, 779, 721)
    println(l)
    println(l.radixSort())
    println(l.sorted())

    val size = 1_000_000
    val bigList = IntArray(size) { Random.nextInt(0, Int.MAX_VALUE) }.asList()

    val sort = measureTimedValue { bigList.sorted() }
    val radix = measureTimedValue { bigList.radixSort() }
    check(sort.value == radix.value)

    println("Sort took ${sort.duration}")
    println("Radix sort took ${radix.duration}")

    println(listOf(90, 70, 50, 80, 10).radixSort())
    println(listOf(0, 1, 0, 1).radixSort())

}