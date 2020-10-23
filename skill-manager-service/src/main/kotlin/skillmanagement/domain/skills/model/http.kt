package skillmanagement.domain.skills.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.mvc.BasicLinkBuilder
import org.springframework.hateoas.server.mvc.BasicLinkBuilder.linkToCurrentMapping
import skillmanagement.common.search.Page
import skillmanagement.common.search.toMetaData
import java.util.SortedSet
import java.util.UUID

private const val RESOURCE_BASE = "api/skills"

@JsonInclude(NON_NULL)
@Relation(itemRelation = "skill", collectionRelation = "skills")
data class SkillResource(
    val id: UUID,
    val label: SkillLabel,
    val description: SkillDescription?,
    val tags: SortedSet<Tag>
) : RepresentationModel<SkillResource>()

fun Skill.toResource() = SkillResource(
    id = id,
    label = label,
    description = description,
    tags = tags
).apply {
    add(linkToSkill(id).withSelfRel())
    add(linkToSkill(id).withRel("delete"))
}

fun Page<Skill>.toAllResource(): PagedModel<SkillResource> =
    PagedModel.of(content.map(Skill::toResource), toMetaData())
        .apply {
            add(linkToSkills(pageIndex, pageSize).withSelfRel())
            if (hasPrevious()) add(linkToSkills(pageIndex - 1, pageSize).withRel("previousPage"))
            if (hasNext()) add(linkToSkills(pageIndex + 1, pageSize).withRel("nextPage"))
        }

fun linkToSkills(pageIndex: Int, pageSize: Int): BasicLinkBuilder {
    val queryPart = "?page=$pageIndex&size=$pageSize"
    return linkToCurrentMapping().slash(RESOURCE_BASE + queryPart)
}

fun Page<Skill>.toSearchResource(): PagedModel<SkillResource> =
    PagedModel.of(content.map(Skill::toResource), toMetaData())
        .apply {
            add(linkToSkillsSearch(pageIndex, pageSize).withSelfRel())
            if (hasPrevious()) add(linkToSkillsSearch(pageIndex - 1, pageSize).withRel("previousPage"))
            if (hasNext()) add(linkToSkillsSearch(pageIndex + 1, pageSize).withRel("nextPage"))
        }

fun linkToSkillsSearch(pageIndex: Int, pageSize: Int): BasicLinkBuilder {
    val queryPart = "/_search?page=$pageIndex&size=$pageSize"
    return linkToCurrentMapping().slash(RESOURCE_BASE + queryPart)
}

fun linkToSkill(id: UUID) =
    linkToCurrentMapping().slash("$RESOURCE_BASE/$id")
