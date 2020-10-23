package skillmanagement.domain.skills.model

import skillmanagement.test.UnitTest
import skillmanagement.test.contracts.string.LabelTypeContract
import skillmanagement.test.stringOfLength

@UnitTest
internal class SkillLabelTests : LabelTypeContract {

    override val maxLength: Int = 100
    override val validExamples = listOf("Kotlin", "Java", "Moderation", "Presentation", "Jenkins")

    override fun createInstance(value: String) = SkillLabel(value)
    override fun createInstanceOfLength(length: Int) = createInstance(stringOfLength(length))

}
