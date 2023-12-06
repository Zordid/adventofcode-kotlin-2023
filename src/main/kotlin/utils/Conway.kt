package utils

//fun <T> conwaySequence(
//    start: Grid<T>,
//    rule: (grid: Grid<T>, p: Point, v: T) -> T?,
//): Sequence<Grid<T>> =
//    generateSequence(start) { prevGeneration ->
//        val nextGeneration = prevGeneration.toMutableGrid()
//        var anyChange = false
//        prevGeneration.forAreaIndexed { p, oldValue ->
//            rule(prevGeneration, p, oldValue)?.also { newValue ->
//                anyChange = anyChange || (oldValue != newValue)
//                nextGeneration[p] = newValue
//            }
//        }
//        if (anyChange) nextGeneration else null
//    }
//
