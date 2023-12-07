val cv = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2').reversed()
val cv2 = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J').reversed()

class Day07 : Day(7, 2023) {

    enum class Type {
        FIVE, FOUR, FULL, THREE, TWO_PAIR, ONE_PAIR, HIGH
    }

    data class Hand(val cards: List<Char>) : Comparable<Hand> {
        val type: Type
            get() {
                return when {
                    cards.groupingBy { it }.eachCount().values.toSet() == setOf(5) -> Type.FIVE
                    cards.groupingBy { it }.eachCount().values.toSet() == setOf(1, 4) -> Type.FOUR
                    cards.groupingBy { it }.eachCount().values.toSet() == setOf(2, 3) -> Type.FULL
                    cards.groupingBy { it }.eachCount().values.sorted() == listOf(1, 1, 3) -> Type.THREE
                    cards.groupingBy { it }.eachCount().values.sorted() == listOf(1, 2, 2) -> Type.TWO_PAIR
                    cards.groupingBy { it }.eachCount().values.sorted() == listOf(1, 1, 1, 2) -> Type.ONE_PAIR
                    else -> Type.HIGH
                }
            }

        val highestType: Type
            get() {
                val js = cards.count { it == 'J' }
                val left = cards.filter { it != 'J' }
                val d = left.groupingBy { it }.eachCount().values.sorted()
                return when (js) {
                    5 -> Type.FIVE
                    4 -> Type.FIVE
                    3 -> when (d) {
                        listOf(2) -> Type.FIVE
                        listOf(1, 1) -> Type.FOUR
                        else -> error(toString())
                    }

                    2 -> when (d) {
                        listOf(3) -> Type.FIVE
                        listOf(1, 2) -> Type.FOUR
                        listOf(1, 1, 1) -> Type.THREE
                        else -> error(toString())
                    }

                    1 -> when (d) {
                        listOf(4) -> Type.FIVE
                        listOf(2, 2) -> Type.FULL
                        listOf(1, 3) -> Type.FOUR
                        listOf(1, 1, 2) -> Type.THREE
                        listOf(1, 1, 1, 1) -> Type.ONE_PAIR
                        else -> error("$d")
                    }

                    0 -> type
                    else -> error("$d")
                }
            }

        override fun compareTo(other: Hand): Int {
            if (this == other) return 0

            if (this.highestType.ordinal < other.highestType.ordinal) return +1
            if (this.highestType.ordinal > other.highestType.ordinal) return -1

            val my = cards.map { cv2.indexOf(it) }
            val ov = other.cards.map { cv2.indexOf(it) }
            return my.zip(ov).first { (a, b) -> a != b }.let { (m, o) ->
                m.compareTo(o)
            }
        }

        override fun toString(): String {
            return cards.joinToString("") + " type: $type"
        }
    }

    val p = input.map {
        it.split(' ').let { (hand, bid) ->
            Hand(hand.toList()) to bid.toInt()
        }
    }.show()

    override fun part2(): Any? {

        return p.sortedBy { it.first }.withIndex().also { println(it.toList().joinToString("\n")) }.map { (index, h) ->
            ((index + 1) * h.second).toLong()
        }.sum()
    }

}

fun main() {
    solve<Day07> {
        """
            32T3K 765
            T55J5 684
            KK677 28
            KTJJT 220
            QQQJA 483
        """.trimIndent()(6440, 5905)
    }
}