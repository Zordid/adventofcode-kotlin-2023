class Day02 : Day(2, 2023, "Cube Conundrum") {

    private val games = input.map { Game(it) }.show()

    override fun part1() = games.sumOf { if (it.possible) it.id else 0 }

    override fun part2() = games.sumOf { it.power }
}

data class Game(val id: Int, val sets: List<Reveal>) {

    constructor(input: String) : this(input.extractFirstInt(), input.extractReveals())
    val possible = sets.all {
        it.red <= 12 && it.green <= 13 && it.blue <= 14
    }
    private val fRed = sets.maxOf { it.red }
    private val fBlue = sets.maxOf { it.blue }
    private val fGreen = sets.maxOf { it.green }
    val power = fGreen * fRed * fBlue
}

data class Reveal(val red: Int, val green: Int, val blue: Int)

fun String.extractReveals(): List<Reveal> = substringAfter(": ").split("; ").map { allSets ->
    val sets = allSets.split(", ")
    Reveal(
        sets.singleOrNull { "red" in it }?.extractFirstInt() ?: 0,
        sets.singleOrNull { "green" in it }?.extractFirstInt() ?: 0,
        sets.singleOrNull { "blue" in it }?.extractFirstInt() ?: 0,
    )
}

fun main() {
    solve<Day02> {
        """
            Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
            Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
            Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
            Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
            Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
        """.trimIndent()(8, 2286)
    }
}