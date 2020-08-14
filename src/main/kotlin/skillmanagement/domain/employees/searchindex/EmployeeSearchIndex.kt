package skillmanagement.domain.employees.searchindex

import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.Operator.AND
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import skillmanagement.common.search.AbstractSearchIndex
import skillmanagement.domain.employees.model.Employee

@Component
class EmployeeSearchIndex(
    override val client: RestHighLevelClient
) : AbstractSearchIndex<Employee>() {

    override val indexName = "employees"
    override val mappingResource = ClassPathResource("/searchindex/employees-mapping.json")

    override fun toSource(instance: Employee) =
        with(instance) {
            mapOf(
                "name" to "$firstName $lastName",
                "firstName" to firstName.toString(),
                "lastName" to lastName.toString(),
                "title" to title.toString(),
                "email" to email.toString(),
                "skills" to skills.map { it.skill.label.toString() },
                "projects" to projects.map { it.project.label.toString() }
            )
        }

    override fun id(instance: Employee) = instance.id

    override fun buildQuery(queryString: String) =
        queryStringQuery(queryString)
            .defaultField("name")
            .defaultOperator(AND)

}