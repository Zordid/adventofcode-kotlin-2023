import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicTest
import kotlin.reflect.KClass

fun aocTests(builder: AoCTestBuilder.() -> Unit): List<DynamicTest> =
    AoCTestBuilder().apply(builder).build().also { verbose = false }

class AoCTestBuilder {

    private val tests = mutableListOf<DynamicTest>()

    inline fun <reified D : Day> test(expectedPart1: Any? = null, expectedPart2: Any? = null) =
        test(D::class, expectedPart1, expectedPart2)

    fun test(dayClass: KClass<out Day>, expectedPart1: Any? = null, expectedPart2: Any? = null) {
        tests += listOfNotNull(
            expectedPart1?.let {
                DynamicTest.dynamicTest("${dayClass.simpleName} - Part 1")
                { create(dayClass).part1.toString().trim() shouldBe "$expectedPart1".trim() }
            },
            expectedPart2?.let {
                DynamicTest.dynamicTest("${dayClass.simpleName} - Part 2")
                { create(dayClass).part2.toString().trim() shouldBe "$expectedPart2".trim() }
            })
    }

    fun build(): List<DynamicTest> = tests

}

