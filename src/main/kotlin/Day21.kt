import utils.*
import java.math.BigInteger

class Day21 : Day(21, 2023, "Step Counter") {

    val p = inputAsGrid

    val start = p.searchIndices('S').single()
    val area = p.area
    val gardens = p.searchIndices('.', 'S').count()
    val rocks = p.searchIndices('#').count()

    fun distances(start: Point): Map<Point, Int> {
        fun n(pos: Point) = pos.directNeighbors(area).filter { p[it] != '#' }

        return Dijkstra(start, ::n).search { false }.distance
    }

    fun calcDistances(from: Direction8?): Map<Point, Int> =
        when (from) {
            null -> distances(start)
            Direction8.SOUTH -> distances(start.x to area.height - 1)
            Direction8.NORTH -> distances(start.x to 0)
            Direction8.EAST -> distances(area.width - 1 to start.y)
            Direction8.WEST -> distances(0 to start.y)
            Direction8.NORTHWEST -> distances(area.upperLeft)
            Direction8.NORTHEAST -> distances(area.upperRight)
            Direction8.SOUTHWEST -> distances(area.lowerLeft)
            Direction8.SOUTHEAST -> distances(area.lowerRight)
        }

    fun maxDepth(from: Direction8?): Int =
        calcDistances(from).values.max()

    fun reachable(from: Direction8?, limit: Int): Long =
        calcDistances(from).values.count {
            it <= limit && it % 2 == limit % 2
        }.toLong()

    override fun part1(): Long {
        val S = if (testInput) 6 else 64
        return reachable(null, S)
    }

    override fun part2(): Any? {
        val S = if (testInput) 5000 else 26501365

        alog { "Total gardens: $gardens" }
        alog { "Reachable gardens: ${distances(origin).size}" }
        alog { "Total rocks: $rocks" }

        val all = (Direction8.all + null).associateWith {
            maxDepth(it)
        }
        all.keys.forEach {
            alog {
                "$it: ${reachable(it, S)} ${reachable(it, S + 1)}  max: ${all[it]}"
            }
        }

        val width = area.width
        val half = (area.width + 1) / 2

//        logEnabled = true
//        S = 400
        val maxReach = (S + start.x) / area.width
        alog { maxReach }

        val completeRings = maxReach - 1

        val res2 =
            reachable(null, S).toBigInteger() +
                    run {
                        val blue = reachable(Direction8.WEST, S - half).toBigInteger()
                        val green = reachable(Direction8.WEST, S - half - width).toBigInteger()
                        val inner = if (maxReach % 2 == 0) {
                            (maxReach).toBigInteger() * (maxReach).toBigInteger() * blue +
                                    ((completeRings).toBigInteger() * (completeRings).toBigInteger() - BigInteger.ONE) * green
                        } else {
                            error("BOOM")
                            maxReach.toBigInteger() * maxReach.toBigInteger() * green + (completeRings + 1).toBigInteger() * (completeRings + 1).toBigInteger() * green
                        }
                        inner
                    } +
                    run {
                        val ring = maxReach
                        val straightReached = half + (ring - 1) * width

                        val leftSteps = S - straightReached
                        val rightMid = reachable(Direction8.EAST, leftSteps).toBigInteger()
                        val blue = ring.toBigInteger() *
                                reachable(Direction8.SOUTHEAST, S - straightReached - half).toBigInteger()
                        val green = (ring - 1).toBigInteger() *
                                reachable(Direction8.SOUTHEAST, S - straightReached - half + width).toBigInteger()
                        val blue2 = ring.toBigInteger() *
                                reachable(Direction8.NORTHEAST, S - straightReached - half).toBigInteger()
                        val green2 = (ring - 1).toBigInteger() *
                                reachable(Direction8.NORTHEAST, S - straightReached - half + width).toBigInteger()

                        val leftMid = reachable(Direction8.WEST, S - straightReached).toBigInteger()
                        val blue3 = ring.toBigInteger() *
                                reachable(Direction8.SOUTHWEST, S - straightReached - half).toBigInteger()
                        val green3 = (ring - 1).toBigInteger() *
                                reachable(Direction8.SOUTHWEST, S - straightReached - half + width).toBigInteger()
                        val blue4 = ring.toBigInteger() *
                                reachable(Direction8.NORTHWEST, S - straightReached - half).toBigInteger()
                        val green4 = (ring - 1).toBigInteger() *
                                reachable(Direction8.NORTHWEST, S - straightReached - half + width).toBigInteger()

                        val top = reachable(Direction8.SOUTH, S - straightReached).toBigInteger()
                        val bottom = reachable(Direction8.NORTH, S - straightReached).toBigInteger()

                        top + bottom + leftMid + rightMid + blue + green + blue2 + green2 + blue3 + green3 + blue4 + green4
                    }

        return res2
    }

}

fun main() {
    solve<Day21> {

        """
            ...........
            .....###.#.
            .###.##..#.
            ..#.#...#..
            ....#.#....
            .##..S####.
            .##..#...#.
            .......##..
            .##.#.####.
            .##..##.##.
            ...........
        """.trimIndent() part1 16 // part2 16733044

    }
}
