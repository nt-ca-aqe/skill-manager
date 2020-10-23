package skillmanagement.domain.projects.model

import skillmanagement.test.uuid
import java.time.Instant

val project_starlink = Project(
    id = uuid("15c1c1d8-3445-4d95-a945-605aaf087078"),
    version = 1,
    label = ProjectLabel("Starlink satellite"),
    description = ProjectDescription("Lorem ipsum dolor sit amet .."),
    lastUpdate = Instant.parse("2020-07-14T12:34:56.789Z")
)

val project_demo_2 = Project(
    id = uuid("d61c7840-7be3-4668-ae48-6c0b396268b5"),
    version = 1,
    label = ProjectLabel("Demo-2"),
    description = ProjectDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed .."),
    lastUpdate = Instant.parse("2020-07-14T12:34:56.789Z")
)
