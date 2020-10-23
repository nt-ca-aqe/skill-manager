package skillmanagement.domain.employees.usecases.projectassignments.update

import com.github.fge.jsonpatch.JsonPatch
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import skillmanagement.common.http.patch.ApplyPatch
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.ProjectAssignment
import skillmanagement.domain.employees.model.ProjectAssignmentChangeData
import skillmanagement.domain.employees.model.merge
import skillmanagement.domain.employees.model.toChangeData
import skillmanagement.domain.employees.model.toResource
import skillmanagement.domain.employees.usecases.projectassignments.update.UpdateProjectAssignmentResult.EmployeeNotFound
import skillmanagement.domain.employees.usecases.projectassignments.update.UpdateProjectAssignmentResult.ProjectAssignmentNotChanged
import skillmanagement.domain.employees.usecases.projectassignments.update.UpdateProjectAssignmentResult.ProjectAssignmentNotFound
import skillmanagement.domain.employees.usecases.projectassignments.update.UpdateProjectAssignmentResult.SuccessfullyUpdatedProjectAssignment
import java.util.UUID

@HttpAdapter
@RequestMapping("/api/employees/{employeeId}/projects/{assignmentId}")
class UpdateProjectAssignmentByIdHttpAdapter(
    private val updateProjectAssignmentById: UpdateProjectAssignmentById,
    private val applyPatch: ApplyPatch
) {

    @PutMapping
    fun put(
        @PathVariable employeeId: UUID,
        @PathVariable assignmentId: UUID,
        @RequestBody request: ProjectAssignmentChangeData
    ): ResponseEntity<EmployeeResource> =
        handleUpdate(employeeId, assignmentId) { it.merge(request) }

    @PatchMapping(consumes = ["application/json-patch+json"])
    fun patch(
        @PathVariable employeeId: UUID,
        @PathVariable assignmentId: UUID,
        @RequestBody patch: JsonPatch
    ): ResponseEntity<EmployeeResource> =
        handleUpdate(employeeId, assignmentId) { it.merge(applyPatch(patch, it.toChangeData())) }

    private fun handleUpdate(
        employeeId: UUID,
        assignmentId: UUID,
        block: (ProjectAssignment) -> ProjectAssignment
    ): ResponseEntity<EmployeeResource> =
        when (val result = updateProjectAssignmentById(employeeId, assignmentId, block)) {
            EmployeeNotFound, ProjectAssignmentNotFound -> notFound().build()
            is ProjectAssignmentNotChanged -> ok(result.employee.toResource())
            is SuccessfullyUpdatedProjectAssignment -> ok(result.employee.toResource())
        }

}
