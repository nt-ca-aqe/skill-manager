package skillmanagement.test

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging.logger
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest

@JsonTest
abstract class AbstractJsonSerializationTests<T> {

    private val log = logger {}

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    abstract val serializationExamples: List<Pair<T, String>>

    @TestFactory
    fun `instances can be serialized to JSON`() =
        serializationExamples.mapIndexed { index, (example, expectedJson) ->
            dynamicTest("Example #${index + 1}") {
                val actualJson = objectMapper.writeValueAsString(example)
                log.info { "Example: $example" }
                log.info { "Actual: $actualJson" }
                log.info { "Expected: $expectedJson" }
                assertEquals(expectedJson, actualJson, true)
            }
        }

}
