package skillmanagement.domain.skills.usecases.read

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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import skillmanagement.common.model.PageIndex
import skillmanagement.common.model.PageSize
import skillmanagement.common.model.Pagination
import skillmanagement.common.model.emptyPage
import skillmanagement.common.model.pageOf
import skillmanagement.domain.skills.model.skill_java
import skillmanagement.domain.skills.model.skill_kotlin
import skillmanagement.test.ResetMocksAfterEachTest
import skillmanagement.test.TechnologyIntegrationTest
import skillmanagement.test.andDocument
import skillmanagement.test.fixedClock
import skillmanagement.test.strictJson
import java.time.Clock

@ResetMocksAfterEachTest
@TechnologyIntegrationTest
@WebMvcTest(GetSkillsPageRestAdapter::class)
@Import(GetSkillsPageRestAdapterTestsConfiguration::class)
@AutoConfigureRestDocs("build/generated-snippets/skills/get-page", uriPort = 80)
internal class GetSkillsPageRestAdapterTests(
    @Autowired val mockMvc: MockMvc,
    @Autowired val getSkillsPage: GetSkillsPageFunction
) {

    @Test
    fun `responds with 200 Ok and empty list if there are no Skills`() {
        every { getSkillsPage(any()) } returns emptyPage()

        mockMvc
            .get("/api/skills")
            .andExpect {
                status { isOk() }
                content {
                    contentType(HAL_JSON)
                    strictJson {
                        """
                        {
                          "_links": {
                            "self": {
                              "href": "http://localhost/api/skills?page=0&size=100"
                            }
                          },
                          "page": {
                            "size": 100,
                            "totalElements": 0,
                            "totalPages": 0,
                            "number": 0
                          }
                        }
                        """
                    }
                }
            }
            .andDocument("empty")

        verify { getSkillsPage(AllSkillsQuery(Pagination.DEFAULT)) }
    }

    @Test
    fun `responds with 200 Ok and Skills if there are any`() {
        every { getSkillsPage(any()) } returns pageOf(listOf(skill_java, skill_kotlin))

        mockMvc
            .get("/api/skills")
            .andExpect {
                status { isOk() }
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
                                "tags": [
                                  "language"
                                ],
                                "_links": {
                                  "self": {
                                    "href": "http://localhost/api/skills/f8948935-dab6-4c33-80d0-9f66ae546a7c"
                                  },
                                  "delete": {
                                    "href": "http://localhost/api/skills/f8948935-dab6-4c33-80d0-9f66ae546a7c"
                                  }
                                }
                              },
                              {
                                "id": "3f7985b9-f5f0-4662-bda9-1dcde01f5f3b",
                                "label": "Kotlin",
                                "description": "The coolest programming language.",
                                "tags": [
                                  "cool",
                                  "language"
                                ],
                                "_links": {
                                  "self": {
                                    "href": "http://localhost/api/skills/3f7985b9-f5f0-4662-bda9-1dcde01f5f3b"
                                  },
                                  "delete": {
                                    "href": "http://localhost/api/skills/3f7985b9-f5f0-4662-bda9-1dcde01f5f3b"
                                  }
                                }
                              }
                            ]
                          },
                          "_links": {
                            "self": {
                              "href": "http://localhost/api/skills?page=0&size=100"
                            }
                          },
                          "page": {
                            "size": 100,
                            "totalElements": 2,
                            "totalPages": 1,
                            "number": 0
                          }
                        }

                        """
                    }
                }
            }
            .andDocument("multiple")

        verify { getSkillsPage(AllSkillsQuery(Pagination.DEFAULT)) }
    }

    @Test
    fun `responds with 200 Ok and Skills on requested page`() {
        every { getSkillsPage(any()) } returns pageOf(listOf(skill_kotlin), 1, 1, 3)

        mockMvc
            .get("/api/skills?page=1&size=1")
            .andExpect {
                status { isOk() }
                content {
                    contentType(HAL_JSON)
                    strictJson {
                        """
                        {
                          "_embedded": {
                            "skills": [
                              {
                                "id": "3f7985b9-f5f0-4662-bda9-1dcde01f5f3b",
                                "label": "Kotlin",
                                "description": "The coolest programming language.",
                                "tags": [
                                  "cool",
                                  "language"
                                ],
                                "_links": {
                                  "self": {
                                    "href": "http://localhost/api/skills/3f7985b9-f5f0-4662-bda9-1dcde01f5f3b"
                                  },
                                  "delete": {
                                    "href": "http://localhost/api/skills/3f7985b9-f5f0-4662-bda9-1dcde01f5f3b"
                                  }
                                }
                              }
                            ]
                          },
                          "_links": {
                            "self": {
                              "href": "http://localhost/api/skills?page=1&size=1"
                            },
                            "previousPage": {
                              "href": "http://localhost/api/skills?page=0&size=1"
                            },
                            "nextPage": {
                              "href": "http://localhost/api/skills?page=2&size=1"
                            }
                          },
                          "page": {
                            "size": 1,
                            "totalElements": 3,
                            "totalPages": 3,
                            "number": 1
                          }
                        }
                        """
                    }
                }
            }
            .andDocument("multiple-pages")

        verify { getSkillsPage(AllSkillsQuery(Pagination(PageIndex(1), PageSize(1)))) }
    }

    @Test
    fun `paging data is handed over correctly`() {
        every { getSkillsPage(any()) } returns emptyPage()

        mockMvc
            .get("/api/skills?page=2&size=42")
            .andExpect { status { isOk() } }

        verify { getSkillsPage(AllSkillsQuery(Pagination(PageIndex(2), PageSize(42)))) }
    }

}

private class GetSkillsPageRestAdapterTestsConfiguration {
    @Bean
    fun findSkills(): GetSkillsPageFunction = mockk()

    @Bean
    fun clock(): Clock = fixedClock("2020-05-28T12:34:56.789Z")
}
