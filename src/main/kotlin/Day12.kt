import arrow.fx.coroutines.parMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import utils.pow
import utils.splitBy

class Day12 : Day(12, 2023, "Hot Springs") {

    val p = input

    val springs = input.map { it.substringBefore(' ').toList() }.show()
    val damages = input.map { it.substringAfter(' ').extractAllIntegers() }.show()

    override fun part1(): Long = springs.zip(damages).sumOf { (s, d) -> final(s, d) }

    fun Int.p(e: Int): Long {
        return this.toDouble().pow(e).toLong()
    }


    override fun part2(): Long = runBlocking {
        withContext(Dispatchers.Default) {
            springs.zip(damages).withIndex().parMap { (idx, pa) ->
                val (s, d) = pa

                when (s.first() to s.last()) {
//                    // begin with a .
//                    '.' to '#', '.' to '?' -> arrangements2(s + '?', d).p(4) * arrangements2(s, d)
//                    // end with a .
//                    '#' to '.', '?' to '.' -> arrangements2(s, d) * arrangements2(listOf('?') + s, d).p(4)
                    else -> {
                        val sb = buildList {
                            repeat(4) {
                                addAll(s)
                                add('?')
                            }
                            addAll(s)
                        }
                        val db = buildList {
                            repeat(5) { addAll(d) }
                        }
                        final(sb, db)
                    }
                }.also {
                    println("$idx: ${s.joinToString("")} $it")
                }.toLong()
            }.sum()
        }
    }

    fun List<Char>.j() = joinToString("")

    fun final(dirty: List<Char>, d: List<Int>): Long {
        val s = dirty.dropWhile { it == '.' }.dropLastWhile { it == '.' }.toMutableList()
        val cache = mutableMapOf<Pair<List<Char>, List<Int>>, Long>()

        fun possibilities(s: List<Char>, d: List<Int>): Long {
            cache[s to d]?.let { return it }

            if (d.isEmpty()) {
                return if (s.none { it == '#' }) 1 else 0
            }

            var count = 0L
            val remainingDamaged = d.drop(1).sum()
            val remainingMinOperationalBetweenDamagedGroups = d.size - 1
            for (pos in 0..<(s.size - remainingDamaged + remainingMinOperationalBetweenDamagedGroups - d[0] + 1)) {
                val pattern = List(pos) { '.' } + List(d[0]) { '#' } + '.'

                val mismatch = s.zip(pattern)
                    .firstOrNull { (spring, poss) ->
                        spring != poss && spring != '?'
                    }

                if (mismatch == null) {
                    count += possibilities(s.drop(pattern.size), d.drop(1))
                }
            }
            cache[s to d] = count
            return count
        }

        return possibilities(s, d).also { println("Cache size: ${cache.size}") }
    }

    fun check(s: List<Char>, d: List<Int>): Boolean {
        var p = 0
        var pd = 0
        while (p < s.size) {
            while (p < s.size && s[p] == '.') p++
            var currentRun = 0
            while (p < s.size && s[p] == '#') {
                currentRun++
                p++
            }
            if (d[pd] != currentRun)
                return false
            pd++
        }
        return pd == d.size
    }

    fun a(s: List<Char>, d: List<Int>) =
        arr(s.splitBy(keepEmpty = false) { it == '.' }, d)

    fun arr(runs: List<List<Char>>, d: List<Int>): Long {
        val remainingRuns = ArrayDeque(runs)
        val dm = ArrayDeque(d)

        var comb = 0L
        while (remainingRuns.isNotEmpty()) {
            val run = remainingRuns.removeFirst()
            val required = dm.removeFirstOrNull() ?: run {
                if (run.all { it == '?' } && remainingRuns.isEmpty()) return comb else return 0
            }
            if (required > run.size) return 0

            val firstPossible = run.indices.firstOrNull { t ->
                run.getOrNull(required + t) != '#'
            } ?: return 0
            val lastPossible = run.indexOf('#').let {
                if (it == -1) run.size - required else it
            }

            (firstPossible..lastPossible).forEach { at ->
                comb += arr(listOfNotNull(run.drop(at + required).takeIf { it.isNotEmpty() }) + remainingRuns, dm)
            }
        }

        return comb
    }

    fun test(s: List<Char>, d: List<Int>, startSp: Int, startDp: Int): Pair<Int, Int>? {
        var sp = startSp
        var dp = startDp
        var currentRun = 0
        var lastDot = startSp
        while (sp <= s.lastIndex) {
            when (s[sp]) {
                '#' -> {
                    currentRun++
                    if (dp > d.lastIndex || currentRun > d[dp]) return null
                }

                '.' -> {
                    lastDot = sp
                    if (currentRun > 0) {
                        if (d[dp] == currentRun) dp++ else return null
                    }
                    currentRun = 0
                }

                else -> {
                    return lastDot to dp
                }
            }
            sp++
        }
        if (dp < d.lastIndex) return null
        if (dp == d.lastIndex && currentRun != d[dp]) return null
        return sp to dp
    }

    fun poss(s: List<Char>, n: Int): Int {
        val sp = s.indexOfFirst { it != '.' }
        TODO()
    }

    fun arrangements2(s: List<Char>, d: List<Int>, startSp: Int = 0, startDp: Int = 0): Int {
        val (sp, dp) = test(s, d, startSp, startDp) ?: return 0
        if (sp > s.lastIndex) return 1

        val first = s.indexOfFirst { it == '?' }
        if (first == -1) return 1

        val x1 = s.toMutableList().apply { this[first] = '.' }
        val x2 = s.toMutableList().apply { this[first] = '#' }
        return arrangements2(x1, d, sp, dp) + arrangements2(x2, d, sp, dp)
    }

    fun arrangements(s: List<Char>, d: List<Int>): Int {
        val q = s.indices.filter { s[it] == '?' }
        return if (q.isNotEmpty()) {
            val max = (1L shl q.size) - 1
            val test = s.toMutableList()
            (0..max).count { t ->
                var x = t
                q.forEach { index ->
                    test[index] = if (x and 1L == 1L) '.' else '#'
                    x = x shr 1
                }
                test.groups() == d
            }
        } else 0
    }

}

fun List<Char>.groups() = splitBy(keepEmpty = false) { it == '.' }.map { it.size }

fun main() {
    solve<Day12> {
        """
            ???.### 1,1,3
            .??..??...?##. 1,1,3
            ?#?#?#?#?#?#?#? 1,3,1,6
            ????.#...#... 4,1,1
            ????.######..#####. 1,6,5
            ?###???????? 3,2,1
        """.trimIndent() part1 21 part2 525152
    }
}