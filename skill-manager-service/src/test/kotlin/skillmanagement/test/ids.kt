package skillmanagement.test

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.util.IdGenerator
import java.util.ArrayDeque
import java.util.Deque
import java.util.NoSuchElementException
import java.util.UUID
import java.util.UUID.fromString

fun uuid(): UUID = UUID.randomUUID()
fun uuid(value: String): UUID = UUID.fromString(value)

/**
 * Creates an [IdGenerator] which will return a pre defined sequence of IDs.
 *
 * If all defined IDs are used up, an exception will be thrown.
 */
fun sequentialIdGenerator(vararg ids: String): IdGenerator =
    SequentialIdGenerator(ids.map(UUID::fromString))

private class SequentialIdGenerator(ids: Collection<UUID>) : IdGenerator {
    private val queue: Deque<UUID> = ArrayDeque(ids)
    override fun generateId(): UUID = queue.pop()
}

@UnitTest
internal class SequentialIdGeneratorTests {

    @Test
    fun `empty ID generator will throw exception at first use`() {
        val generator = sequentialIdGenerator()
        assertThrows<NoSuchElementException> { generator.generateId() }
    }

    @Test
    fun `clock will return instant instances as long as there are some defined`() {
        val generator = sequentialIdGenerator(
            "6b51ac39-55b9-4a13-8b42-c71da567af16",
            "bb75d8c2-de98-46c2-b1a7-c00a32438453",
            "6cdee3f7-8f9a-4b81-a1b8-06a5b2efcd2e"
        )
        assertThat(generator.generateId()).isEqualTo(fromString("6b51ac39-55b9-4a13-8b42-c71da567af16"))
        assertThat(generator.generateId()).isEqualTo(fromString("bb75d8c2-de98-46c2-b1a7-c00a32438453"))
        assertThat(generator.generateId()).isEqualTo(fromString("6cdee3f7-8f9a-4b81-a1b8-06a5b2efcd2e"))
        assertThrows<NoSuchElementException> { generator.generateId() }
    }

}
