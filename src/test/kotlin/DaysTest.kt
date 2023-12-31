import org.junit.jupiter.api.TestFactory

class DaysTest {

    @TestFactory
    fun `AoC 2023`() = aocTests {
        test<Day01>(54916, 54728)
        test<Day02>(2283, 78669)
        test<Day03>(514969, 78915902)
        test<Day04>(20107, 8172507)
        test<Day05>(322500873, 108956227)
        test<Day06>(1108800, 36919753)
        test<Day07>(248396258, 246436046)
        test<Day08>(11309, 13740108158591)
        test<Day09>(1834108701, 993)
        test<Day10>(6947, 273)
        test<Day11>(10292708, 790194712336)
        test<Day15>(510013, 268497)
        test<Day16>(7482, 7896)
        test<Day17>(953, 1180)
        test<Day18>(74074, 112074045986829)
        test<Day19>(397061, 125657431183201)
        test<Day20>(834323022, 225386464601017)
        test<Day21>(3816, 634549784009844)
        test<Day22>(505, 71002)
    }

}
