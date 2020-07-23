package skillmanagement.domain.skills.metrics

import io.kotlintest.shouldBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import skillmanagement.test.ResetMocksAfterEachTest
import skillmanagement.test.UnitTest

@UnitTest
@ResetMocksAfterEachTest
internal class TotalNumberOfSkillsTests {

    val getTotalNumberOfSkillsFromDataStore: GetTotalNumberOfSkillsFromDataStore = mockk()
    val meterRegistry = SimpleMeterRegistry()
    val cut = TotalNumberOfSkills(getTotalNumberOfSkillsFromDataStore, meterRegistry)

    @BeforeEach
    fun init() = cut.init()

    @Test
    fun `gauge is instantiated with a value of 0`() {
        getCurrentGaugeValue() shouldBe 0.0
    }

    @Test
    fun `gauge is updated via an external trigger`() {
        getCurrentGaugeValue() shouldBe 0.0
        stubNumberOfSkillsInDataStore(42)
        getCurrentGaugeValue() shouldBe 0.0
        cut.updateCache()
        getCurrentGaugeValue() shouldBe 42.0
    }

    @Test
    fun `gauge value can go up and down`() {
        stubNumberOfSkillsInDataStore(42)
        cut.updateCache()
        getCurrentGaugeValue() shouldBe 42.0

        stubNumberOfSkillsInDataStore(21)
        cut.updateCache()
        getCurrentGaugeValue() shouldBe 21.0
    }

    private fun getCurrentGaugeValue(): Double {
        val gauge = meterRegistry.find("skills.total").gauge() ?: error("gauge not found")
        return gauge.value()

    }

    private fun stubNumberOfSkillsInDataStore(number: Long) {
        every { getTotalNumberOfSkillsFromDataStore() } returns number
    }

}