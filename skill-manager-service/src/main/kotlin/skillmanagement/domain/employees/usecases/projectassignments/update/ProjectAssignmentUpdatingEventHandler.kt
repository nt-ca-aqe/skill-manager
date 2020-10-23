package skillmanagement.domain.employees.usecases.projectassignments.update

import mu.KotlinLogging.logger
import org.springframework.context.event.EventListener
import skillmanagement.common.search.PageSize
import skillmanagement.common.stereotypes.EventHandler
import skillmanagement.domain.employees.model.Employee
import skillmanagement.domain.employees.usecases.find.EmployeesWhoWorkedOnProject
import skillmanagement.domain.employees.usecases.find.FindEmployeeIds
import skillmanagement.domain.employees.usecases.update.UpdateEmployeeById
import skillmanagement.domain.projects.model.Project
import skillmanagement.domain.projects.model.ProjectUpdatedEvent

@EventHandler
class ProjectAssignmentUpdatingEventHandler(
    private val findEmployeeIds: FindEmployeeIds,
    private val updateEmployeeById: UpdateEmployeeById
) {

    private val log = logger {}

    // TODO: how to update more than one page? (ES eventual consistency)

    @EventListener
    fun handle(event: ProjectUpdatedEvent) {
        log.info { "Handling $event" }
        val projectId = event.project.id
        findEmployeeIds(EmployeesWhoWorkedOnProject(projectId = projectId, pageSize = PageSize.MAX))
            .onEach { log.info { "Updating project assignments for project [$projectId] of employee [${it}]" } }
            .forEach { employeeId ->
                updateEmployeeById(employeeId) { it.updateProjectAssignmentsOfProject(event.project) }
            }
    }

    private fun Employee.updateProjectAssignmentsOfProject(project: Project): Employee =
        copy(projects = projects.map {
            when (it.project.id) {
                project.id -> it.copy(project = project)
                else -> it
            }
        })

}
