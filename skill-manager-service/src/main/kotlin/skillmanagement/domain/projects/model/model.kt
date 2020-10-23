package skillmanagement.domain.projects.model

import skillmanagement.common.model.Label
import skillmanagement.common.model.Text
import java.time.Instant
import java.util.UUID

data class Project(
    val id: UUID,
    val version: Int,
    val label: ProjectLabel,
    val description: ProjectDescription,
    val lastUpdate: Instant
)

class ProjectLabel(value: String) : Label(value)
class ProjectDescription(value: String) : Text(value)
