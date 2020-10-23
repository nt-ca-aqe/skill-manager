package skillmanagement.domain.skills.model

import skillmanagement.common.events.Event

/**
 * Tagging interface for _all_ [Skill] related event types.
 */
interface SkillEvent : Event

data class SkillAddedEvent(val skill: Skill) : SkillEvent
data class SkillUpdatedEvent(val skill: Skill) : SkillEvent
data class SkillDeletedEvent(val skill: Skill) : SkillEvent
