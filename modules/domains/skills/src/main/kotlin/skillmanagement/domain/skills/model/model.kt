package skillmanagement.domain.skills.model

import skillmanagement.common.model.Entity
import skillmanagement.common.model.IdType
import skillmanagement.common.model.Label
import skillmanagement.common.model.StringType
import skillmanagement.common.model.Text
import skillmanagement.common.validation.hasMaxLength
import skillmanagement.common.validation.matchesPattern
import java.time.Instant
import java.util.SortedSet
import java.util.UUID

private val TAG_PATTERN = Regex("""[a-z]+([-_][a-z]+)*""")

data class Skill(
    override val id: SkillId,
    override val version: Int,
    val label: SkillLabel,
    val description: SkillDescription?,
    val tags: SortedSet<Tag>,
    override val lastUpdate: Instant
) : Entity<SkillId>

class SkillId(value: UUID) : IdType(value)
class SkillLabel(value: String) : Label(value, maxLength = 100)
class SkillDescription(value: String) : Text(value)
class Tag(value: String) : StringType(value, { hasMaxLength(50); matchesPattern(TAG_PATTERN) })

fun skillId(value: String) = SkillId(UUID.fromString(value))
