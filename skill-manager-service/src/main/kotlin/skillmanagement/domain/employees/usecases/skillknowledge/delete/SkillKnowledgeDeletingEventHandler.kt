package skillmanagement.domain.employees.usecases.skillknowledge.delete

import mu.KotlinLogging.logger
import org.springframework.context.event.EventListener
import skillmanagement.common.search.PageSize
import skillmanagement.common.stereotypes.EventHandler
import skillmanagement.domain.employees.usecases.find.EmployeesWithSkill
import skillmanagement.domain.employees.usecases.find.FindEmployeeIds
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeById
import skillmanagement.domain.skills.model.SkillDeletedEvent

@EventHandler
class SkillKnowledgeDeletingEventHandler(
    private val findEmployeeIds: FindEmployeeIds,
    private val updateEmployeeById: UpdateEmployeeById
) {

    private val log = logger {}

    // TODO: how to update more than one page? (ES eventual consistency)

    @EventListener
    fun handle(event: SkillDeletedEvent) {
        log.info { "Handling $event" }
        val skillId = event.skill.id
        findEmployeeIds(EmployeesWithSkill(skillId = skillId, pageSize = PageSize.MAX))
            .onEach { log.info { "Removing knowledge of skill [$skillId] from employee [${it}]" } }
            .forEach { employeeId ->
                updateEmployeeById(employeeId) { it.removeSkillKnowledgeBySkillId(skillId) }
            }
    }

}
