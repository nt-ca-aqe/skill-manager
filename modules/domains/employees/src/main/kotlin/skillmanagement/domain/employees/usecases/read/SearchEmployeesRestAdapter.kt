package skillmanagement.domain.employees.usecases.read

import org.springframework.hateoas.PagedModel
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import skillmanagement.common.model.PageIndex
import skillmanagement.common.model.PageSize
import skillmanagement.common.model.Pagination
import skillmanagement.common.stereotypes.RestAdapter
import skillmanagement.domain.employees.model.EmployeeRepresentation
import skillmanagement.domain.employees.model.toSearchResource

@RestAdapter
@RequestMapping("/api/employees/_search")
internal class SearchEmployeesRestAdapter(
    private val getEmployeesPage: GetEmployeesPageFunction
) {

    @PostMapping
    fun post(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?,
        @RequestBody request: SearchRequest
    ): PagedModel<EmployeeRepresentation> {
        val employees = getEmployeesPage(query(request.query, page, size))
        return employees.toSearchResource()
    }

    private fun query(query: String, page: Int?, size: Int?) =
        EmployeesMatchingQuery(query, Pagination(PageIndex.of(page), PageSize.of(size)))

    data class SearchRequest(
        val query: String
    )

}
