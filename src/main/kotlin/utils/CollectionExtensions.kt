@file:Suppress("unused")

package utils

import kotlin.math.max
import kotlin.math.min

/**
 * Splits elements by a defined [delimiter] predicate into groups of elements.
 *
 * @param limit limits the number of generated groups.
 * @param keepEmpty if true, groups without elements are preserved, otherwise will be omitted in the result.
 * @return a List of the groups of elements.
 */
fun <T> Iterable<T>.splitBy(limit: Int = 0, keepEmpty: Boolean = true, delimiter: (T) -> Boolean): List<List<T>> {
    require(limit >= 0) { "Limit must not be negative, but was $limit" }
    val isLimited = limit > 0

    val result = ArrayList<List<T>>(if (isLimited) limit.coerceAtMost(10) else 10)
    var currentSubList = mutableListOf<T>()
    for (element in this) {
        if ((!isLimited || (result.size < limit - 1)) && delimiter(element)) {
            if (keepEmpty || currentSubList.isNotEmpty()) {
                result += currentSubList
                currentSubList = mutableListOf()
            }
        } else {
            currentSubList += element
        }
    }
    if (keepEmpty || currentSubList.isNotEmpty())
        result += currentSubList
    return result
}

/**
 * Splits nullable elements by `null` values. The resulting groups will not contain any nulls.
 *
 * @param keepEmpty if true, groups without elements are preserved, otherwise will be omitted in the result.
 * @return a List of the groups of elements.
 */
@Suppress("UNCHECKED_CAST")
fun <T> Iterable<T>.splitByNulls(keepEmpty: Boolean = true): List<List<T & Any>> =
    splitBy(keepEmpty = keepEmpty) { it == null } as List<List<T & Any>>

fun Pair<Int, Int>.asRange(): IntRange = min(first, second)..max(first, second)
fun Pair<Long, Long>.asRange(): LongRange = min(first, second)..max(first, second)

/**
 * Returns the smallest and largest element or `null` if there are no elements.
 */
fun <T : Comparable<T>> Iterable<T>.minMaxOrNull(): MinMaxRange<T>? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    var min = iterator.next()
    var max = min
    while (iterator.hasNext()) {
        val e = iterator.next()
        if (min > e) min = e
        if (e > max) max = e
    }
    return MinMaxRange(min, max)
}

interface MinMax<T> {
    val min: T
    val max: T

    operator fun component1(): T = min
    operator fun component2(): T = max
}

data class MinMaxResult<T>(override val min: T, override val max: T) : MinMax<T>

data class MinMaxRange<T : Comparable<T>>(
    override val min: T, override val max: T
) : MinMax<T>, ClosedRange<T> {
    override val start: T get() = min
    override val endInclusive: T get() = max
}

val MinMaxRange<Int>.range: IntRange get() = min..max
val MinMaxRange<Long>.range: LongRange get() = min..max
val MinMaxRange<Char>.range: CharRange get() = min..max

@JvmName("iteratorInt")
operator fun MinMaxRange<Int>.iterator(): Iterator<Int> = range.iterator()
@JvmName("iteratorLong")
operator fun MinMaxRange<Long>.iterator(): Iterator<Long> = range.iterator()
@JvmName("iteratorChar")
operator fun MinMaxRange<Char>.iterator(): Iterator<Char> = range.iterator()

/**
 * Returns the smallest and largest element or throws [NoSuchElementException] if there are no elements.
 */
fun <T : Comparable<T>> Iterable<T>.minMax(): MinMaxRange<T> = minMaxOrNull() ?: throw NoSuchElementException()

/**
 * Returns the smallest and largest element or `null` if there are no elements.
 */
fun <T : Comparable<T>> Sequence<T>.minMaxOrNull(): MinMaxRange<T>? = asIterable().minMaxOrNull()

/**
 * Returns the smallest and largest element or throws [NoSuchElementException] if there are no elements.
 */
fun <T : Comparable<T>> Sequence<T>.minMax(): MinMaxRange<T> = minMaxOrNull() ?: throw NoSuchElementException()

/**
 * Returns the first element yielding the smallest and the first element yielding the largest value
 * of the given function or `null` if there are no elements.
 */
inline fun <T, R : Comparable<R>> Iterable<T>.minMaxByOrNull(selector: (T) -> R): MinMax<T>? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    var minElem = iterator.next()
    var maxElem = minElem
    if (!iterator.hasNext()) return MinMaxResult(minElem, maxElem)
    var minValue = selector(minElem)
    var maxValue = minValue
    do {
        val e = iterator.next()
        val v = selector(e)
        if (minValue > v) {
            minElem = e
            minValue = v
        }
        if (v > maxValue) {
            maxElem = e
            maxValue = v
        }
    } while (iterator.hasNext())
    return MinMaxResult(minElem, maxElem)
}

/**
 * Returns the first element yielding the smallest and the first element yielding the largest value
 * of the given function or throws [NoSuchElementException] if there are no elements.
 */
inline fun <T, R : Comparable<R>> Iterable<T>.minMaxBy(selector: (T) -> R): MinMax<T> =
    minMaxByOrNull(selector) ?: throw NoSuchElementException()

/**
 * Efficiently generate the top [n] smallest elements without sorting all elements.
 */
@Suppress("DuplicatedCode")
fun <T : Comparable<T>> Iterable<T>.minN(n: Int): List<T> {
    require(n >= 0) { "Number of smallest elements must not be negative" }
    val iterator = iterator()
    when {
        n == 0 || !iterator.hasNext() -> return emptyList()
        n == 1 -> return minOrNull()?.let { listOf(it) } ?: emptyList()
        this is Collection<T> && n >= size -> return this.sorted()
    }

    val smallest = ArrayList<T>(n.coerceAtMost(10))
    var min = iterator.next()
        .also { smallest += it }
        .let { MinMaxRange(it, it) }

    while (iterator.hasNext()) {
        val e = iterator.next()
        when {
            smallest.size < n -> {
                smallest += e
                min = when {
                    e < min.min -> min.copy(min = e)
                    e > min.max -> min.copy(max = e)
                    else -> min
                }
            }

            e < min.max -> {
                val removeAt = smallest.indexOfLast { it.compareTo(min.max) == 0 }
                smallest.removeAt(removeAt)
                smallest += e
                min = smallest.minMax()
            }
        }
    }
    return smallest.sorted()
}

/**
 * Efficiently generate the top [n] largest elements without sorting all elements.
 */
@Suppress("DuplicatedCode")
fun <T : Comparable<T>> Iterable<T>.maxN(n: Int): List<T> {
    require(n >= 0) { "Number of largest elements must not be negative" }
    val iterator = iterator()
    when {
        n == 0 || !iterator.hasNext() -> return emptyList()
        n == 1 -> return maxOrNull()?.let { listOf(it) } ?: emptyList()
        this is Collection<T> && n >= size -> return this.sortedDescending()
    }

    val largest = ArrayList<T>(n.coerceAtMost(10))
    var max = iterator.next()
        .also { largest += it }
        .let { MinMaxRange(it, it) }

    while (iterator.hasNext()) {
        val e = iterator.next()
        when {
            largest.size < n -> {
                largest += e
                max = when {
                    e < max.min -> max.copy(min = e)
                    e > max.max -> max.copy(max = e)
                    else -> max
                }
            }

            e > max.min -> {
                val removeAt = largest.indexOfLast { it.compareTo(max.min) == 0 }
                largest.removeAt(removeAt)
                largest += e
                max = largest.minMax()
            }
        }
    }
    return largest.sortedDescending()
}
