package skillmanagement.configuration

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Provides components needed to use Elasticsearch.
 *
 * @see RestHighLevelClient
 */
@Configuration
@EnableConfigurationProperties(ElasticsearchProperties::class)
class ElasticsearchConfiguration {

    @Bean(destroyMethod = "close")
    fun elasticsearchClient(properties: ElasticsearchProperties): RestHighLevelClient =
        createElasticsearchClient(properties)

}

@ConstructorBinding
@ConfigurationProperties("storage.elasticsearch")
data class ElasticsearchProperties(
    val schema: String = "http",
    val host: String = "localhost",
    val port: Int = 9200
) {
    fun toHttpHost() = HttpHost(host, port, schema)
}

internal fun createElasticsearchClient(properties: ElasticsearchProperties): RestHighLevelClient =
    RestHighLevelClient(RestClient.builder(properties.toHttpHost()))
