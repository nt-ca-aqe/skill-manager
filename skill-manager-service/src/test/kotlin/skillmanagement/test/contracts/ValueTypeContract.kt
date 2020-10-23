package skillmanagement.test.contracts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import skillmanagement.common.model.ValueType

interface ValueTypeContract<T : Any> {

    val validExamples: List<T>

    @BeforeEach
    fun checkEqualityPreConditions() {
        check(validExamples.size >= 2) { "at least 2 valid examples are needed" }
    }

    @Test
    fun `equality is based on the original value`() {
        assertThat(createInstance(validExamples[0])).isEqualTo(createInstance(validExamples[0]))
        assertThat(createInstance(validExamples[0])).isNotSameAs(createInstance(validExamples[0]))
        assertThat(createInstance(validExamples[0])).isNotEqualTo(createInstance(validExamples[1]))
    }

    fun createInstance(value: T): ValueType<T>

}
