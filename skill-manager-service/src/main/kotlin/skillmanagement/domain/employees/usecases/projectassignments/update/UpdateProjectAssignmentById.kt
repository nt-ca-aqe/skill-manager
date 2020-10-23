package skillmanagement.domain.employees.usecases.projectassignments.update

import skillmanagement.common.stereotypes.BusinessFunction
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.model.ProjectAssignment
import skillmanagement.domain.employees.usecases.projectassignments.update.UpdateProjectAssignmentResult.EmployeeNotFound
import skillmanagement.domain.employees.usecases.projectassignments.update.UpdateProjectAssignmentResult.ProjectAssignmentNotChanged
import skillmanagement.domain.employees.usecases.projectassignments.update.UpdateProjectAssignmentResult.ProjectAssignmentNotFound
import skillmanagement.domain.employees.usecases.projectassignments.update.UpdateProjectAssignmentResult.SuccessfullyUpdatedProjectAssignment
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeById
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotChanged
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotFound
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.SuccessfullyUpdatedEmployee
import java.util.UUID

@BusinessFunction
class UpdateProjectAssignmentById(
    private val updateEmployeeById: UpdateEmployeeById
) {

    operator fun invoke(
        employeeId: UUID,
        projectAssignmentId: UUID,
        block: (ProjectAssignment) -> ProjectAssignment
    ): UpdateProjectAssignmentResult {
        var assignmentExists = false
        val updateResult = updateEmployeeById(employeeId) { employee ->
            val updatedProjects = employee.projects
                .map { projectAssignment ->
                    if (projectAssignment.id == projectAssignmentId) {
                        assignmentExists = true
                        update(projectAssignment, block)
                    } else {
                        projectAssignment
                    }
                }
            employee.copy(projects = updatedProjects)
        }

        return when (updateResult) {
            is NotUpdatedBecauseEmployeeNotFound -> EmployeeNotFound
            is NotUpdatedBecauseEmployeeNotChanged -> when (assignmentExists) {
                true -> ProjectAssignmentNotChanged(updateResult.employee)
                false -> ProjectAssignmentNotFound
            }
            is SuccessfullyUpdatedEmployee -> SuccessfullyUpdatedProjectAssignment(updateResult.employee)
        }
    }

    private fun update(
        currentAssignment: ProjectAssignment,
        block: (ProjectAssignment) -> ProjectAssignment
    ): ProjectAssignment {
        val modifiedAssignment = block(currentAssignment)
        check(currentAssignment.id == modifiedAssignment.id) { "ID must not be changed!" }
        check(currentAssignment.project == modifiedAssignment.project) { "Project must not be changed!" }
        return modifiedAssignment
    }

}

sealed class UpdateProjectAssignmentResult {
    object EmployeeNotFound : UpdateProjectAssignmentResult()
    object ProjectAssignmentNotFound : UpdateProjectAssignmentResult()
    data class ProjectAssignmentNotChanged(val employee: Employee) : UpdateProjectAssignmentResult()
    data class SuccessfullyUpdatedProjectAssignment(val employee: Employee) : UpdateProjectAssignmentResult()
}
