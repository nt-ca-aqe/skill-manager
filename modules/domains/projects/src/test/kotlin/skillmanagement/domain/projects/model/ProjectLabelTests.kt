package skillmanagement.domain.projects.model

import skillmanagement.test.UnitTest
import skillmanagement.test.contracts.string.LabelTypeContract
import skillmanagement.test.stringOfLength

@UnitTest
internal class ProjectLabelTests : LabelTypeContract() {

    override val validExamples = listOf(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod",
        "Starlink Satellite",
        "Sending the first human to the moon's surface and getting them back to earth."
    )

    override fun createInstance(value: String) = ProjectLabel(value)
    override fun createInstanceOfLength(length: Int) = createInstance(stringOfLength(length))

}

