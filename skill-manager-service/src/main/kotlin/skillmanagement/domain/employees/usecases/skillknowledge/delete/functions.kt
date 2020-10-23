package skillmanagement.domain.employees.usecases.skillknowledge.delete

import skillmanagement.domain.employees.model.Employee
import java.util.UUID

internal fun Employee.removeSkillKnowledgeBySkillId(skillId: UUID): Employee =
    copy(skills = skills.filter { it.skill.id != skillId })
