package skillmanagement.domain.employees.usecases.get

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.noContent
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.toResource
import java.util.UUID

@HttpAdapter
@RequestMapping("/api/employees/{id}")
class GetEmployeeByIdHttpAdapter(
    private val getEmployeeById: GetEmployeeById
) {

    @GetMapping
    fun get(@PathVariable id: UUID): ResponseEntity<EmployeeResource> {
        val employee = getEmployeeById(id)
        if (employee != null) {
            return ok(employee.toResource())
        }
        return noContent().build()
    }

}
