package skillmanagement.test.contracts.string

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import skillmanagement.common.validation.ValidationException

/**
 *
 */
interface HasMaxLengthContract : StringTypeContract {

    val maxLength: Int

    @Test
    fun `instances with max-length values can be initialized`() {
        createInstanceOfLength(maxLength)
    }

    @Test
    fun `instances with greater than max-length values cannot be initialized`() {
        assertThrows<ValidationException> { createInstanceOfLength(maxLength + 1) }
    }

    fun createInstanceOfLength(length: Int): Any

}
