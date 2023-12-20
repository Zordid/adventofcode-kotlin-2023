import Day20.Module.*
import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.attribute.Label
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.graph
import guru.nidi.graphviz.toGraphviz
import utils.dequeOf
import utils.lcm
import java.io.File
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class Day20 : Day(20, 2023, "Pulse Propagation") {

    private fun buildModules(start: Module, input: List<String>) = input.associate {
        val (name, b) = it.split(" -> ")
        val connectsTo = b.split(", ")
        val module = when {
            name.startsWith('%') -> FlipFlop(name.drop(1), connectsTo)
            name.startsWith('&') -> Conjunction(name.drop(1), connectsTo)
            else -> Broadcaster(name, connectsTo)
        }
        module.name to module
    }.apply {
        start.outputs = listOf(this["broadcaster"]!!)
        values.forEach { module ->
            module.outputs = module.connectsTo.map { this[it] }
            module.outputs.forEach { to ->
                to?.connectFrom(module)
            }
        }
    }

    sealed interface Module {
        val name: String
        val connectsTo: List<String>
        val inputs: Set<Module>
        var outputs: List<Module?>
        fun connectFrom(module: Module)
        fun receive(p: Pulse): List<Pulse>

        data class Button(override val name: String = "button") : Module {
            override val connectsTo: List<String>
                get() = listOf("broadcaster")
            override val inputs: Set<Module>
                get() = emptySet()
            override lateinit var outputs: List<Module?>

            override fun connectFrom(module: Module) =
                error("Nothing can connect to the button")

            override fun receive(p: Pulse): List<Pulse> =
                error("Button cannot receive a pulse")
        }

        data class FlipFlop(override val name: String, override val connectsTo: List<String>) : Module {
            override val inputs: Set<Module> get() = _inputs
            override lateinit var outputs: List<Module?>
            private val _inputs = mutableSetOf<Module>()
            var state: Boolean = false

            override fun connectFrom(module: Module) {
                _inputs += module
            }

            override fun receive(p: Pulse): List<Pulse> =
                if (p is Pulse.Low) {
                    state = !state
                    outputs.map {
                        if (state)
                            Pulse.High(this, it)
                        else
                            Pulse.Low(this, it)
                    }
                } else emptyList()

        }

        data class Conjunction(override val name: String, override val connectsTo: List<String>) : Module {
            override val inputs: Set<Module> get() = last.keys
            override lateinit var outputs: List<Module?>
            val last = mutableMapOf<Module, Int>()
            override fun connectFrom(module: Module) {
                last[module] = 0
            }

            override fun receive(p: Pulse): List<Pulse> {
                last[p.from] = if (p is Pulse.Low) 0 else 1
                return if (last.values.all { it == 1 })
                    outputs.map { Pulse.Low(this, it) }
                else
                    outputs.map { Pulse.High(this, it) }
            }
        }

        data class Broadcaster(override val name: String, override val connectsTo: List<String>) : Module {
            private val _inputs = mutableSetOf<Module>()
            override lateinit var outputs: List<Module?>
            override val inputs: Set<Module>
                get() = _inputs

            override fun connectFrom(module: Module) {
                _inputs += module
            }

            override fun receive(p: Pulse): List<Pulse> =
                if (p is Pulse.Low)
                    outputs.map { Pulse.Low(this, it) }
                else
                    outputs.map { Pulse.High(this, it) }

        }

    }

    sealed interface Pulse {
        val from: Module
        val to: Module?

        data class Low(override val from: Module, override val to: Module?) : Pulse
        data class High(override val from: Module, override val to: Module?) : Pulse
    }

    override fun part1(): Long {
        val button = Button()
        buildModules(button, input)

        var highCount = 0
        var lowCount = 0
        repeat(1000) {
            val q = dequeOf<Pulse>(Pulse.Low(button, button.outputs.single()))
            while (q.isNotEmpty()) {
                val p = q.removeFirst()
                if (p is Pulse.Low) lowCount++ else highCount++

                p.to?.let { q.addAll(it.receive(p)) }
            }
        }
        return highCount.toLong() * lowCount
    }

    fun createGraph(modules: Map<String, Module>) {
        val clusters = modules.values.filter { m -> m.inputs.size > 4 }

        fun Module.color(): Color = when (this) {
            is Conjunction -> Color.RED
            is FlipFlop -> Color.BLUE
            else -> Color.BLACK
        }

        graph(directed = true) {
            "broadcaster"[Color.GREEN]

            clusters.forEach { center ->
                graph(name = center.name, directed = true, cluster = true) {
                    graph[Label.lines(center.name)]
                    center.name[Color.RED]
                    val flipFlops = (center.inputs + center.outputs).filterIsInstance<FlipFlop>()
                    graph(directed = true, cluster = true) {
                        graph[Label.lines("FFs")]
                        flipFlops.forEach {
                            it.name[it.color()]
                        }
                    }
                }
            }

            graph(name = "result", directed = true, cluster = true) {
                modules.values.filter {
                    it is Conjunction && (it.inputs.size <= 4)
                }.forEach { x ->
                    x.name[Color.RED]
                    x.inputs.forEach {
                        it.name[it.color()]
                    }
                }
                "rx"[Color.BLACK]
            }

            modules.values.forEach { module ->
                module.connectsTo.forEach {
                    module.name[module.color()] - it
                }
            }


        }.toGraphviz().render(Format.PNG).toFile(File("day20.png"))
    }

    data class Cluster(
        val center: Conjunction,
        val flipFlops: List<FlipFlop>,
        val input: FlipFlop,
        val output: Module
    ) {
        override fun toString(): String {
            return "Counter ${center.name} with ${flipFlops.size} FFs - input is ${input.name}, output is ${output.name}"
        }
    }

    override fun part2(): Long {
        val start = Button()
        val modules = buildModules(start, input)

        val clusters = modules.values.filterIsInstance<Conjunction>().filter { m -> m.inputs.size > 4 }
            .map { center ->
                val ff = modules.values.filterIsInstance<FlipFlop>()
                    .filter { it in center.inputs || it in center.outputs }
                val all = ff + center
                val input = ff.single { it.inputs.any { it !in all } }
                val output = all.flatMap { it.outputs }.filterNotNull().single { it !in all }
                Cluster(center, ff, input, output)
            }

        val cycles = clusters.map {
            alog { it }

            var count = 0
            outer@ while (true) {
                count++
                val q = dequeOf<Pulse>(Pulse.Low(start, it.input))
                while (q.isNotEmpty()) {
                    val p = q.removeFirst()
                    if (p is Pulse.Low && p.to == it.output)
                        break@outer

                    p.to?.let {
                        q.addAll(it.receive(p))
                    }
                }
            }
            alog { "Cycles after $count button presses!" }
            count
        }

        return cycles.lcm()
    }

}

fun main() {
    solve<Day20> {
        """
            broadcaster -> a, b, c
            %a -> b
            %b -> c
            %c -> inv
            &inv -> a
        """.trimIndent() part1 32000000

        """
            broadcaster -> a
            %a -> inv, con
            &inv -> b
            %b -> con
            &con -> output
        """.trimIndent() part1 11687500
    }
}
