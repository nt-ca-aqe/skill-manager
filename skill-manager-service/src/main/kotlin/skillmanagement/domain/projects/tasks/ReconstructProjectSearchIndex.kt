package skillmanagement.domain.projects.tasks

import mu.KotlinLogging.logger
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint
import skillmanagement.common.search.AbstractReconstructSearchIndexTask
import skillmanagement.common.stereotypes.Task
import skillmanagement.domain.projects.model.Project
import skillmanagement.domain.projects.searchindex.ProjectSearchIndex
import skillmanagement.domain.projects.usecases.get.GetProjectsFromDataStore

@Task
@WebEndpoint(id = "reconstructProjectSearchIndex")
class ReconstructProjectSearchIndex(
    override val searchIndex: ProjectSearchIndex,
    private val getProjectsFromDataStore: GetProjectsFromDataStore
) : AbstractReconstructSearchIndexTask<Project>() {

    override val log = logger {}

    override fun executeForAllInstancesInDataStore(callback: (Project) -> Unit) = getProjectsFromDataStore(callback)
    override fun shortDescription(instance: Project) = "${instance.id} - ${instance.label}"

}
