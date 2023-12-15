class Day15 : Day(15, 2023, "Lens Library") {

    private val initSequence = input.single().split(',').show()

    override fun part1() = initSequence.sumOf { it.hash() }

    override fun part2(): Int {
        val boxes = List(256) { mutableListOf<Pair<String, Int>>() }

        initSequence.forEach { code ->
            val (label, focalLength) =
                if (code.endsWith("-")) code.dropLast(1) to null
                else code.split('=').let { (label, focalLength) ->
                    label to focalLength.toInt()
                }

            val box = boxes[label.hash()]
            val foundInSlot = box.indexOfFirst { (l, _) -> l == label }

            if (focalLength == null) {
                if (foundInSlot >= 0)
                    box.removeAt(foundInSlot)
            } else {
                if (foundInSlot >= 0)
                    box[foundInSlot] = label to focalLength
                else
                    box += label to focalLength
            }
        }

        return boxes.withIndex().sumOf { (boxIdx, box) ->
            (boxIdx + 1) * box.withIndex().sumOf { (slotIdx, laf) ->
                (slotIdx + 1) * laf.second
            }
        }
    }

    private fun String.hash() = fold(0) { h, c -> (h + c.code) * 17 % 256 }

}

fun main() {
    solve<Day15> {
        """rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7""".trimIndent() part1 1320 part2 145
    }
}