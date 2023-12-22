import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrNull
import utils.dim3d.Point3D
import utils.dim3d.x
import utils.dim3d.y
import utils.dim3d.z
import utils.minMax
import utils.overlaps
import utils.range
import utils.size

class Day22 : Day(22, 2023, "Sand Slabs") {

    val originalBricks = input.mapIndexed { idx, l ->
        val (f, t) = l.split('~')
        Brick(Char(idx + 'A'.code), f.extractAllIntegers().let { (x, y, z) -> Point3D(x, y, z) },
            t.extractAllIntegers().let { (x, y, z) -> Point3D(x, y, z) }).fix()
    }.show()

    data class Brick(val id: Any, val f: Point3D, val t: Point3D) {
        val xRange = listOf(f.x, t.x).minMax().range
        val yRange = listOf(f.y, t.y).minMax().range
        val zRange = listOf(f.z, t.z).minMax().range
        val lowestZ = zRange.first

        val size = xRange.size * yRange.size * zRange.size

        fun fix(): Brick =
            if (f.z > t.z) Brick(id, t, f) else this

        fun moveDownTo(z: Int) =
            Brick(id, f.copy(third = z), t.copy(third = z + (zRange.last - zRange.first)))
    }

    fun Brick.fall(others: List<Brick>): Pair<Brick, List<Brick>> {
        val relevant = others.filter {
            (xRange overlaps it.xRange && yRange overlaps it.yRange) &&
                    it.zRange.last < lowestZ
        }
        val max = relevant.maxOfOrNull { it.zRange.last }
        val supporters = if (max != null) relevant.filter { it.zRange.last == max } else emptyList()
        val newZ = (max ?: 0) + 1

        return moveDownTo(newZ) to supporters
    }

    val supportedBy = mutableMapOf<Brick, NonEmptyList<Brick>>()

    fun compact(): List<Brick> {
        val orderedBricks = originalBricks.sortedBy { it.lowestZ }
        val nB = mutableListOf<Brick>()
        orderedBricks.forEach {
            val (fallen, supporters) = it.fall(nB)
            supporters.toNonEmptyListOrNull()?.let {
                supportedBy[fallen] = it
            }
            nB += fallen
        }
        log {
            nB.joinToString("\n")
        }
        alog { "Compacted ${nB.size} bricks..." }
        return nB
    }

    val cb = compact()

    override fun part1(): Int =
        cb.count { b ->
            (cb - b).none { supportedBy[it]?.singleOrNull() == b }
        }

    override fun part2(): Int {
        val bricks = cb.toSet()

        return bricks.sumOf { target ->
            val gone = mutableSetOf(target)
            val rest = (bricks - target).toMutableSet()
            do {
                val wouldFall = rest.filter {
                    supportedBy[it]?.let { it.all { it in gone } } ?: false
                }
                gone += wouldFall
                rest -= wouldFall
            } while (wouldFall.isNotEmpty())
            log { "If you pull $target" }
            log { "these would fall: ${gone.joinToString("\n")}" }
            log {}
            gone.size - 1
        }
    }
}

fun main() {
    solve<Day22> {
        """
            1,0,1~1,2,1
            0,0,2~2,0,2
            0,2,3~2,2,3
            0,0,4~0,2,4
            2,0,5~2,2,5
            0,1,6~2,1,6
            1,1,8~1,1,9
        """.trimIndent() part1 5 part2 7
    }
}
