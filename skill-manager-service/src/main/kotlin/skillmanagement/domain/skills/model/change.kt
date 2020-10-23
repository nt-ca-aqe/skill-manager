package skillmanagement.domain.skills.model

import java.util.SortedSet

data class SkillChangeData(
    val label: SkillLabel,
    val description: SkillDescription?,
    val tags: SortedSet<Tag>
)

fun Skill.toChangeData(): SkillChangeData =
    SkillChangeData(label = label, description = description, tags = tags)

fun Skill.merge(changes: SkillChangeData): Skill =
    copy(label = changes.label, description = changes.description, tags = changes.tags)
