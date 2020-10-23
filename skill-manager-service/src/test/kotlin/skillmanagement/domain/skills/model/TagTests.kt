package skillmanagement.domain.skills.model

import skillmanagement.test.UnitTest
import skillmanagement.test.contracts.string.HasMaxLengthContract
import skillmanagement.test.contracts.string.IsJsonSerializableContract
import skillmanagement.test.contracts.string.MatchesPatternContract
import skillmanagement.test.lowerCaseLetters
import skillmanagement.test.stringOfLength

@UnitTest
internal class TagTests : HasMaxLengthContract, MatchesPatternContract, IsJsonSerializableContract {

    override val maxLength: Int = 50
    override val validExamples = listOf("language", "cool", "foo-bar", "bar_foo", "a")
    override val invalidExamples = listOf("Language", "foo bar", "äöi", "FOO", "", " ", "\n", "\t")

    override fun createInstance(value: String) = Tag(value)
    override fun createInstanceOfLength(length: Int) = createInstance(stringOfLength(length, lowerCaseLetters))

}
