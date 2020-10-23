package skillmanagement.domain.skills.model

import org.junit.jupiter.api.Nested
import skillmanagement.test.AbstractJsonSerializationTests
import skillmanagement.test.TechnologyIntegrationTest

internal class SkillResourceTests {

    @Nested
    @TechnologyIntegrationTest
    inner class JsonSerializationTests : AbstractJsonSerializationTests<SkillResource>() {
        val instance = skill_resource_kotlin
        val json = """
            {
              "id": "3f7985b9-f5f0-4662-bda9-1dcde01f5f3b",
              "label": "Kotlin",
              "description": "The coolest programming language.",
              "tags": [
                "cool",
                "language"
              ],
              "links": []
            }
            """

        override val serializationExamples = listOf(instance to json)

    }

}
