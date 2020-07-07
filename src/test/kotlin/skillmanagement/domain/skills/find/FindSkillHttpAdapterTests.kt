package skillmanagement.domain.skills.find

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
import skillmanagement.domain.skills.skill_java
import skillmanagement.domain.skills.skill_kotlin
import skillmanagement.test.TechnologyIntegrationTest
import skillmanagement.test.andDocument
import skillmanagement.test.fixedClock
import skillmanagement.test.strictJson
import java.time.Clock

@WithMockUser
@TechnologyIntegrationTest
@WebMvcTest(FindSkillHttpAdapter::class)
@Import(TestConfiguration::class)
@AutoConfigureRestDocs("build/generated-snippets/skills/find", uriPort = 80)
internal class FindSkillHttpAdapterTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val findSkills: FindSkills
) {

    @Test
    fun `responds with 200 Ok and empty list if there are no Skills`() {
        every { findSkills() } returns emptyList()

        mockMvc
            .get("/api/skills")
            .andExpect {
                status { isOk }
                content {
                    contentType(HAL_JSON)
                    strictJson {
                        """
                        {
                          "_links": {
                            "self": {
                              "href": "http://localhost/api/skills"
                            }
                          }
                        }
                        """
                    }
                }
            }
            .andDocument("empty")
    }

    @Test
    fun `responds with 200 Ok and Skills if there are any`() {
        every { findSkills() } returns listOf(skill_java, skill_kotlin)

        mockMvc
            .get("/api/skills")
            .andExpect {
                status { isOk }
                content {
                    contentType(HAL_JSON)
                    strictJson {
                        """
                        {
                          "_embedded": {
                            "skills": [
                              {
                                "id": "f8948935-dab6-4c33-80d0-9f66ae546a7c",
                                "label": "Java",
                                "_links": {
                                  "self": {
                                    "href": "http://localhost/api/skills/f8948935-dab6-4c33-80d0-9f66ae546a7c"
                                  }
                                }
                              },
                              {
                                "id": "3f7985b9-f5f0-4662-bda9-1dcde01f5f3b",
                                "label": "Kotlin",
                                "_links": {
                                  "self": {
                                    "href": "http://localhost/api/skills/3f7985b9-f5f0-4662-bda9-1dcde01f5f3b"
                                  }
                                }
                              }
                            ]
                          },
                          "_links": {
                            "self": {
                              "href": "http://localhost/api/skills"
                            }
                          }
                        }
                        """
                    }
                }
            }
            .andDocument("multiple")
    }

}

private class TestConfiguration {
    @Bean
    fun findSkills(): FindSkills = mockk()

    @Bean
    fun clock(): Clock = fixedClock("2020-05-28T12:34:56.789Z")
}