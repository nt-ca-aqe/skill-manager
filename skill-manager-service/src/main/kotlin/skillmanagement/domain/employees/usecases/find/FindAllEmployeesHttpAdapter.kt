package skillmanagement.domain.employees.usecases.find

import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import skillmanagement.common.search.PageIndex
import skillmanagement.common.search.PageSize
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.toAllResource

@HttpAdapter
@RequestMapping("/api/employees")
class FindAllEmployeesHttpAdapter(
    private val findEmployees: FindEmployees
) {

    @GetMapping
    fun get(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "100") size: Int
    ): PagedModel<EmployeeResource> {
        val employees = findEmployees(AllEmployeesQuery(PageIndex(page), PageSize(size)))
        return employees.toAllResource()
    }

}
