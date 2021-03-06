package skillmanagement.domain.projects.usecases.delete

import graphql.kickstart.tools.GraphQLMutationResolver
import skillmanagement.common.stereotypes.GraphQLAdapter
import skillmanagement.domain.projects.model.projectId

@GraphQLAdapter
internal class DeleteProjectByIdGraphQLAdapter(
    private val deleteProjectById: DeleteProjectByIdFunction
) : GraphQLMutationResolver {

    fun deleteProjectById(id: String): Boolean = deleteProjectById(projectId(id))

}
