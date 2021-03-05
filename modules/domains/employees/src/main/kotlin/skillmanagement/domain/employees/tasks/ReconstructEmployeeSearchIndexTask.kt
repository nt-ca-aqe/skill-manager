package skillmanagement.domain.employees.tasks

import mu.KotlinLogging.logger
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint
import skillmanagement.common.search.AbstractReconstructSearchIndexTask
import skillmanagement.common.stereotypes.Task
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.searchindex.EmployeeSearchIndex
import skillmanagement.domain.employees.usecases.read.GetEmployeesFromDataStoreFunction

@Task
@WebEndpoint(id = "reconstructEmployeeSearchIndex")
internal class ReconstructEmployeeSearchIndexTask(
    override val searchIndex: EmployeeSearchIndex,
    private val getEmployeeFromDataStore: GetEmployeesFromDataStoreFunction
) : AbstractReconstructSearchIndexTask<Employee>() {

    override val log = logger {}

    override fun executeForAllInstancesInDataStore(callback: (Employee) -> Unit) = getEmployeeFromDataStore(callback)
    override fun shortDescription(instance: Employee) = "${instance.id} - ${instance.firstName} ${instance.lastName}"

}
