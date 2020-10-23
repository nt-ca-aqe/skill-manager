package skillmanagement.domain.skills.usecases.add

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.hateoas.MediaTypes.HAL_JSON
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import skillmanagement.domain.skills.model.SkillDescription
import skillmanagement.domain.skills.model.SkillLabel
import skillmanagement.domain.skills.model.Tag
import skillmanagement.domain.skills.model.skill_kotlin
import skillmanagement.domain.skills.model.skill_python
import skillmanagement.test.TechnologyIntegrationTest
import skillmanagement.test.andDocument
import skillmanagement.test.fixedClock
import skillmanagement.test.strictJson
import java.time.Clock
import java.util.Collections.emptySortedSet

@WithMockUser
@TechnologyIntegrationTest
@WebMvcTest(AddSkillHttpAdapter::class)
@Import(TestConfiguration::class)
@AutoConfigureRestDocs("build/generated-snippets/skills/add", uriPort = 80)
internal class AddSkillHttpAdapterTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val addSkill: AddSkill
) {

    @Test
    fun `well formed request leads to correct response`() {
        every { addSkill(any(), any(), any()) } returns skill_kotlin

        mockMvc
            .post("/api/skills") {
                contentType = APPLICATION_JSON
                content = """
                    { 
                      "label": "Kotlin",
                      "description": "The coolest programming language.",
                      "tags": ["language", "cool"]
                    }
                    """
            }
            .andExpect {
                status { isCreated }
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
            .andDocument("created")

        verify {
            addSkill(
                label = SkillLabel("Kotlin"),
                description = SkillDescription("The coolest programming language."),
                tags = sortedSetOf(Tag("language"), Tag("cool"))
            )
        }
    }

    @Test
    fun `tags are optional`() {
        every { addSkill(any(), any(), any()) } returns skill_python

        mockMvc
            .post("/api/skills") {
                contentType = APPLICATION_JSON
                content = """{ "label": "Python" }"""
            }
            .andExpect {
                status { isCreated }
                content {
                    contentType(HAL_JSON)
                    strictJson {
                        """
                        {
                          "id": "6935e550-d041-418a-9070-e37431069232",
                          "label": "Python",
                          "tags": [],
                          "_links": {
                            "self": {
                              "href": "http://localhost/api/skills/6935e550-d041-418a-9070-e37431069232"
                            },
                            "delete": {
                              "href": "http://localhost/api/skills/6935e550-d041-418a-9070-e37431069232"
                            }
                          }
                        }
                        """
                    }
                }
            }
            .andDocument("created")

        verify { addSkill(SkillLabel("Python"), null, emptySortedSet()) }
    }

    @Test
    fun `malformed request leads to BAD REQUEST response - illegal value`() {
        mockMvc
            .post("/api/skills") {
                contentType = APPLICATION_JSON
                content = """{ "label": "" }"""
            }
            .andExpect {
                status { isBadRequest }
                content {
                    contentType(APPLICATION_JSON)
                    strictJson {
                        """
                        {
                          "timestamp": "2020-05-08T12:34:56.789Z",
                          "status": 400,
                          "path": "/api/skills",
                          "error": "Bad Request",
                          "message": "Request body validation failed!",
                          "details": [
                            "'SkillLabel' [] - must not be blank!"
                          ]
                        }
                        """
                    }
                }
            }
            .andDocument("bad-request")
    }

}

private class TestConfiguration {
    @Bean
    fun addSkill(): AddSkill = mockk()

    @Bean
    fun clock(): Clock = fixedClock("2020-05-08T12:34:56.789Z")
}
