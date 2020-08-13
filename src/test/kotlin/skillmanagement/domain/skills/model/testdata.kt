package skillmanagement.domain.skills.model

import skillmanagement.test.instant
import skillmanagement.test.uuid
import java.util.Collections.emptySortedSet

val skill_kotlin = Skill(
    id = uuid("3f7985b9-f5f0-4662-bda9-1dcde01f5f3b"),
    version = 1,
    label = SkillLabel("Kotlin"),
    tags = sortedSetOf(Tag("language"), Tag("cool")),
    lastUpdate = instant("2020-07-14T12:34:56.789Z")
)
val skill_java = Skill(
    id = uuid("f8948935-dab6-4c33-80d0-9f66ae546a7c"),
    version = 1,
    label = SkillLabel("Java"),
    tags = sortedSetOf(Tag("language")),
    lastUpdate = instant("2020-07-14T12:34:56.789Z")
)
val skill_python = Skill(
    id = uuid("6935e550-d041-418a-9070-e37431069232"),
    version = 1,
    label = SkillLabel("Python"),
    tags = emptySortedSet(),
    lastUpdate = instant("2020-07-14T12:34:56.789Z")
)

fun skill(
    id: String = uuid().toString(),
    version: Int = 1,
    label: String = "skill",
    tags: Collection<String> = emptyList(),
    lastUpdate: String = "2020-08-13T12:34:56.789Z"
) = Skill(
    id = uuid(id),
    version = version,
    label = SkillLabel(label),
    tags = tags.map(::Tag).toSortedSet(),
    lastUpdate = instant(lastUpdate)
)
