package skillmanagement.common.events

import io.kotlintest.matchers.beEmpty
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.Test
import skillmanagement.test.UnitTest
import kotlin.reflect.KClass

@UnitTest
internal class EventCounterTests {

    private val registry = SimpleMeterRegistry()
    private val eventCounter = EventCounter(registry)

    @Test
    fun `without any events there is no counter`() {
        getCountersWitMetricName() should beEmpty()
    }

    @Test
    fun `with events they are counted by their type`() {
        count(TestEventOne, TestEventTwo, TestEventOne)

        counterValueOf(TestEventOne::class) shouldBe 2
        counterValueOf(TestEventTwo::class) shouldBe 1
    }

    private fun count(vararg events: TestEvent) =
        events.forEach { eventCounter.increment(it::class) }

    private fun counterValueOf(type: KClass<out TestEvent>) =
        registry.find(eventCounter.name)
            .tag("type", type.simpleName!!)
            .counter()?.count()?.toInt()

    private fun getCountersWitMetricName() =
        registry.find(eventCounter.name).counters()

}
