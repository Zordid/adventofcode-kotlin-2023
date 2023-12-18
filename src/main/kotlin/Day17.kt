import utils.*

class Day17 : Day(17, 2023, "Clumsy Crucible") {

    val p = inputAsGrid.map { it.map { it.digitToInt() } }
    val area = p.area

    data class State(
        val pos: Point,
        val movedStraight: Int,
        val dir: Direction4?,
    )

    override fun part1(): Any? {

        val g = object : Graph<State> {
            override fun neighborsOf(node: State): Collection<State> {
                return Direction4.all.map { d ->
                    State(
                        node.pos + d,
                        movedStraight = if (d == node.dir) node.movedStraight + 1 else 1,
                        dir = d
                    )
                }.filter { it.pos in area && it.movedStraight <= 3 && it.dir != node.dir?.opposite }
            }

            override fun cost(from: State, to: State): Int =
                p[to.pos]

            override fun costEstimation(from: State, to: State): Int =
                from.pos manhattanDistanceTo to.pos
        }

        val start = State(origin, 0, null)

        val x = Dijkstra(start, g::neighborsOf, g::cost).search { it.pos == area.lowerRight }

        val path = x.path
        println(p.formatted { pos, v ->
            if (pos in path.map { it.pos })
                path.first { it.pos == pos }.movedStraight.toString() else "."
        })

        return path.drop(1).sumOf { p[it.pos] }
    }

    class X : Graph<State> {
        override fun neighborsOf(node: State): Collection<State> {
            TODO("Not yet implemented")
        }
    }

    override fun part2(): Any? {

        val g = object : Graph<State> {
            override fun neighborsOf(node: State): Collection<State> {
                return (if (node.movedStraight < 4 && node.pos != origin) listOf(
                    node.copy(node.pos + node.dir!!, movedStraight = node.movedStraight + 1)
                )
                else Direction4.all.map { d ->
                    State(
                        node.pos + d,
                        movedStraight = if (d == node.dir) node.movedStraight + 1 else 1,
                        dir = d
                    )
                }).filter { it.pos in area && it.movedStraight <= 10 && it.dir != node.dir?.opposite }
            }

            override fun cost(from: State, to: State): Int =
                p[to.pos]

            override fun costEstimation(from: State, to: State): Int =
                from.pos manhattanDistanceTo to.pos
        }

        val start = State(origin, 0, null)


        X().dijkstraSearch(start, start)


        val x = Dijkstra(start, g::neighborsOf, g::cost).search { it.pos == area.lowerRight && it.movedStraight >= 4 }

        val path = x.path
        println(p.formatted { pos, v ->
            if (pos in path.map { it.pos })
                path.first { it.pos == pos }.movedStraight.toString() else "."
        })

        return path.drop(1).sumOf { p[it.pos] }
    }

}

fun main() {
    solve<Day17> {
        """
            2413432311323
            3215453535623
            3255245654254
            3446585845452
            4546657867536
            1438598798454
            4457876987766
            3637877979653
            4654967986887
            4564679986453
            1224686865563
            2546548887735
            4322674655533
        """.trimIndent() part1 102 part2 94

        """
            111111111111
            999999999991
            999999999991
            999999999991
            999999999991
        """.trimIndent() part2 71
    }
}
