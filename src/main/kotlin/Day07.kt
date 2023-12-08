import CamelCards.Hand
import CamelCards.compareUsing

class Day07 : Day(7, 2023, "Camel Cards") {

    private val handsAndBids = input.map {
        it.split(' ').let { (hand, bid) ->
            Hand(hand.toList()) to bid.toLong()
        }
    }.show()

    override fun part1() =
        handsAndBids
            .sortedWith(compareBy(compareUsing(Hand::type, CamelCards.cardValuesPt1)) { it.first })
            .totalWinnings()

    override fun part2() =
        handsAndBids
            .sortedWith(compareBy(compareUsing(Hand::highestType, CamelCards.cardValuesPt2)) { it.first })
            .totalWinnings()

    private fun List<Pair<Hand, Long>>.totalWinnings() =
        withIndex().sumOf { (index, handAndBid) ->
            val rank = index + 1
            val (_, bid) = handAndBid
            rank * bid
        }

}

object CamelCards {

    val cardValuesPt1 = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2').reversed()
    val cardValuesPt2 = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J').reversed()

    enum class Type {
        FIVE, FOUR, FULL, THREE, TWO_PAIR, ONE_PAIR, HIGH
    }

    fun List<Char>.histogram() = groupingBy { it }.eachCount().values.sorted()

    fun List<Int>.histogramToType(): Type = when (this.joinToString("")) {
        "5" -> Type.FIVE
        "14" -> Type.FOUR
        "23" -> Type.FULL
        "113" -> Type.THREE
        "122" -> Type.TWO_PAIR
        "1112" -> Type.ONE_PAIR
        "11111" -> Type.HIGH
        else -> error("Wrong histogram: $this")
    }

    data class Hand(val cards: List<Char>) {
        val type: Type = cards.histogram().histogramToType()
        val highestType: Type = run {
            val jCount = cards.count { it == 'J' }
            if (jCount < 5) {
                val rest = cards.filter { it != 'J' }
                rest.histogram().let { it.dropLast(1) + (it.last() + jCount) }.histogramToType()
            } else Type.FIVE
        }

        override fun toString(): String {
            return cards.joinToString("") + " type: $type"
        }

    }

    fun compareUsing(typeSelector: (Hand) -> Type, values: List<Char>) = Comparator<Hand> { o1, o2 ->
        if (o1 == o2) return@Comparator 0

        if (typeSelector(o1) < typeSelector(o2)) return@Comparator +1
        if (typeSelector(o1) > typeSelector(o2)) return@Comparator -1

        o1.cards.zip(o2.cards).first { (c1, c2) -> c1 != c2 }.let { (c1, c2) ->
            values.indexOf(c1).compareTo(values.indexOf(c2))
        }

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