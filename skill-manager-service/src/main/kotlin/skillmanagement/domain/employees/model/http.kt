package skillmanagement.domain.employees.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import org.springframework.hateoas.PagedModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.mvc.BasicLinkBuilder
import org.springframework.hateoas.server.mvc.BasicLinkBuilder.linkToCurrentMapping
import skillmanagement.common.search.Page
import skillmanagement.common.search.toMetaData
import skillmanagement.domain.projects.model.ProjectDescription
import skillmanagement.domain.projects.model.ProjectLabel
import skillmanagement.domain.projects.model.linkToProject
import skillmanagement.domain.skills.model.SkillLabel
import skillmanagement.domain.skills.model.linkToSkill
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

private const val RESOURCE_BASE = "api/employees"

@JsonInclude(NON_NULL)
@Relation(itemRelation = "employee", collectionRelation = "employees")
data class EmployeeResource(
    val id: UUID,

    val firstName: FirstName,
    val lastName: LastName,
    val title: JobTitle,
    val email: EmailAddress,
    val telephone: TelephoneNumber,

    val description: EmployeeDescription?,
    val academicDegrees: List<AcademicDegree>,
    val certifications: List<Certification>,
    val publications: List<Publication>,
    val languages: List<LanguageProficiency>,
    val jobHistory: List<Job>,

    val skills: List<SkillKnowledgeResource>,
    val projects: List<ProjectAssignmentResource>,

    val lastUpdate: Instant
) : RepresentationModel<EmployeeResource>()

data class SkillKnowledgeResource(
    val label: SkillLabel,
    val level: SkillLevel,
    val secret: Boolean
) : RepresentationModel<SkillKnowledgeResource>()

data class ProjectAssignmentResource(
    val label: ProjectLabel,
    val description: ProjectDescription,
    val contribution: ProjectContribution,
    val startDate: LocalDate,
    val endDate: LocalDate?
) : RepresentationModel<SkillKnowledgeResource>()

fun Employee.toResource() = EmployeeResource(
    id = id,
    firstName = firstName,
    lastName = lastName,
    title = title,
    email = email,
    telephone = telephone,
    description = description,
    academicDegrees = academicDegrees,
    certifications = certifications,
    publications = publications,
    languages = languages,
    jobHistory = jobHistory,
    skills = skills.map { it.toResource(id) },
    projects = projects.map { it.toResource(id) },
    lastUpdate = lastUpdate
).apply {
    add(linkToEmployee(id).withSelfRel())
    add(linkToEmployee(id).withRel("delete"))
}

fun SkillKnowledge.toResource(employeeId: UUID) = SkillKnowledgeResource(
    label = skill.label,
    level = level,
    secret = secret
).apply {
    add(linkToSkillKnowledge(employeeId, skill.id).withSelfRel())
    add(linkToEmployee(employeeId).withRel("employee"))
    add(linkToSkill(skill.id).withRel("skill"))
}

fun ProjectAssignment.toResource(employeeId: UUID) = ProjectAssignmentResource(
    label = project.label,
    description = project.description,
    contribution = contribution,
    startDate = startDate,
    endDate = endDate
).apply {
    add(linkToProjectAssignment(employeeId, id).withSelfRel())
    add(linkToEmployee(employeeId).withRel("employee"))
    add(linkToProject(project.id).withRel("project"))
}

fun Page<Employee>.toAllResource(): PagedModel<EmployeeResource> =
    PagedModel.of(content.map(Employee::toResource), toMetaData())
        .apply {
            add(linkToEmployees(pageIndex, pageSize).withSelfRel())
            if (hasPrevious()) add(linkToEmployees(pageIndex - 1, pageSize).withRel("previousPage"))
            if (hasNext()) add(linkToEmployees(pageIndex + 1, pageSize).withRel("nextPage"))
        }

fun linkToEmployees(pageIndex: Int, pageSize: Int): BasicLinkBuilder {
    val queryPart = "?page=$pageIndex&size=$pageSize"
    return linkToCurrentMapping().slash(RESOURCE_BASE + queryPart)
}

fun Page<Employee>.toSearchResource(): PagedModel<EmployeeResource> =
    PagedModel.of(content.map(Employee::toResource), toMetaData())
        .apply {
            add(linkToEmployeesSearch(pageIndex, pageSize).withSelfRel())
            if (hasPrevious()) add(linkToEmployeesSearch(pageIndex - 1, pageSize).withRel("previousPage"))
            if (hasNext()) add(linkToEmployeesSearch(pageIndex + 1, pageSize).withRel("nextPage"))
        }

fun linkToEmployeesSearch(pageIndex: Int, pageSize: Int): BasicLinkBuilder {
    val queryPart = "/_search?page=$pageIndex&size=$pageSize"
    return linkToCurrentMapping().slash(RESOURCE_BASE + queryPart)
}

fun linkToEmployee(id: UUID) =
    linkToCurrentMapping().slash("$RESOURCE_BASE/$id")

fun linkToSkillKnowledge(employeeId: UUID, skillId: UUID) =
    linkToCurrentMapping().slash("$RESOURCE_BASE/$employeeId/skills/$skillId")

fun linkToProjectAssignment(employeeId: UUID, assignmentId: UUID) =
    linkToCurrentMapping().slash("$RESOURCE_BASE/$employeeId/projects/$assignmentId")

