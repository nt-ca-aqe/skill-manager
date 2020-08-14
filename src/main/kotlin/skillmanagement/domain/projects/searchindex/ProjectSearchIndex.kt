package skillmanagement.domain.projects.searchindex

import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.Operator.AND
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import skillmanagement.common.search.AbstractSearchIndex
import skillmanagement.domain.projects.model.Project

@Component
class ProjectSearchIndex(
    override val client: RestHighLevelClient
) : AbstractSearchIndex<Project>() {

    override val indexName = "projects"
    override val mappingResource = ClassPathResource("/searchindex/projects-mapping.json")

    override fun toSource(instance: Project) =
        mapOf(
            "label" to instance.label.toString(),
            "description" to instance.description.toString()
        )

    override fun id(instance: Project) = instance.id

    override fun buildQuery(queryString: String) =
        queryStringQuery(queryString)
            .field("label", 3.0f)
            .field("description")
            .defaultOperator(AND)

}