package skillmanagement.test.docker

import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.springframework.core.io.PathResource
import kotlin.annotation.AnnotationTarget.CLASS

@Retention
@Target(CLASS)
@ExtendWith(ElasticsearchContainerExtension::class)
annotation class RunWithDockerizedElasticsearch

private class ElasticsearchContainerExtension : AbstractDockerContainerExtension<ElasticsearchContainer>() {

    override val port = 9200
    override val portProperty = "ELASTICSEARCH_PORT"
    override fun createResource() = ElasticsearchContainer()
        .apply { addEnvFromFile(PathResource("elasticsearch.env")) }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean =
        parameterContext.parameter.type == ElasticsearchContainer::class.java

}
