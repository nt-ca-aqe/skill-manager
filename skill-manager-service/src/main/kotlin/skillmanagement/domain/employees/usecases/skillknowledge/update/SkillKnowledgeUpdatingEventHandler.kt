package skillmanagement.domain.employees.usecases.skillknowledge.update

import mu.KotlinLogging.logger
import org.springframework.context.event.EventListener
import skillmanagement.common.search.PageSize
import skillmanagement.common.stereotypes.EventHandler
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.usecases.find.EmployeesWithSkill
import skillmanagement.domain.employees.usecases.find.FindEmployeeIds
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeById
import skillmanagement.domain.skills.model.Skill
import skillmanagement.domain.skills.model.SkillUpdatedEvent

@EventHandler
class SkillKnowledgeUpdatingEventHandler(
    private val findEmployeeIds: FindEmployeeIds,
    private val updateEmployeeById: UpdateEmployeeById
) {

    private val log = logger {}

    // TODO: how to update more than one page? (ES eventual consistency)

    @EventListener
    fun handle(event: SkillUpdatedEvent) {
        log.info { "Handling $event" }
        val skillId = event.skill.id
        findEmployeeIds(EmployeesWithSkill(skillId = skillId, pageSize = PageSize.MAX))
            .onEach { log.info { "Updating knowledge of skill [$skillId] of employee [${it}]" } }
            .forEach { employeeId ->
                updateEmployeeById(employeeId) { it.updateSkillKnowledgeOfSkill(event.skill) }
            }
    }

    private fun Employee.updateSkillKnowledgeOfSkill(skill: Skill): Employee =
        copy(skills = skills.map {
            when (it.skill.id) {
                skill.id -> it.copy(skill = skill)
                else -> it
            }
        })

}
