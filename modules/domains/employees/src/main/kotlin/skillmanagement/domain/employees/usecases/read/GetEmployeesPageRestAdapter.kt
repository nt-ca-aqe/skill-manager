package skillmanagement.domain.employees.usecases.read

import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import skillmanagement.common.model.PageIndex
import skillmanagement.common.model.PageSize
import skillmanagement.common.model.Pagination
import skillmanagement.common.stereotypes.RestAdapter
import skillmanagement.domain.employees.model.EmployeeRepresentation
import skillmanagement.domain.employees.model.toResource

@RestAdapter
@RequestMapping("/api/employees")
internal class GetEmployeesPageRestAdapter(
    private val getEmployeesPage: GetEmployeesPageFunction
) {

    @GetMapping
    fun get(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?
    ): PagedModel<EmployeeRepresentation> {
        val employees = getEmployeesPage(query(page, size))
        return employees.toResource()
    }

    private fun query(page: Int?, size: Int?) =
        AllEmployeesQuery(Pagination(PageIndex.of(page), PageSize.of(size)))

}
