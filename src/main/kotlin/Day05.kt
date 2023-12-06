class Day05 : Day(5, 2023) {

    val p = inputAsGroups

    val seeds = p.first().first().extractAllLongs()
    val maps = p.drop(1).map {
        Mapping(it.drop(1).map {
            it.extractAllLongs().let { (d, s, r) -> SMap(s, d, r) }
        })
    }

    override fun part1() = seeds.minOf { seed ->
        maps.fold(seed) { v, mapping -> mapping.map(v) }
    }

    override fun part2(): Long {
//        val allSeeds = seeds.chunked(2).asSequence().flatMap { (start, length) ->
//            (start..<start+length).asSequence()
//        }
//
//        return allSeeds.minOf { seed ->
//            maps.fold(seed) { v, mapping -> mapping.map(v) }
//        }

        val targets = seeds.chunked(2).map { (from, range) ->
            (from..<from + range)
        }

        return (0..Long.MAX_VALUE).first { h ->
            val seed = maps.reversed().fold(h) { v, mapping -> mapping.mapReverse(v) }
            targets.any { seed in it }
        }
    }

    class Mapping(private val components: List<SMap>) {
        fun map(v: Long): Long =
            components.firstOrNull { v in it.source..it.source + it.range }
                ?.let { it.destination + (v - it.source) }
                ?: v

        fun mapReverse(v: Long): Long =
            components.firstOrNull { v in it.destination..it.destination + it.range }
                ?.let { it.source + (v - it.destination) }
                ?: v
    }

    data class SMap(val source: Long, val destination: Long, val range: Long)

}

fun main() {
    solve<Day05> {
        """
            seeds: 79 14 55 13

            seed-to-soil map:
            50 98 2
            52 50 48

            soil-to-fertilizer map:
            0 15 37
            37 52 2
            39 0 15

            fertilizer-to-water map:
            49 53 8
            0 11 42
            42 0 7
            57 7 4

            water-to-light map:
            88 18 7
            18 25 70

            light-to-temperature map:
            45 77 23
            81 45 19
            68 64 13

            temperature-to-humidity map:
            0 69 1
            1 0 69

            humidity-to-location map:
            60 56 37
            56 93 4
        """.trimIndent()(35, 46)
    }
}
