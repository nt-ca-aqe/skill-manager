package skillmanagement.domain.employees.usecases.add

import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import skillmanagement.common.stereotypes.HttpAdapter
import skillmanagement.domain.employees.model.EmailAddress
import skillmanagement.domain.employees.model.EmployeeResource
import skillmanagement.domain.employees.model.FirstName
import skillmanagement.domain.employees.model.LastName
import skillmanagement.domain.employees.model.TelephoneNumber
import skillmanagement.domain.employees.model.JobTitle
import skillmanagement.domain.employees.model.toResource

@HttpAdapter
@RequestMapping("/api/employees")
class AddEmployeeHttpAdapter(
    private val addEmployee: AddEmployee
) {

    @PostMapping
    @ResponseStatus(CREATED)
    fun post(@RequestBody request: Request): EmployeeResource {
        val employee = addEmployee(
            firstName = request.firstName,
            lastName = request.lastName,
            title = request.title,
            email = request.email,
            telephone = request.telephone
        )
        return employee.toResource()
    }

    data class Request(
        val firstName: FirstName,
        val lastName: LastName,
        val title: JobTitle,
        val email: EmailAddress,
        val telephone: TelephoneNumber
    )

}
