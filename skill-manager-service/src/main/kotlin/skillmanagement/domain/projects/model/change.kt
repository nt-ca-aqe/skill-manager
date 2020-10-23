package skillmanagement.domain.projects.model

data class ProjectChangeData(
    val label: ProjectLabel,
    val description: ProjectDescription
)

fun Project.toChangeData(): ProjectChangeData =
    ProjectChangeData(label = label, description = description)

fun Project.merge(changes: ProjectChangeData): Project =
    copy(label = changes.label, description = changes.description)
