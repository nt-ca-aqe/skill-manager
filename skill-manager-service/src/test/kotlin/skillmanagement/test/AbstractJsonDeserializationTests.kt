package skillmanagement.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotlintest.shouldBe
import mu.KotlinLogging.logger
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import skillmanagement.domain.skills.model.Skill

@JsonTest
abstract class AbstractJsonDeserializationTests<T> {

    private val log = logger {}

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    abstract val deserializationExamples: List<Pair<String, T>>

    @TestFactory
    fun `instances can be de-serialized from JSON`() =
        deserializationExamples.mapIndexed { index, (example, expectedInstance) ->
            dynamicTest("Example #${index + 1}") {
                val actualInstance = objectMapper.readValue<Skill>(example)
                log.info { "Example: $example" }
                log.info { "Actual: $actualInstance" }
                log.info { "Expected: $expectedInstance" }
                actualInstance shouldBe expectedInstance
            }
        }

}
