package skillmanagement.domain.skills.searchindex

import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import skillmanagement.common.stereotypes.EventHandler
import skillmanagement.domain.skills.model.SkillAddedEvent
import skillmanagement.domain.skills.model.SkillDeletedEvent
import skillmanagement.domain.skills.model.SkillUpdatedEvent

@EventHandler
class SkillSearchIndexUpdatingEventHandler(
    private val searchIndex: SkillSearchIndex
) {

    @Async
    @EventListener
    fun handle(event: SkillAddedEvent) {
        searchIndex.index(event.skill)
    }

    @Async
    @EventListener
    fun handle(event: SkillUpdatedEvent) {
        searchIndex.index(event.skill)
    }

    @Async
    @EventListener
    fun handle(event: SkillDeletedEvent) {
        searchIndex.deleteById(event.skill.id)
    }

}
