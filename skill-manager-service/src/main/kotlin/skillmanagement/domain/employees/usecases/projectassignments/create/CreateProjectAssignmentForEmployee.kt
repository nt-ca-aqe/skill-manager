package skillmanagement.domain.employees.usecases.projectassignments.create

import org.springframework.util.IdGenerator
import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.model.ProjectAssignment
import skillmanagement.domain.employees.model.ProjectContribution
import skillmanagement.domain.employees.usecases.projectassignments.create.AssignProjectToEmployeeResult.EmployeeNotFound
import skillmanagement.domain.employees.usecases.projectassignments.create.AssignProjectToEmployeeResult.ProjectNotFound
import skillmanagement.domain.employees.usecases.projectassignments.create.AssignProjectToEmployeeResult.SuccessfullyCreatedProjectAssignment
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeById
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotChanged
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotFound
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.SuccessfullyUpdatedEmployee
import skillmanagement.domain.projects.usecases.get.GetProjectById
import java.time.LocalDate
import java.util.UUID

@BusinessFunction
class CreateProjectAssignmentForEmployee(
    private val idGenerator: IdGenerator,
    private val getProjectById: GetProjectById,
    private val updateEmployeeById: UpdateEmployeeById
) {

    // TODO: Security - Only invokable by Employee themselves or Employee-Admins
    operator fun invoke(
        employeeId: UUID,
        projectId: UUID,
        contribution: ProjectContribution,
        startDate: LocalDate,
        endDate: LocalDate?
    ): AssignProjectToEmployeeResult {
        val project = getProjectById(projectId) ?: return ProjectNotFound
        val updateResult = updateEmployeeById(employeeId) {
            val assignment = ProjectAssignment(
                id = idGenerator.generateId(),
                project = project,
                contribution = contribution,
                startDate = startDate,
                endDate = endDate
            )
            it.addProjectAssignment(assignment)
        }

        return when (updateResult) {
            is NotUpdatedBecauseEmployeeNotFound -> EmployeeNotFound
            is NotUpdatedBecauseEmployeeNotChanged -> error("should not happen")
            is SuccessfullyUpdatedEmployee -> SuccessfullyCreatedProjectAssignment(updateResult.employee)
        }
    }

    private fun Employee.addProjectAssignment(projectAssignment: ProjectAssignment): Employee =
        copy(projects = projects + projectAssignment)

}

sealed class AssignProjectToEmployeeResult {
    object EmployeeNotFound : AssignProjectToEmployeeResult()
    object ProjectNotFound : AssignProjectToEmployeeResult()
    data class SuccessfullyCreatedProjectAssignment(val employee: Employee) : AssignProjectToEmployeeResult()
}
