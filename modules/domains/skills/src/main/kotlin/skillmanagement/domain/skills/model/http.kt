@file:Suppress("MatchingDeclarationName")

package skillmanagement.domain.skills.model

import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.mvc.BasicLinkBuilder
import org.springframework.hateoas.server.mvc.BasicLinkBuilder.linkToCurrentMapping
import skillmanagement.common.http.toMetaData
import skillmanagement.common.model.Page
import java.util.SortedSet

@Relation(itemRelation = "skill", collectionRelation = "skills")
internal data class SkillResource(
    val id: SkillId,
    val label: SkillLabel,
    val description: SkillDescription?,
    val tags: SortedSet<Tag>
) : RepresentationModel<SkillResource>()

internal fun SkillEntity.toResource() = SkillResource(
    id = id,
    label = label,
    description = description,
    tags = tags
).apply {
    add(linkToSkill(id).withSelfRel())
    add(linkToSkill(id).withRel("delete"))
}

internal fun Page<SkillEntity>.toResource(): PagedModel<SkillResource> =
    PagedModel.of(content.map(SkillEntity::toResource), toMetaData())
        .apply {
            add(linkToSkills(pageIndex, pageSize).withSelfRel())
            if (hasPrevious()) add(linkToSkills(pageIndex - 1, pageSize).withRel("previousPage"))
            if (hasNext()) add(linkToSkills(pageIndex + 1, pageSize).withRel("nextPage"))
        }

internal fun linkToSkills(pageIndex: Int, pageSize: Int): BasicLinkBuilder {
    val queryPart = "?page=$pageIndex&size=$pageSize"
    return linkToCurrentMapping().slash("api/skills$queryPart")
}

internal fun Page<SkillEntity>.toSearchResource(): PagedModel<SkillResource> =
    PagedModel.of(content.map(SkillEntity::toResource), toMetaData())
        .apply {
            add(linkToSkillsSearch(pageIndex, pageSize).withSelfRel())
            if (hasPrevious()) add(linkToSkillsSearch(pageIndex - 1, pageSize).withRel("previousPage"))
            if (hasNext()) add(linkToSkillsSearch(pageIndex + 1, pageSize).withRel("nextPage"))
        }

internal fun linkToSkillsSearch(pageIndex: Int, pageSize: Int): BasicLinkBuilder {
    val queryPart = "/_search?page=$pageIndex&size=$pageSize"
    return linkToCurrentMapping().slash("api/skills$queryPart")
}

internal fun linkToSkill(id: SkillId) =
    linkToCurrentMapping().slash("api/skills/$id")
