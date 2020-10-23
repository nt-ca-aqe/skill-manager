package skillmanagement

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import skillmanagement.domain.employees.model.EmailAddress
import skillmanagement.domain.employees.model.FirstName
import skillmanagement.domain.employees.model.LastName
import skillmanagement.domain.employees.model.ProjectContribution
import skillmanagement.domain.employees.model.SkillLevel
import skillmanagement.domain.employees.model.TelephoneNumber
import skillmanagement.domain.employees.model.JobTitle
import skillmanagement.domain.employees.usecases.add.AddEmployee
import skillmanagement.domain.employees.usecases.projectassignments.create.CreateProjectAssignmentForEmployee
import skillmanagement.domain.employees.usecases.skillknowledge.set.SetSkillKnowledgeOfEmployee
import skillmanagement.domain.projects.model.ProjectDescription
import skillmanagement.domain.projects.model.ProjectLabel
import skillmanagement.domain.projects.usecases.add.AddProject
import skillmanagement.domain.skills.model.SkillDescription
import skillmanagement.domain.skills.model.SkillLabel
import skillmanagement.domain.skills.model.Tag
import skillmanagement.domain.skills.usecases.add.AddSkill
import java.time.Clock
import java.time.LocalDate

@Component
@Profile("with-test-data")
class TestDataInserter(
    private val clock: Clock,
    private val addSkill: AddSkill,
    private val addProject: AddProject,
    private val addEmployee: AddEmployee,
    private val setSkillKnowledgeOfEmployee: SetSkillKnowledgeOfEmployee,
    private val createProjectAssignmentForEmployee: CreateProjectAssignmentForEmployee
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val kotlin = addSkill(
            label = SkillLabel("Kotlin"),
            description = SkillDescription("Lorem Ipsum .."),
            tags = sortedSetOf(Tag("language"), Tag("cool"))
        )
        val starlink = addProject(
            label = ProjectLabel("Starlink"),
            description = ProjectDescription("Lorem ipsum ..")
        )
        val maxMustermann = addEmployee(
            firstName = FirstName("Max"),
            lastName = LastName("Mustermann"),
            title = JobTitle("Managing Consultant"),
            email = EmailAddress("max.mustermann@example-gmbh.de"),
            telephone = TelephoneNumber("+49 555 123456")
        )

        setSkillKnowledgeOfEmployee(
            employeeId = maxMustermann.id,
            skillId = kotlin.id,
            level = SkillLevel(7),
            secret = false
        )
        createProjectAssignmentForEmployee(
            employeeId = maxMustermann.id,
            projectId = starlink.id,
            contribution = ProjectContribution("eu fugiat nulla pariatur. Excepteur sint occaeca"),
            startDate = LocalDate.now(clock).minusDays(180),
            endDate = LocalDate.now(clock).minusDays(10)
        )
    }

}
