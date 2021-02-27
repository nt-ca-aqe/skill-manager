package skillmanagement.domain.skills.model

import org.junit.jupiter.api.Nested
import skillmanagement.test.AbstractJsonSerializationTests
import skillmanagement.test.TechnologyIntegrationTest

internal class SkillTests {

    @Nested
    @TechnologyIntegrationTest
    inner class JsonSerializationTests : AbstractJsonSerializationTests<Skill>() {
        override val serializationExamples = listOf(
            skill_kotlin to skill_kotlin_json,
            skill_java to skill_java_json,
            skill_python to skill_python_json
        )
    }

}