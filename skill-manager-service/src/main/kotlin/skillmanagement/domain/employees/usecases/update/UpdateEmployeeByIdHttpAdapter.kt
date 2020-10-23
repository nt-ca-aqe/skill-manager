package skillmanagement.domain.employees.usecases.update

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
import skillmanagement.domain.employees.model.EmployeeChangeData
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.merge
import skillmanagement.domain.employees.model.toChangeData
import skillmanagement.domain.employees.model.toResource
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotChanged
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.NotUpdatedBecauseEmployeeNotFound
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeByIdResult.SuccessfullyUpdatedEmployee
import java.util.UUID

@HttpAdapter
@RequestMapping("/api/employees/{employeeId}")
class UpdateEmployeeByIdHttpAdapter(
    private val updateEmployeeById: UpdateEmployeeById,
    private val applyPatch: ApplyPatch
) {

    @PutMapping
    fun put(
        @PathVariable employeeId: UUID,
        @RequestBody request: EmployeeChangeData
    ): ResponseEntity<EmployeeResource> {
        val result = updateEmployeeById(employeeId) {
            it.merge(request)
        }
        return when (result) {
            is NotUpdatedBecauseEmployeeNotFound -> notFound().build()
            is NotUpdatedBecauseEmployeeNotChanged -> ok(result.employee.toResource())
            is SuccessfullyUpdatedEmployee -> ok(result.employee.toResource())
        }
    }

    @PatchMapping(consumes = ["application/json-patch+json"])
    fun patch(
        @PathVariable employeeId: UUID,
        @RequestBody patch: JsonPatch
    ): ResponseEntity<EmployeeResource> {
        val result = updateEmployeeById(employeeId) {
            it.merge(applyPatch(patch, it.toChangeData()))
        }
        return when (result) {
            is NotUpdatedBecauseEmployeeNotFound -> notFound().build()
            is NotUpdatedBecauseEmployeeNotChanged -> ok(result.employee.toResource())
            is SuccessfullyUpdatedEmployee -> ok(result.employee.toResource())
        }
    }

}
