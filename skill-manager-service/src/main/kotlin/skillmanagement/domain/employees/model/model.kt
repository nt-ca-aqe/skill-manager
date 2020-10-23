package skillmanagement.domain.employees.model

import skillmanagement.common.model.IntType
import skillmanagement.common.model.Name
import skillmanagement.common.model.StringType
import skillmanagement.common.model.Text
import skillmanagement.domain.projects.model.Project
import skillmanagement.domain.skills.model.Skill
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

data class Employee(
    val id: UUID,
    val version: Int,

    val firstName: FirstName,
    val lastName: LastName,
    val title: JobTitle,
    val email: EmailAddress,
    val telephone: TelephoneNumber,

    val description: EmployeeDescription? = null,
    val academicDegrees: List<AcademicDegree> = emptyList(),
    val certifications: List<Certification> = emptyList(),
    val publications: List<Publication> = emptyList(),
    val languages: List<LanguageProficiency> = emptyList(),
    val jobHistory: List<Job> = emptyList(),

    val skills: List<SkillKnowledge> = emptyList(),
    val projects: List<ProjectAssignment> = emptyList(),

    val lastUpdate: Instant
)

class FirstName(value: String) : Name(value)
class LastName(value: String) : Name(value)
class EmployeeDescription(value: String) : Text(value)
class JobTitle(value: String) : StringType(value)
class EmailAddress(value: String) : StringType(value)
class TelephoneNumber(value: String) : StringType(value)
class Certification(value: String) : StringType(value)
class Publication(value: String) : StringType(value)

data class LanguageProficiency(
    val language: Language,
    val qualifier: LanguageQualifier
)

class Language(value: String) : StringType(value)
class LanguageQualifier(value: String) : StringType(value)

data class AcademicDegree(
    val subject: AcademicSubject,
    val title: AcademicTitle,
    val institution: AcademicInstitution
)

class AcademicSubject(value: String) : StringType(value)
class AcademicTitle(value: String) : StringType(value)
class AcademicInstitution(value: String) : StringType(value)

data class Job(
    val employer: Employer,
    val title: JobTitle,
    val start: YearMonth,
    val end: YearMonth? = null
)

class Employer(value: String) : StringType(value)

data class SkillKnowledge(
    val skill: Skill,
    val level: SkillLevel,
    val secret: Boolean
)

class SkillLevel(value: Int) : IntType(value, min = 1, max = 10)

data class ProjectAssignment(
    val id: UUID,
    val project: Project,
    val contribution: ProjectContribution,
    val startDate: LocalDate,
    val endDate: LocalDate?
)

class ProjectContribution(value: String) : Text(value)
