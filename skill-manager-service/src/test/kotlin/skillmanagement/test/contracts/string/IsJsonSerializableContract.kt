package skillmanagement.test.contracts.string

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotlintest.shouldBe
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

private val defaultObjectMapper = jacksonObjectMapper()

interface IsJsonSerializableContract : StringTypeContract {

    @TestFactory
    fun `is serialized to JSON string value`() = validExamples
        .map { example ->
            dynamicTest(example) {
                val instance = createInstance(example)
                val expectedJsonValue = defaultObjectMapper.writeValueAsString(example)
                defaultObjectMapper.writeValueAsString(instance) shouldBe expectedJsonValue
            }
        }

    @TestFactory
    fun `can be de-serialized from JSON string value`() = validExamples
        .map { example ->
            dynamicTest(example) {
                val jsonValue = defaultObjectMapper.writeValueAsString(example)
                val expectedInstance = createInstance(example)
                defaultObjectMapper.readValue(jsonValue, expectedInstance::class.java) shouldBe expectedInstance
            }
        }

}
