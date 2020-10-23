package skillmanagement.domain.skills.usecases.add

import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import org.springframework.util.IdGenerator
import skillmanagement.common.events.PublishEvent
import skillmanagement.domain.skills.model.Skill
import skillmanagement.domain.skills.model.SkillAddedEvent
import skillmanagement.domain.skills.model.SkillDescription
import skillmanagement.domain.skills.model.SkillLabel
import skillmanagement.domain.skills.model.Tag
import skillmanagement.test.UnitTest
import skillmanagement.test.sequentialClock
import skillmanagement.test.sequentialIdGenerator
import skillmanagement.test.uuid
import java.time.Instant
import java.util.Collections.emptySortedSet

@UnitTest
internal class AddSkillTests {

    val ids = arrayOf("312f3bfc-c9b0-4b4c-9cc4-33242cdfc39e", "da8748e1-405b-456c-92d4-26fdad09a0c9")
    val idGenerator: IdGenerator = sequentialIdGenerator(*ids)
    val insertSkillIntoDataStore: InsertSkillIntoDataStore = mockk(relaxUnitFun = true)
    val publishEvent: PublishEvent = mockk(relaxUnitFun = true)
    val clock = sequentialClock("2020-07-14T12:34:56.789Z", "2020-07-14T13:34:56.789Z")

    val addSkill = AddSkill(idGenerator, insertSkillIntoDataStore, publishEvent, clock)

    @Test
    fun `correct Skill instances are constructed and stored`() {
        val actualSkill1 = addSkill(
            label = SkillLabel("Skill #1"),
            description = null,
            tags = emptySortedSet()
        )
        val expectedSkill1 = Skill(
            id = uuid(ids[0]),
            version = 1,
            label = SkillLabel("Skill #1"),
            description = null,
            tags = emptySortedSet(),
            lastUpdate = Instant.parse("2020-07-14T12:34:56.789Z")
        )
        actualSkill1 shouldBe expectedSkill1

        val actualSkill2 = addSkill(
            label = SkillLabel("Skill #2"),
            description = SkillDescription("description"),
            tags = sortedSetOf(Tag("foo-bar"))
        )
        val expectedSkill2 = Skill(
            id = uuid(ids[1]),
            version = 1,
            label = SkillLabel("Skill #2"),
            description = SkillDescription("description"),
            tags = sortedSetOf(Tag("foo-bar")),
            lastUpdate = Instant.parse("2020-07-14T13:34:56.789Z")
        )
        actualSkill2 shouldBe expectedSkill2

        verifyOrder {
            insertSkillIntoDataStore(expectedSkill1)
            publishEvent(SkillAddedEvent(expectedSkill1))
            insertSkillIntoDataStore(expectedSkill2)
            publishEvent(SkillAddedEvent(expectedSkill2))
        }
        confirmVerified(insertSkillIntoDataStore, publishEvent)
    }

}
