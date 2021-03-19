package skillmanagement

import org.junit.jupiter.api.Test
import org.springframework.boot.web.server.LocalServerPort
import skillmanagement.TestCredentials.actuator
import skillmanagement.test.SmokeTest
import skillmanagement.test.e2e.SpringBootTestWithDockerizedDependencies

@SmokeTest
@SpringBootTestWithDockerizedDependencies
class ApplicationSmokeTests(
    @LocalServerPort val port: Int
) {

    private val application = ApplicationTestDriver(port = port)

    @Test
    fun `info endpoint is publicly available`() {
        println(application.getInfo())
        println(application.getInfo(actuator))
    }

    @Test
    fun `health endpoint is publicly available - but only limited data is returned`() {
        println(application.getHealth())
        println(application.getHealth(actuator))
    }

    @Test
    fun `beans endpoint is not publicly available`() {
        println(application.getBeans())
        println(application.getBeans(actuator))
    }

}
