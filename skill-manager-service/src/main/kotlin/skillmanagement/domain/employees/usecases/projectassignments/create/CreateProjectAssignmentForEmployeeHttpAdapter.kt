package skillmanagement.domain.employees.usecases.projectassignments.create

import mu.KotlinLogging.logger
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.ProjectContribution
import skillmanagement.domain.employees.model.toResource
import skillmanagement.domain.employees.usecases.projectassignments.create.AssignProjectToEmployeeResult.EmployeeNotFound
import skillmanagement.domain.employees.usecases.projectassignments.create.AssignProjectToEmployeeResult.ProjectNotFound
import skillmanagement.domain.employees.usecases.projectassignments.create.AssignProjectToEmployeeResult.SuccessfullyCreatedProjectAssignment
import java.time.LocalDate
import java.util.UUID

@HttpAdapter
@RequestMapping("/api/employees/{employeeId}/projects")
class CreateProjectAssignmentForEmployeeHttpAdapter(
    private val createProjectAssignmentForEmployee: CreateProjectAssignmentForEmployee
) {

    private val log = logger {}

    @PostMapping
    fun post(
        @PathVariable employeeId: UUID,
        @RequestBody request: Request
    ): ResponseEntity<EmployeeResource> {
        log.info { "Assigning project [${request.projectId}] of employee [$employeeId]" }
        val result = createProjectAssignmentForEmployee(
            employeeId = employeeId,
            projectId = request.projectId,
            contribution = request.contribution,
            startDate = request.startDate,
            endDate = request.endDate
        )
        log.info { "Result: $result" }
        return when (result) {
            EmployeeNotFound, ProjectNotFound -> notFound().build()
            is SuccessfullyCreatedProjectAssignment -> ok(result.employee.toResource())
        }
    }

    data class Request(
        val projectId: UUID,
        val contribution: ProjectContribution,
        val startDate: LocalDate,
        val endDate: LocalDate?
    )

}
