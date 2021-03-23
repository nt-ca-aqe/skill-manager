package skillmanagement.domain.skills.usecases.read

import graphql.kickstart.tools.GraphQLQueryResolver
import skillmanagement.common.stereotypes.GraphQLAdapter
import skillmanagement.domain.skills.model.Skill
import java.util.UUID

@GraphQLAdapter
internal class GetSkillByIdGraphQLAdapter(
    private val getSkillById: GetSkillByIdFunction
) : GraphQLQueryResolver {

    fun getSkillById(id: String): Skill? = getSkillById(UUID.fromString(id))

}
