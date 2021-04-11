package skillmanagement.domain.projects.usecases.read

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import skillmanagement.common.model.Page
import skillmanagement.common.model.PageIndex
import skillmanagement.common.model.PageSize
import skillmanagement.common.model.Pagination
import skillmanagement.domain.projects.model.ProjectEntity
import skillmanagement.test.ResetMocksAfterEachTest
import skillmanagement.test.UnitTest

@UnitTest
@ResetMocksAfterEachTest
internal class GetProjectsPageGraphQLAdapterTests {

    private val getProjectsPage: GetProjectsPageFunction = mockk()
    private val cut = GetProjectsPageGraphQLAdapter(getProjectsPage)

    @Test
    fun `translates and delegates retrieval to business function`() {
        val page: Page<ProjectEntity> = mockk()
        every { getProjectsPage(AllProjectsQuery(Pagination(PageIndex(3), PageSize(42)))) } returns page
        assertThat(tryToGetProjectsPage(index = 3, size = 42)).isEqualTo(page)
    }

    @Test
    fun `default values are used when necessary`() {
        val page: Page<ProjectEntity> = mockk()
        every { getProjectsPage(AllProjectsQuery(Pagination.DEFAULT)) } returns page
        assertThat(tryToGetProjectsPage()).isEqualTo(page)
    }

    private fun tryToGetProjectsPage(index: Int? = null, size: Int? = null) =
        cut.getProjectsPage(Pagination(PageIndex.of(index), PageSize.of(size)))
}
