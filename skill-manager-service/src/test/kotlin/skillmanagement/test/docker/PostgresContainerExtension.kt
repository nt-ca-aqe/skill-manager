package skillmanagement.test.docker

import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.springframework.core.io.PathResource
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@ExtendWith(PostgresContainerExtension::class)
annotation class RunWithDockerizedPostgres

private class PostgresContainerExtension : AbstractDockerContainerExtension<PostgresContainer>() {

    override val port = 5432
    override val portProperty = "POSTGRES_PORT"
    override fun createResource() = PostgresContainer()
        .apply { addEnvFromFile(PathResource("postgres.env")) }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        parameterContext.parameter.type == PostgresContainer::class.java

}
