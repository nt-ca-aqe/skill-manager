package skillmanagement.domain.skills.tasks

import mu.KotlinLogging.logger
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint
import skillmanagement.common.search.AbstractReconstructSearchIndexTask
import skillmanagement.common.stereotypes.Task
import skillmanagement.domain.skills.model.Skill
import skillmanagement.domain.skills.searchindex.SkillSearchIndex
import skillmanagement.domain.skills.usecases.get.GetSkillsFromDataStore

@Task
@WebEndpoint(id = "reconstructSkillSearchIndex")
class ReconstructSkillSearchIndex(
    override val searchIndex: SkillSearchIndex,
    private val getSkillsFromDataStore: GetSkillsFromDataStore
) : AbstractReconstructSearchIndexTask<Skill>() {

    override val log = logger {}

    override fun executeForAllInstancesInDataStore(callback: (Skill) -> Unit) = getSkillsFromDataStore(callback)
    override fun shortDescription(instance: Skill) = "${instance.id} - ${instance.label}"

}
