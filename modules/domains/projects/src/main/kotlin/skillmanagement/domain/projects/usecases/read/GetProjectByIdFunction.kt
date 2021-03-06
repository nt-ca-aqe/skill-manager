package skillmanagement.domain.projects.usecases.read

import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.projects.model.ProjectEntity
import skillmanagement.domain.projects.model.ProjectId

@BusinessFunction
class GetProjectByIdFunction internal constructor(
    private val getProjectsFromDataStore: GetProjectsFromDataStoreFunction
) {

    operator fun invoke(id: ProjectId): ProjectEntity? = getProjectsFromDataStore(id)

}
