package utils

interface BaseGrid2D<T> {
    fun checkBounds(column: Int, row: Int): Boolean

    operator fun get(column: Int, row: Int): T
    operator fun get(point: Point): T

    fun getOrNull(column: Int, row: Int): T?
    fun getOrDefault(column: Int, row: Int, default: T): T
    fun getOrElse(column: Int, row: Int, default: (column: Int, row: Int) -> T): T
}

interface InfiniteGrid2D<T> : BaseGrid2D<T> {
    override fun checkBounds(column: Int, row: Int): Boolean = true

    @Deprecated("useless for InfiniteGrid2D", ReplaceWith("get(column, row)"))
    override fun getOrNull(column: Int, row: Int): T? = get(column, row)

    @Deprecated("useless for InfiniteGrid2D", ReplaceWith("get(column, row)"))
    override fun getOrDefault(column: Int, row: Int, default: T): T = get(column, row)

    @Deprecated("useless for InfiniteGrid2D", ReplaceWith("get(column, row)"))
    override fun getOrElse(column: Int, row: Int, default: (column: Int, row: Int) -> T): T = get(column, row)
}

private class InfiniteBackedBy<T>(val base: Grid2D<T>, val outsideValue: T) : InfiniteGrid2D<T> {
    override fun get(point: Point): T =
        if (base.checkBounds(point.first, point.second)) base[point] else outsideValue

    override fun get(column: Int, row: Int): T =
        if (base.checkBounds(column, row)) base[column, row] else outsideValue
}

fun <T> Grid2D<T>.toInfiniteGrid(default: T): InfiniteGrid2D<T> = InfiniteBackedBy(this, default)

interface Grid2D<T> : BaseGrid2D<T> {
    val rows: Int
    val columns: Int
    val size: Int
    val area: Area get() = origin to (columns - 1 to rows - 1)

    override fun checkBounds(column: Int, row: Int): Boolean =
        column in 0 until columns && row in 0 until rows

    override fun get(point: Point): T = get(point.first, point.second)

    override fun getOrNull(column: Int, row: Int): T? {
        checkBounds(column, row) || return null
        return get(column, row)
    }

    override fun getOrDefault(column: Int, row: Int, default: T): T {
        checkBounds(column, row) || return default
        return get(column, row)
    }

    override fun getOrElse(column: Int, row: Int, default: (column: Int, row: Int) -> T): T {
        checkBounds(column, row) || return default(column, row)
        return get(column, row)
    }

    fun toList(): List<List<T>> =
        rowIndices.map { row -> columnIndices.map { col -> get(col, row) } }

    private class BaseListGrid2D<T>(
        val underlying: List<List<T>>,
        val filler: (column: Int, row: Int) -> T = ::reportHoleInGrid,
    ) : Grid2D<T> {
        override val rows: Int get() = underlying.size
        override val columns: Int by lazy { underlying.maxOf { it.size } }
        override val size: Int by lazy { rows * columns }

        override fun get(column: Int, row: Int): T {
            ensureBounds(column, row)
            return underlying
                .getOrElse(row) { return filler(column, row) }
                .getOrElse(column) { return filler(column, row) }
        }

        override fun toList(): List<List<T>> = underlying

    }

    companion object {
        fun <T> of(base: List<List<T>>, filler: (column: Int, row: Int) -> T = ::reportHoleInGrid): Grid2D<T> =
            BaseListGrid2D(base, filler)

        fun <T> of(base: List<List<T>>, filler: T): Grid2D<T> =
            BaseListGrid2D(base) { _, _ -> filler }
    }

}

fun <T, R> Grid2D<T>.map(mapper: (T) -> R): Grid2D<R> = MappedGrid2D(this, mapper)

@Suppress("UNCHECKED_CAST")
private class MappedGrid2D<T, R>(val base: Grid2D<T>, val mapper: (T) -> R) : Grid2D<R> by base as Grid2D<R> {
    override fun get(column: Int, row: Int): R = mapper(base[column, row])

    override fun getOrNull(column: Int, row: Int): R? {
        checkBounds(column, row) || return null
        return mapper(base[column, row])
    }

    override fun getOrElse(column: Int, row: Int, default: (column: Int, row: Int) -> R): R {
        checkBounds(column, row) || return default(column, row)
        return mapper(base[column, row])
    }
}

operator fun Grid2D<*>.contains(p: Point) =
    p.first in columnIndices && p.second in rowIndices


val <T> Grid2D<T>.rowIndices get() = 0 until rows
val <T> Grid2D<T>.columnIndices get() = 0 until columns
val <T> Grid2D<T>.indices
    get() = sequence {
        for (row in rowIndices)
            for (col in columnIndices)
                yield(col to row)
    }
val <T> Grid2D<T>.values get() = indices.map { this[it] }
fun <T> Grid2D<T>.flatten() = values
val <T> Grid2D<T>.indexedValues get() = indices.map { it to this[it] }

fun <T> List<List<T>>.asGrid2D() = Grid2D.of(this)

private fun reportHoleInGrid(column: Int, row: Int): Nothing =
    throw NoSuchElementException("Hole in Grid2D detected at column $column row $row)")

private fun Grid2D<*>.ensureBounds(column: Int, row: Int) {
    if (!checkBounds(column, row))
        throw IndexOutOfBoundsException("Index: column $column row $row, Size: $columns by $rows")
}