import utils.*
import java.math.BigInteger

class Day21 : Day(21, 2023) {

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
            Direction8.SOUTH -> distances(start.x to area.lastRow)
            Direction8.NORTH -> distances(start.x to 0)
            Direction8.EAST -> distances(area.lastCol to start.y)
            Direction8.WEST -> distances(0 to start.y)
            Direction8.NORTHWEST -> distances(area.upperLeft)
            Direction8.NORTHEAST -> distances(area.upperRight)
            Direction8.SOUTHWEST -> distances(area.lowerLeft)
            Direction8.SOUTHEAST -> distances(area.lowerRight)
        }

    fun maxDepth(from: Direction8?): Int =
        calcDistances(from).values.max()

    val maxDepth = (Direction8.all + null).associateWith {
        maxDepth(it)
    }

    val dp = mutableMapOf<Any, Int>()

    fun cachedReachable(from: Direction8, limit: Int): Int {
        val key = from to if (limit > maxDepth[from]!!) maxDepth[from]!! + limit % 2 else limit
        dp[key]?.let { return it }
        return reachable(from, limit).also { dp[key] = it }
    }

    fun reachable(from: Direction8?, limit: Int): Int =
        calcDistances(from).values.count {
            it <= limit && it % 2 == limit % 2
        }

    override fun part1(): Int {
        val S = if (area.height > 15) 64 else 6
        return reachable(null, S)
    }

    override fun part2(): Any? {
        var S = 26501365

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

    data class MiniMap(val firstReached: Int, val landing: Point, val distances: DistMap)

    data class DistMap(
        val landing: Point,
        val distances: Map<Point, Int>,
        val reachable: Map<Direction4, Pair<Point, Int>>,
        val largestDistance: Int,
        val even: Int,
        val odd: Int,
    )

    fun reachableIn(start: Point, stepsLeft: Int): Long {

        fun n(pos: Point) =
            pos.directNeighbors(area).filter { p[it] != '#' }

        val dp = mutableMapOf<Point, DistMap>()

        fun calcDist(start: Point): DistMap {
            dp[start]?.let { return it }
            val distances = Dijkstra(start, ::n).search { false }.distance
            val reachable = Direction4.all.associateWith { dir ->
                when (dir) {
                    Direction4.NORTH -> (0..area.lastCol).map { it to 0 }.minBy { distances[it]!! }
                        .let { it.x to area.lastRow to distances[it]!! + 1 }

                    Direction4.SOUTH -> (0..area.lastCol).map { it to area.lastRow }.minBy { distances[it]!! }
                        .let { it.x to 0 to distances[it]!! + 1 }

                    Direction4.EAST -> (0..area.lastRow).map { area.lastCol to it }.minBy { distances[it]!! }
                        .let { 0 to it.y to distances[it]!! + 1 }

                    Direction4.WEST -> (0..area.lastRow).map { 0 to it }.minBy { distances[it]!! }
                        .let { area.lastCol to it.y to distances[it]!! + 1 }
                }
            }

            val largestDistance = distances.values.max()
            val even = distances.values.count { it % 2 == 0 }
            val odd = distances.size - even

            return DistMap(start, distances, reachable, largestDistance, even, odd).also { dp[start] = it }
        }

        val q = minPriorityQueueOf(origin to 0)
        val dist = mutableMapOf(origin to MiniMap(0, start, calcDist(start)))
        val prev = HashMap<Point, Point>()
        while (q.isNotEmpty()) {
            //log { "${dist.size} left in queue ${q.size}" }

            val u = q.removeFirst()
            //log { "$u in ${dist[u]!!.firstReached}" }
            if (dist[u]!!.firstReached > stepsLeft)
                continue

            Direction4.forEach { d ->
                val n = u + d
                val (potentialLanding, potentialDist) = dist[u]!!.distances.reachable[d]!!
                val f = dist[u]!!.firstReached + potentialDist
                val pp = dist[n]
                if (pp == null || pp.firstReached > f) {
                    dist[n] = MiniMap(f, potentialLanding, calcDist(potentialLanding))
                    prev[n] = u
                    q.insertOrUpdate(n, f)
                }
            }
        }

        return dist.values.filter { it.firstReached <= stepsLeft }.sumOf { x ->
            val d = x.distances
            if (x.firstReached + d.largestDistance <= stepsLeft) {
                if (stepsLeft % 2 == 0) d.even else d.odd
            } else {
                d.distances.values.count { (x.firstReached + it) <= stepsLeft && (x.firstReached + it) % 2 == stepsLeft % 2 }
            }.toLong()
        }
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
        """.trimIndent() part1 16

    }
}
