package skillmanagement.domain.employees.usecases.find

import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import skillmanagement.common.search.PageIndex
import skillmanagement.common.search.PageSize
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.toSearchResource

@HttpAdapter
@RequestMapping("/api/employees/_search")
class SearchForEmployeesHttpAdapter(
    private val findEmployees: FindEmployees
) {

    @PostMapping
    fun post(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "100") size: Int,
        @RequestBody request: SearchRequest
    ): PagedModel<EmployeeResource> {
        val employees = findEmployees(EmployeesMatchingQuery(PageIndex(page), PageSize(size), request.query))
        return employees.toSearchResource()
    }

    data class SearchRequest(
        val query: String
    )

}
