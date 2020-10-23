package skillmanagement.common.validation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import skillmanagement.common.validation.Validation.Companion.validate
import skillmanagement.test.UnitTest

@UnitTest
internal class IntRequirementsTests {

    @Nested
    inner class Between {

        @Test
        fun `positive numbers are handled correctly`() {
            val boundaries = 41..43
            assertInvalid(40, boundaries)
            assertValid(41, boundaries)
            assertValid(42, boundaries)
            assertValid(43, boundaries)
            assertInvalid(44, boundaries)
        }

        @Test
        fun `negative numbers are handled correctly`() {
            val boundaries = -43..-41
            assertInvalid(-40, boundaries)
            assertValid(-41, boundaries)
            assertValid(-42, boundaries)
            assertValid(-43, boundaries)
            assertInvalid(-44, boundaries)
        }

        private fun assertInvalid(value: Int, boundaries: IntRange) =
            assertThat(assertThrows<ValidationException> { assertValid(value, boundaries) })
                .hasMessageEndingWith("- must be between ${boundaries.first} and ${boundaries.last}")

        private fun assertValid(value: Int, boundaries: IntRange) =
            validate(value, "b") { isBetween(boundaries) }

    }

    @Nested
    inner class GreaterThanOrEqualTo {

        @Test
        fun `positive numbers are handled correctly`() {
            assertValid(43, 42)
            assertValid(42, 42)
            assertInvalid(41, 42)
        }

        @Test
        fun `negative numbers are handled correctly`() {
            assertValid(-41, -42)
            assertValid(-42, -42)
            assertInvalid(-43, -42)
        }

        private fun assertInvalid(value: Int, boundary: Int) =
            assertThat(assertThrows<ValidationException> { assertValid(value, boundary) })
                .hasMessageEndingWith("- must be greater than or equal to $boundary!")

        private fun assertValid(value: Int, boundary: Int) =
            validate(value, "gt|e") { isGreaterThanOrEqualTo(boundary) }

    }

    @Nested
    inner class LessThanOrEqualTo {

        @Test
        fun `positive numbers are handled correctly`() {
            assertValid(41, 42)
            assertValid(42, 42)
            assertInvalid(43, 42)
        }

        @Test
        fun `negative numbers are handled correctly`() {
            assertValid(-43, -42)
            assertValid(-42, -42)
            assertInvalid(-41, -42)
        }

        private fun assertInvalid(value: Int, boundary: Int) =
            assertThat(assertThrows<ValidationException> { assertValid(value, boundary) })
                .hasMessageEndingWith("- must be less than or equal to $boundary!")

        private fun assertValid(value: Int, boundary: Int) =
            validate(value, "lt|e") { isLessThanOrEqualTo(boundary) }

    }

}
