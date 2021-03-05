package skillmanagement.domain.skills.model

import skillmanagement.test.UnitTest
import skillmanagement.test.contracts.string.TextTypeContract
import skillmanagement.test.stringOfLength

@UnitTest
internal class SkillDescriptionTests : TextTypeContract() {

    override val validExamples = listOf(
        "tbd",
        """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore
        magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo
        consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
        pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est
        laborum.
        """.trimIndent()
    )

    override fun createInstance(value: String) = SkillDescription(value)
    override fun createInstanceOfLength(length: Int) = createInstance(stringOfLength(length))

}
