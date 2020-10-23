package skillmanagement.domain.projects.model

import skillmanagement.common.events.Event

/**
 * Tagging interface for _all_ [Project] related event types.
 */
interface ProjectEvent : Event

data class ProjectAddedEvent(val project: Project) : ProjectEvent
data class ProjectUpdatedEvent(val project: Project) : ProjectEvent
data class ProjectDeletedEvent(val project: Project) : ProjectEvent
