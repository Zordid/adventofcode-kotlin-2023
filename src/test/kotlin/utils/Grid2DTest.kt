package utils

import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class Grid2DTest {

    @Test
    fun `Grid2D from List of List`() {
        val l = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9),
        )
        val g = l.asGrid2D()

        g.size shouldBeExactly 9
        g.rows shouldBeExactly 3
        g.columns shouldBeExactly 3

        val doubled = g.map { it * 2 }

        println(g.toList())
        println(g.values.toList())
        println(doubled.toList())
        println(doubled.values.toList())

        g.values.sum() shouldBeExactly (1..9).sum()
        doubled.also { println(it.toList()) }.values.sum() shouldBeExactly ((1..9).sum() * 2)

        val s = g.map { it.toString() }.values.joinToString("") shouldBe "123456789"

    }

}