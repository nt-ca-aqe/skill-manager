package skillmanagement.domain.projects

import io.mockk.InternalPlatformDsl.toStr
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.hateoas.RepresentationModel
import skillmanagement.domain.projects.model.ProjectDescription
import skillmanagement.domain.projects.model.ProjectLabel
import skillmanagement.domain.projects.model.ProjectResource
import skillmanagement.domain.projects.searchindex.ProjectSearchIndex
import skillmanagement.domain.projects.usecases.delete.DeleteProjectFromDataStore
import skillmanagement.test.SmokeTest
import skillmanagement.test.SpringBootTestWithDockerizedDependencies
import java.lang.Thread.sleep

@SmokeTest
@SpringBootTestWithDockerizedDependencies
class ProjectsSmokeTests(
    @Autowired val deleteProjectsFromDataStore: DeleteProjectFromDataStore,
    @Autowired val searchIndex: ProjectSearchIndex,
    @LocalServerPort val port: Int
) {

    val projects = ProjectsTestDriver(port = port)

    @AfterEach
    fun cleanUp() {
        deleteProjectsFromDataStore()
        searchIndex.reset()
    }

    @Nested
    inner class CrudOperations {

        @Test
        fun `projects can be added`() {
            val actual = projects.add(
                label = "Building Software",
                description = "Lorem Ipsum ..."
            )

            val expected = ProjectResource(
                id = actual.id,
                label = ProjectLabel("Building Software"),
                description = ProjectDescription("Lorem Ipsum ...")
            )

            assertThat(actual).isEqualTo(expected)
            assertThat(linkNames(actual)).containsOnly("self", "delete")
        }

        @Test
        fun `projects can be got by their ID`() {
            val addedResource = projects.add(
                label = "Building Software",
                description = "Lorem Ipsum ..."
            )
            val gotResource = projects.get(addedResource.id)

            assertThat(gotResource).isEqualTo(addedResource)
            assertThat(linkNames(gotResource)).containsOnly("self", "delete")
        }

        @Test
        fun `projects can be deleted by their ID`() {
            val projectId = projects.add().id

            assertThat(projects.get(projectId)).isNotNull()
            projects.delete(projectId)
            assertThat(projects.get(projectId)).isNull()
        }

    }

    @Nested
    inner class FindingAllProjects {

        @Test
        fun `getting all projects returns a paged result`() {
            val project1 = projects.add(
                label = "Building Software for a Bank",
                description = "Lorem Ipsum #1"
            )
            val project2 = projects.add(
                label = "Building a Machine Learning Platform",
                description = "Lorem Ipsum #2"
            )

            waitUntilSearchIndexIsRefreshed()

            assertThat(getAll()).containsExactly(project1, project2)
        }

        @Test
        fun `pagination works properly`() {
            val project1 = projects.add(label = "Project #1")
            val project2 = projects.add(label = "Project #2")
            val project3 = projects.add(label = "Project #3")
            val project4 = projects.add(label = "Project #4")
            val project5 = projects.add(label = "Project #5")

            waitUntilSearchIndexIsRefreshed()

            assertThat(getAll(page = 0, size = 3))
                .containsExactly(project1, project2, project3)
            assertThat(getAll(page = 1, size = 3))
                .containsExactly(project4, project5)
        }

        private fun getAll(page: Int = 0, size: Int = 100) =
            projects.getAll(page = page, size = size).content

    }

    @Nested
    inner class SearchingForProjects {

        @Test
        fun `searching for projects returns a paged result`() {
            val swe1 = projects.add(label = "Software Engineering Project #1")
            val swe2 = projects.add(label = "Software Engineering Project #2")
            val consulting1 = projects.add(label = "Consulting Project #1")
            val consulting2 = projects.add(label = "Consulting Project #2")

            waitUntilSearchIndexIsRefreshed()

            assertThat(search("engineering")).containsOnly(swe1, swe2)
            assertThat(search("#1")).containsOnly(consulting1, swe1)
            assertThat(search("project AND #2")).containsOnly(swe2, consulting2)
        }

        @Test
        fun `pagination works properly`() {
            val project1 = projects.add(label = "Project #1")
            val project2 = projects.add(label = "Project #2")
            val project3 = projects.add(label = "Project #3")
            val project4 = projects.add(label = "Project #4")
            val project5 = projects.add(label = "Project #5")

            waitUntilSearchIndexIsRefreshed()

            val resultsFromPage1 = search(query = "project", page = 0, size = 3)
            val resultsFromPage2 = search(query = "project", page = 1, size = 3)

            assertThat(resultsFromPage1).hasSize(3)
            assertThat(resultsFromPage2).hasSize(2)
            assertThat(resultsFromPage1 + resultsFromPage2)
                .containsOnly(project1, project2, project3, project4, project5)
        }

        private fun search(query: String, page: Int = 0, size: Int = 100) =
            projects.search(query = query, page = page, size = size).content

    }

    private fun waitUntilSearchIndexIsRefreshed() {
        searchIndex.refresh()
        sleep(1_500)
    }

    fun linkNames(model: RepresentationModel<*>?) =
        model?.getLinks()?.map { it.rel.toStr() }?.toSet()

}
