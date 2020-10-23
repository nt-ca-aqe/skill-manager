package skillmanagement

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.MediaTypes.HAL_JSON
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import skillmanagement.domain.employees.model.EmailAddress
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.FirstName
import skillmanagement.domain.employees.model.JobTitle
import skillmanagement.domain.employees.model.LastName
import skillmanagement.domain.employees.model.TelephoneNumber
import skillmanagement.test.SmokeTest
import skillmanagement.test.SpringBootTestWithDockerizedDependencies
import kotlin.reflect.KClass

@SmokeTest
@SpringBootTestWithDockerizedDependencies
class ApplicationSmokeTests(
    @Autowired val mockMvc: MockMvc
) {

    val objectMapper = ObjectMapper()
        .apply { registerModule(KotlinModule()) }
        .apply { registerModule(JavaTimeModule()) }
        .apply { registerModule(Jackson2HalModule()) }

    @Nested
    @WithMockUser
    inner class Employees {

        @Test
        fun `add, get and delete employee`() {
            val employee = mockMvc
                .post("/api/employees") {
                    contentType = APPLICATION_JSON
                    content = """
                        {
                          "firstName": "Max",
                          "lastName": "Mustermann",
                          "title": "Managing Consultant",
                          "email": "max.musterman@beispiel-gmbh.de",
                          "telephone": "+49 (555) 123456"
                        }
                        """
                }
                .andExpect {
                    status { isCreated }
                    content { contentType(HAL_JSON) }
                }
                .extract(EmployeeResource::class)
                .also {
                    assertThat(it.id).isNotNull()
                    assertThat(it.firstName).isEqualTo(FirstName("Max"))
                    assertThat(it.lastName).isEqualTo(LastName("Mustermann"))
                    assertThat(it.title).isEqualTo(JobTitle("Managing Consultant"))
                    assertThat(it.email).isEqualTo(EmailAddress("max.musterman@beispiel-gmbh.de"))
                    assertThat(it.telephone).isEqualTo(TelephoneNumber("+49 (555) 123456"))
                    assertThat(it.lastUpdate).isNotNull()
                    assertThat(it.getLink("self")).isPresent()
                }
            val employeePath = employee.selfLinkPath()

            mockMvc
                .get(employeePath)
                .andExpect {
                    status { isOk }
                    content { contentType(HAL_JSON) }
                }
                .extract(EmployeeResource::class)
                .also { assertThat(it).isEqualTo(employee) }

            mockMvc
                .delete(employeePath)
                .andExpect {
                    status { isNoContent }
                    content { string("") }
                }

            mockMvc
                .get(employeePath)
                .andExpect {
                    status { isNoContent }
                    content { string("") }
                }
        }

    }

    private fun <T : Any> ResultActionsDsl.extract(clazz: KClass<T>): T {
        val mvcResult = andReturn()
        return objectMapper.readValue(mvcResult.response.contentAsByteArray, clazz.java)
    }

    private fun RepresentationModel<*>.selfLinkPath(): String = linkPath("self")
    private fun RepresentationModel<*>.linkPath(relation: String): String = getRequiredLink(relation).toUri().path

}
