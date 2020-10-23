package skillmanagement.domain.projects.usecases.get

import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.projects.model.Project
import java.util.UUID

@BusinessFunction
class GetProjectById(
    private val getProjectsFromDataStore: GetProjectsFromDataStore
) {

    operator fun invoke(id: UUID): Project? {
        return getProjectsFromDataStore(id)
    }

}
