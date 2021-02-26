package skillmanagement.domain.employees.model

import skillmanagement.common.events.Event

/**
 * Tagging interface for _all_ [Employee] related event types.
 */
interface EmployeeEvent : Event

data class EmployeeAddedEvent(val employee: Employee) : EmployeeEvent
data class EmployeeUpdatedEvent(val employee: Employee) : EmployeeEvent
data class EmployeeDeletedEvent(val employee: Employee) : EmployeeEvent

data class SkillUpdatedEvent(val skill: SkillData) : EmployeeEvent
data class SkillDeletedEvent(val skill: SkillData) : EmployeeEvent

data class ProjectUpdatedEvent(val project: ProjectData) : EmployeeEvent
data class ProjectDeletedEvent(val project: ProjectData) : EmployeeEvent
