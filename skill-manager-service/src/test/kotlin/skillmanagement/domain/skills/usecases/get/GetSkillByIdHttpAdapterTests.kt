package skillmanagement.domain.skills.usecases.get

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.hateoas.MediaTypes.HAL_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import skillmanagement.domain.skills.model.skill_kotlin
import skillmanagement.test.TechnologyIntegrationTest
import skillmanagement.test.andDocument
import skillmanagement.test.fixedClock
import skillmanagement.test.strictJson
import skillmanagement.test.uuid
import java.time.Clock

@WithMockUser
@TechnologyIntegrationTest
@WebMvcTest(GetSkillByIdHttpAdapter::class)
@Import(TestConfiguration::class)
@AutoConfigureRestDocs("build/generated-snippets/skills/get-by-id", uriPort = 80)
internal class GetSkillByIdHttpAdapterTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val getSkillById: GetSkillById
) {

    @Test
    fun `responds with 204 No Content if Skill not found`() {
        val id = uuid()
        every { getSkillById(id) } returns null

        mockMvc
            .get("/api/skills/$id")
            .andExpect {
                status { isNoContent }
                content { string("") }
            }
            .andDocument("not-found")
    }

    @Test
    fun `responds with 200 Ok if Skill found`() {
        val id = skill_kotlin.id
        every { getSkillById(id) } returns skill_kotlin

        mockMvc
            .get("/api/skills/$id")
            .andExpect {
                status { isOk }
                content {
                    contentType(HAL_JSON)
                    strictJson {
                        """
                        {
                          "id": "3f7985b9-f5f0-4662-bda9-1dcde01f5f3b",
                          "label": "Kotlin",
                          "description": "The coolest programming language.",
                          "tags": ["cool", "language"],
                          "_links": {
                            "self": {
                              "href": "http://localhost/api/skills/3f7985b9-f5f0-4662-bda9-1dcde01f5f3b"
                            },
                            "delete": {
                              "href": "http://localhost/api/skills/3f7985b9-f5f0-4662-bda9-1dcde01f5f3b"
                            }
                          }
                        }
                        """
                    }
                }
            }
            .andDocument("found")
    }

}

private class TestConfiguration {
    @Bean
    fun getSkillById(): GetSkillById = mockk()

    @Bean
    fun clock(): Clock = fixedClock("2020-05-08T12:34:56.789Z")
}
