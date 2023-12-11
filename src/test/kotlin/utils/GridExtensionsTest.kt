package utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe

class GridExtensionsTest : FunSpec({

    val matrix = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9))

    context("transpose") {
        test("works for non square grid") {
            val grid = Grid(3, 2) { it }

            grid.transposed() shouldBe Grid(2, 3) { it.second to it.first }
        }
    }

    test("create and work with Grid") {

        val g = matrix.asGrid()

        g.width shouldBeExactly 3
        g.height shouldBeExactly 3
        g.area shouldBe (origin to (2 to 2))

        g[origin] shouldBeExactly 1
        g[g.lastPoint] shouldBeExactly 9

        g.searchIndices(2, 5, 8).toList() shouldBe listOf(1 to 0, 1 to 1, 1 to 2)

        g.toMapGrid().size shouldBe 9
        val mapGrid = g.toMapGrid { it % 2 == 0 }
        mapGrid.size shouldBe 5

        val ng = Grid(mapGrid, 0)

        println(g.formatted())
        println(g.formatted())

        println(Grid(mapOf((2 to 2) to 9), 0).formatted())

    }


})