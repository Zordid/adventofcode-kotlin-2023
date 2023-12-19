import utils.productOf

class Day19 : Day(19, 2023, "Aplenty") {

    private val workflows = inputAsGroups[0].map {
        val name = it.substringBefore('{')
        val rules = it.substringAfter('{').dropLast(1).split(',').map { r ->
            if (':' in r) {
                val (c, n) = r.split(':')
                val category = when (c[0]) {
                    'x' -> 0
                    'm' -> 1
                    'a' -> 2
                    's' -> 3
                    else -> error(r)
                }
                when (c[1]) {
                    '>' -> Rule.GreaterThan(category, c.extractFirstInt(), n)
                    '<' -> Rule.LessThan(category, c.extractFirstInt(), n)
                    else -> error(r)
                }
            } else
                Rule.Unconditional(r)
        }
        Workflow(name, rules)
    }.associateBy { it.name }.show()

    private val parts = inputAsGroups[1].map { it.extractAllIntegers() }.show()

    data class Workflow(val name: String, val rules: List<Rule>)

    sealed interface Rule {
        val next: String
        fun matches(part: List<Int>): Boolean
        fun split(parts: PotentialPart): Pair<PotentialPart, PotentialPart>

        data class LessThan(val category: Int, val value: Int, override val next: String) : Rule {
            override fun matches(part: List<Int>) = part[category] < value

            override fun split(parts: PotentialPart): Pair<PotentialPart, PotentialPart> {
                val relevant = parts[category]
                val (matching, notMatching) =
                    relevant?.let { range ->
                        when {
                            value in range ->
                                (range.first..<value) to (value..range.last)

                            value > range.last ->
                                range to null

                            else -> null to null
                        }
                    } ?: (null to null)
                return parts.mapIndexed { idx, r ->
                    if (idx == category) matching else r
                } to parts.mapIndexed { idx, r ->
                    if (idx == category) notMatching else r
                }
            }
        }

        data class GreaterThan(val category: Int, val value: Int, override val next: String) : Rule {
            override fun matches(part: List<Int>) = part[category] > value

            override fun split(parts: PotentialPart): Pair<PotentialPart, PotentialPart> {
                val relevant = parts[category]
                val (matching, notMatching) =
                    relevant?.let { range ->
                        when {
                            value in range ->
                                ((value + 1)..range.last) to (range.first..value)

                            value < range.first ->
                                range to null

                            else -> null to null
                        }
                    } ?: (null to null)
                return parts.mapIndexed { idx, r ->
                    if (idx == category) matching else r
                } to parts.mapIndexed { idx, r ->
                    if (idx == category) notMatching else r
                }
            }
        }

        data class Unconditional(override val next: String) : Rule {
            override fun matches(part: List<Int>) = true

            override fun split(parts: PotentialPart): Pair<PotentialPart, PotentialPart> =
                parts to listOf(null, null, null, null)
        }
    }

    override fun part1(): Int {

        fun List<Int>.isAccepted(): Boolean {
            var wf = "in"
            while (true) {
                if (wf == "R") return false
                if (wf == "A") return true
                wf = workflows[wf]!!.rules.first { r -> r.matches(this) }.next
            }
        }

        return parts.filter { it.isAccepted() }.sumOf { it.sum() }
    }

    override fun part2(): Long {
        val potentialPart = listOf(
            1..4000,
            1..4000,
            1..4000,
            1..4000,
        )

        fun countAccepted(wf: Workflow, parts: PotentialPart): Long =
            wf.rules.fold(parts to 0L) { (remaining, count), rule ->
                val (matching, notMatching) = rule.split(remaining)
                notMatching to count + when (rule.next) {
                    "A" -> matching.combinations()
                    "R" -> 0
                    else -> countAccepted(workflows[rule.next]!!, matching)
                }
            }.second

        return countAccepted(workflows["in"]!!, potentialPart)
    }

    private fun PotentialPart.combinations(): Long =
        productOf { r -> r?.let { it.last - it.first + 1 } ?: 0 }

}

typealias PotentialPart = List<IntRange?>

fun main() {
    solve<Day19> {
        """
            px{a<2006:qkq,m>2090:A,rfg}
            pv{a>1716:R,A}
            lnx{m>1548:A,A}
            rfg{s<537:gd,x>2440:R,A}
            qs{s>3448:A,lnx}
            qkq{x<1416:A,crn}
            crn{x>2662:A,R}
            in{s<1351:px,qqz}
            qqz{s>2770:qs,m<1801:hdj,R}
            gd{a>3333:R,R}
            hdj{m>838:A,pv}

            {x=787,m=2655,a=1222,s=2876}
            {x=1679,m=44,a=2067,s=496}
            {x=2036,m=264,a=79,s=2244}
            {x=2461,m=1339,a=466,s=291}
            {x=2127,m=1623,a=2188,s=1013}
        """.trimIndent() part1 19114 part2 167409079868000
    }
}
