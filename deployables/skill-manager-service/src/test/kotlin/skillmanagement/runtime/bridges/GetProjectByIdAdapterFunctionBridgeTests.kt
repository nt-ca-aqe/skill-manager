package skillmanagement.runtime.bridges

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import skillmanagement.domain.projects.model.Project
import skillmanagement.domain.projects.model.ProjectDescription
import skillmanagement.domain.projects.model.ProjectEntity
import skillmanagement.domain.projects.model.ProjectLabel
import skillmanagement.domain.projects.model.projectId
import skillmanagement.domain.projects.usecases.read.GetProjectByIdFunction
import skillmanagement.test.ResetMocksAfterEachTest
import skillmanagement.test.UnitTest
import skillmanagement.test.instant

@UnitTest
@ResetMocksAfterEachTest
internal class GetProjectByIdAdapterFunctionBridgeTests {

    private val getProjectById: GetProjectByIdFunction = mockk()
    private val getProjectByIdAdapter = GetProjectByIdAdapterFunctionBridge(getProjectById)

    @Test
    fun `delegates to function of other module and converts the result`() {
        val project = ProjectEntity(
            id = projectId("f804d83f-466c-4eab-a58f-4b25ca1778f3"),
            version = 1,
            data = Project(
                label = ProjectLabel("Neo"),
                description = ProjectDescription("The PlayStation 4 Pro.")
            ),
            created = instant("2021-03-11T12:34:56.789Z"),
            lastUpdate = instant("2021-03-11T12:34:56.789Z")
        )
        every { getProjectById(project.id) } returns project

        getProjectByIdAdapter(projectId(project.id)) shouldBe project_data_neo
    }

    @Test
    fun `returns null if there is no project with thte given ID`() {
        every { getProjectById(any()) } returns null
        getProjectByIdAdapter(externalProjectId()) shouldBe null
    }

}
