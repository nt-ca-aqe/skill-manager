package skillmanagement.domain.projects.usecases.update

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import skillmanagement.common.stereotypes.TechnicalFunction
import skillmanagement.domain.projects.model.Project
import java.time.Clock

@TechnicalFunction
class UpdateProjectInDataStore(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper,
    private val clock: Clock
) {

    private val statement = """
        UPDATE projects
        SET version = :version, data = :data
        WHERE id = :id AND version = :expectedVersion
        """

    /**
     * Updates the given [Project] in the data store.
     *
     * This also updates the [Project.version] and [Project.lastUpdate]
     * properties. Always use the returned [Project] instance for subsequent
     * operation!
     *
     * @throws ConcurrentProjectUpdateException in case there was a concurrent
     * update to this projects's data while the invoking operation was working.
     */
    operator fun invoke(project: Project): Project = doUpdateProject(
        project = project.copy(version = project.version + 1, lastUpdate = clock.instant()),
        expectedVersion = project.version
    )

    private fun doUpdateProject(project: Project, expectedVersion: Int): Project {
        val parameters = mapOf(
            "id" to project.id.toString(),
            "version" to project.version,
            "data" to objectMapper.writeValueAsString(project),
            "expectedVersion" to expectedVersion
        )
        if (jdbcTemplate.update(statement, parameters) == 0) throw ConcurrentProjectUpdateException()
        return project
    }

}

class ConcurrentProjectUpdateException : RuntimeException()
