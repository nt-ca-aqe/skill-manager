package skillmanagement.domain.skills.usecases.delete

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import skillmanagement.domain.skills.usecases.delete.DeleteSkillByIdResult.SuccessfullyDeleted
import skillmanagement.test.TechnologyIntegrationTest
import skillmanagement.test.andDocument
import skillmanagement.test.fixedClock
import skillmanagement.test.uuid
import java.time.Clock

@WithMockUser
@TechnologyIntegrationTest
@WebMvcTest(DeleteSkillByIdHttpAdapter::class)
@Import(TestConfiguration::class)
@AutoConfigureRestDocs("build/generated-snippets/skills/delete-by-id", uriPort = 80)
internal class DeleteSkillByIdHttpAdapterTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val deleteSkillById: DeleteSkillById
) {

    val skillId = uuid("14ae4e75-5cf6-4b30-9fc2-7037bd428584")

    @Test
    fun `well formed request leads to correct response`() {
        every { deleteSkillById(skillId) } returns SuccessfullyDeleted
        mockMvc
            .delete("/api/skills/$skillId")
            .andExpect {
                status { isNoContent }
                content { string("") }
            }
            .andDocument("deleted")

        verify { deleteSkillById(skillId) }
    }

}

private class TestConfiguration {
    @Bean
    fun deleteSkillById(): DeleteSkillById = mockk()

    @Bean
    fun clock(): Clock = fixedClock("2020-05-21T12:34:56.789Z")
}
