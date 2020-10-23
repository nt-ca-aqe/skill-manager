package skillmanagement.test

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.ArrayDeque
import java.util.Deque
import java.util.NoSuchElementException

fun instant(value: String): Instant = Instant.parse(value)

/**
 * Creates a [Clock] which will return the same timestamps every time
 * it is invoked.
 */
fun fixedClock(timestamps: String): Clock =
    Clock.fixed(Instant.parse(timestamps), ZoneId.of("UTC"))

/**
 * Creates a [Clock] which will return a pre defined sequence of timestamps.
 *
 * If all defined timestamps are used up, an exception will be thrown.
 */
fun sequentialClock(vararg timestamps: String): Clock =
    SequentialClock(timestamps.map(Instant::parse))

private class SequentialClock(timestamps: Collection<Instant>) : Clock() {

    private val queue: Deque<Instant> = ArrayDeque(timestamps)

    override fun instant(): Instant = queue.pop()

    override fun withZone(zone: ZoneId?): Clock = error("not supported")
    override fun getZone(): ZoneId = error("not supported")

}

@UnitTest
internal class SequentialClockTests {

    @Test
    fun `empty clock will throw exception at first use`() {
        val clock = sequentialClock()
        assertThrows<NoSuchElementException> { clock.instant() }
    }

    @Test
    fun `clock will return instant instances as long as there are some defined`() {
        val clock = sequentialClock(
            "2019-11-23T12:34:56.001Z",
            "2019-11-23T12:34:56.002Z",
            "2019-11-23T12:34:56.003Z"
        )
        assertThat(clock.instant()).isEqualTo("2019-11-23T12:34:56.001Z")
        assertThat(clock.instant()).isEqualTo("2019-11-23T12:34:56.002Z")
        assertThat(clock.instant()).isEqualTo("2019-11-23T12:34:56.003Z")
        assertThrows<NoSuchElementException> { clock.instant() }
    }

}
