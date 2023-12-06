import org.junit.jupiter.api.TestFactory

class DaysTest {

    @TestFactory
    fun `AoC 2023`() = aocTests {
        test<Day01>(54916, 54728)
        test<Day02>(2283, 78669)
        test<Day03>(514969, 78915902)
        test<Day04>(20107, 8172507)
        test<Day05>(322500873, 108956227)
        test<Day06>(20107, 8172507)
    }

}
