package skillmanagement.domain.employees.usecases.projectassignments.delete

import mu.KotlinLogging.logger
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.toResource
import skillmanagement.domain.employees.usecases.projectassignments.delete.DeleteProjectAssignmentOfEmployeeResult.EmployeeNotFound
import skillmanagement.domain.employees.usecases.projectassignments.delete.DeleteProjectAssignmentOfEmployeeResult.ProjectAssignmentNotFound
import skillmanagement.domain.employees.usecases.projectassignments.delete.DeleteProjectAssignmentOfEmployeeResult.SuccessfullyDeletedProjectAssignment
import java.util.UUID

@HttpAdapter
@RequestMapping("/api/employees/{employeeId}/projects/{assignmentId}")
class DeleteProjectAssignmentOfEmployeeHttpAdapter(
    private val deleteProjectAssignmentOfEmployee: DeleteProjectAssignmentOfEmployee
) {

    private val log = logger {}

    @DeleteMapping
    fun delete(@PathVariable employeeId: UUID, @PathVariable assignmentId: UUID): ResponseEntity<EmployeeResource> {
        log.info { "Deleting project assignment [$assignmentId] of employee [$employeeId]" }
        val result = deleteProjectAssignmentOfEmployee(employeeId, assignmentId)
        log.info { "Result: $result" }
        return when (result) {
            EmployeeNotFound, ProjectAssignmentNotFound -> noContent().build()
            is SuccessfullyDeletedProjectAssignment -> ok(result.employee.toResource())
        }
    }

}
