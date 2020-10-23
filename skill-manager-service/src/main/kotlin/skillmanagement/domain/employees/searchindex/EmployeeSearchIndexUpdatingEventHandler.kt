package skillmanagement.domain.employees.searchindex

import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import skillmanagement.common.stereotypes.EventHandler
import skillmanagement.domain.employees.model.EmployeeAddedEvent
import skillmanagement.domain.employees.model.EmployeeDeletedEvent
import skillmanagement.domain.employees.model.EmployeeUpdatedEvent

@EventHandler
class EmployeeSearchIndexUpdatingEventHandler(
    private val searchIndex: EmployeeSearchIndex
) {

    @Async
    @EventListener
    fun handle(event: EmployeeAddedEvent) {
        searchIndex.index(event.employee)
    }

    @Async
    @EventListener
    fun handle(event: EmployeeUpdatedEvent) {
        searchIndex.index(event.employee)
    }

    @Async
    @EventListener
    fun handle(event: EmployeeDeletedEvent) {
        searchIndex.deleteById(event.employee.id)
    }

}
