package skillmanagement.domain.employees.usecases.projectassignments.delete

import mu.KotlinLogging.logger
import org.springframework.context.event.EventListener
import skillmanagement.common.search.PageSize
import skillmanagement.common.stereotypes.EventHandler
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.usecases.find.EmployeesWhoWorkedOnProject
import skillmanagement.domain.employees.usecases.find.FindEmployeeIds
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeById
import skillmanagement.domain.projects.model.ProjectDeletedEvent
import java.util.UUID

@EventHandler
class ProjectAssignmentDeletingEventHandler(
    private val findEmployeeIds: FindEmployeeIds,
    private val updateEmployeeById: UpdateEmployeeById
) {

    private val log = logger {}

    // TODO: how to update more than one page? (ES eventual consistency)

    @EventListener
    fun handle(event: ProjectDeletedEvent) {
        log.info { "Handling $event" }
        val projectId = event.project.id
        findEmployeeIds(EmployeesWhoWorkedOnProject(projectId = projectId, pageSize = PageSize.MAX))
            .onEach { log.info { "Removing projects assignments of project [$projectId] from employee [${it}]" } }
            .forEach { employeeId ->
                updateEmployeeById(employeeId) { it.removeProjectAssignmentsByProjectId(projectId) }
            }
    }

    private fun Employee.removeProjectAssignmentsByProjectId(projectId: UUID): Employee =
        copy(projects = projects.filter { it.project.id != projectId })

}
