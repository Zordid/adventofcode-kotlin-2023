class Day04 : Day(4, 2023, "Scratchcards") {

    private val cardWins = input.map { line ->
        line.substringAfter(':')
            .split('|').map(String::extractAllIntegers).let { (have, winning) ->
                have.count { it in winning }
            }
    }

    override fun part1() = cardWins.filter { it > 0 }.sumOf { wins ->
        1 shl (wins - 1)
    }

    override fun part2(): Int {
        val cardsOwned = MutableList(cardWins.size) { 1 }
        return cardsOwned.indices.sumOf { cardIdx ->
            cardsOwned[cardIdx].also {
                (cardIdx + 1..cardIdx + cardWins[cardIdx]).forEach { c ->
                    cardsOwned[c] += cardsOwned[cardIdx]
                }
            }
        }
    }

}

fun main() {
    solve<Day04> {
        """
            Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
            Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
            Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
            Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
            Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
            Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
        """.trimIndent()(13, 30)
    }
}
