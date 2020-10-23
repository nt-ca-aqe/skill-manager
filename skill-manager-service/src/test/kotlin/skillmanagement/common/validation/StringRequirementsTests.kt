package skillmanagement.common.validation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import skillmanagement.common.validation.Validation.Companion.validate
import skillmanagement.test.UnitTest
import skillmanagement.test.stringOfLength

@UnitTest
internal class StringRequirementsTests {

    @Nested
    inner class IsNotBlank {

        @ParameterizedTest(name = "[{0}]")
        @ValueSource(strings = ["", " ", "  ", "\t", "\t\t", "\n", "\n\n", "\r"])
        fun `blank values are invalid`(example: String) {
            assertInvalid(example)
        }

        @Test
        fun `value normal characters is valid`() {
            assertValid("Hello World!")
        }

        @Test
        fun `value with leading and trailing blank characters is valid`() {
            assertValid(" hello\nworld ")
        }

        private fun assertInvalid(value: String) =
            assertThat(assertThrows<ValidationException> { assertValid(value) })
                .hasMessageEndingWith("- must not be blank!")

        private fun assertValid(value: String) =
            validate(value, "nb") { isNotBlank() }

    }

    @Nested
    inner class MatchesPattern {

        val pattern = Regex("""[A][\d]""")

        @Test
        fun `values matching the pattern are valid`() {
            (0..9).forEach { digit -> assertValid("A$digit") }
        }

        @Test
        fun `values not matching the pattern are invalid`() {
            assertInvalid("B5")
            assertInvalid("4A")
        }

        private fun assertInvalid(value: String) =
            assertThat(assertThrows<ValidationException> { assertValid(value) })
                .hasMessageEndingWith("- must match pattern: $pattern")

        private fun assertValid(value: String) =
            validate(value, "mp") { matchesPattern(pattern) }

    }

    @Nested
    inner class HasMaxLength {

        @Test
        fun `values of max length is valid`() {
            assertValid(stringOfLength(42), 42)
            assertValid(stringOfLength(10), 10)
        }

        @Test
        fun `values slightly shorter than max length is valid`() {
            assertValid(stringOfLength(4), 5)
            assertValid(stringOfLength(9), 10)
        }

        @Test
        fun `values slightly longer than max length is invalid`() {
            assertInvalid(stringOfLength(6), 5)
            assertInvalid(stringOfLength(11), 10)
        }

        private fun assertInvalid(value: String, maxLength: Int) =
            assertThat(assertThrows<ValidationException> { assertValid(value, maxLength) })
                .hasMessageEndingWith("- must not be longer than $maxLength characters, but is ${value.length} characters long!")

        private fun assertValid(value: String, maxLength: Int) =
            validate(value, "ml") { hasMaxLength(maxLength) }

    }

    @Nested
    inner class DoesNotContainAny {

        val invalidCharacters = setOf(',', '.')

        @Test
        fun `values without any blacklisted characters are valid`() {
            assertValid("Hello World!", invalidCharacters)
        }

        @Test
        fun `values with any blacklisted characters are invalid`() {
            assertInvalid("Hello, World!", invalidCharacters)
            assertInvalid("Hello World.", invalidCharacters)
        }

        private fun assertInvalid(value: String, blacklist: Collection<Char>) {
            val blacklistAsString = blacklist.joinToString(" ")
            assertThat(assertThrows<ValidationException> { assertValid(value, blacklist) })
                .hasMessageEndingWith("- contains at least one of the following illegal characters: $blacklistAsString")
        }

        private fun assertValid(value: String, blacklist: Collection<Char>) =
            validate(value, "c-bl") { doesNotContainAny(blacklist) }

    }

}
