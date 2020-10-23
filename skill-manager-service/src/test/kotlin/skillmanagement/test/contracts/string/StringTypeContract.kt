package skillmanagement.test.contracts.string

import io.kotlintest.shouldBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import skillmanagement.test.contracts.ValueTypeContract

interface StringTypeContract : ValueTypeContract<String> {

    @TestFactory
    fun `examples of valid values`(): List<DynamicTest> =
        validExamples.map { value ->
            dynamicTest(value) { createInstance(value) }
        }

    @Test
    fun `toString returns the original value`() {
        val value = validExamples.first()
        val instance = createInstance(value)

        instance.toString() shouldBe value
    }

}
