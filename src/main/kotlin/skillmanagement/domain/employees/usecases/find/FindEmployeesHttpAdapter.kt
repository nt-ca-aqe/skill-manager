package skillmanagement.domain.employees.usecases.find

import org.springframework.hateoas.CollectionModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.toResource

@HttpAdapter
@RequestMapping("/api/employees")
class FindEmployeesHttpAdapter(
    private val findEmployees: FindEmployees
) {

    @GetMapping
    fun get(): CollectionModel<EmployeeResource> =
        findEmployees().toResource()

}
