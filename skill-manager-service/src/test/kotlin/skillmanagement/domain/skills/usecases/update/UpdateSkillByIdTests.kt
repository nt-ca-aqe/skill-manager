package skillmanagement.domain.skills.usecases.update

import io.kotlintest.shouldBe
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.retry.annotation.EnableRetry
import skillmanagement.common.events.PublishEvent
import skillmanagement.domain.skills.model.Skill
import skillmanagement.domain.skills.model.SkillDescription
import skillmanagement.domain.skills.model.SkillLabel
import skillmanagement.domain.skills.model.SkillUpdatedEvent
import skillmanagement.domain.skills.model.Tag
import skillmanagement.domain.skills.usecases.get.GetSkillById
import skillmanagement.domain.skills.usecases.update.UpdateSkillByIdResult.SkillNotFound
import skillmanagement.domain.skills.usecases.update.UpdateSkillByIdResult.SuccessfullyUpdated
import skillmanagement.test.ResetMocksAfterEachTest
import skillmanagement.test.TechnologyIntegrationTest
import skillmanagement.test.UnitTest
import skillmanagement.test.instant
import skillmanagement.test.uuid

internal class UpdateSkillByIdTests {

    val id = uuid()
    val skill = Skill(
        id = id,
        version = 2,
        label = SkillLabel("Old Label"),
        description = SkillDescription("Old Description"),
        tags = sortedSetOf(Tag("old")),
        lastUpdate = instant("2020-07-16T12:34:56.789Z")
    )

    @Nested
    @UnitTest
    inner class FunctionalTests {

        val getSkillById: GetSkillById = mockk()
        val updateSkillInDataStore: UpdateSkillInDataStore = mockk()
        val publishEvent: PublishEvent = mockk(relaxUnitFun = true)

        val updateSkillById = UpdateSkillById(getSkillById, updateSkillInDataStore, publishEvent)

        @Test
        fun `updating an existing skill stores it in the data store and publishes an event`() {
            val change: (Skill) -> (Skill) = {
                it.copy(
                    label = SkillLabel("New Label"),
                    description = SkillDescription("New Description"),
                    tags = sortedSetOf(Tag("new"))
                )
            }

            val expectedChangedSkill = change(skill)
            val expectedUpdatedSkill = expectedChangedSkill
                .copy(version = 3, lastUpdate = instant("2020-07-16T12:35:06.789Z"))

            every { getSkillById(id) } returns skill
            every { updateSkillInDataStore(expectedChangedSkill) } answers { simulateUpdate(firstArg()) }

            val result = updateSkillById(id, change)

            result shouldBe SuccessfullyUpdated(expectedUpdatedSkill)
            verify { publishEvent(SkillUpdatedEvent(expectedUpdatedSkill)) }
        }

        @Test
        fun `updating a non-existing skill returns skill not found result`() {
            every { getSkillById(id) } returns null

            val result = updateSkillById(skill.id) { it.copy(label = SkillLabel("New Label")) }

            result shouldBe SkillNotFound

            verify { updateSkillInDataStore wasNot called }
            verify { publishEvent wasNot called }
        }

        @TestFactory
        fun `certain modifications are prohibited`(): List<DynamicTest> = listOf(
            prohibitedModificationTest("Changing the ID") {
                it.copy(id = uuid())
            },
            prohibitedModificationTest("Changing the Version") {
                it.copy(version = 5)
            },
            prohibitedModificationTest("Changing the Last Update") {
                it.copy(lastUpdate = instant("2020-01-01T12:00:00.789Z"))
            }
        )

        private fun prohibitedModificationTest(name: String, operation: (Skill) -> (Skill)) = dynamicTest(name) {
            every { getSkillById(id) } returns skill
            assertThrows<IllegalStateException> {
                updateSkillById(id, operation)
            }
            verify { updateSkillInDataStore wasNot called }
            verify { publishEvent wasNot called }
        }

        private fun simulateUpdate(it: Skill) =
            it.copy(version = it.version + 1, lastUpdate = it.lastUpdate.plusSeconds(10))

    }

    @Nested
    @ResetMocksAfterEachTest
    @TechnologyIntegrationTest
    @SpringBootTest(classes = [RetryTestsConfiguration::class])
    inner class RetryTests(
        @Autowired val getSkillById: GetSkillById,
        @Autowired val updateSkillInDataStore: UpdateSkillInDataStore,
        @Autowired val updateSkillById: UpdateSkillById
    ) {

        @Test
        fun `operation is retried up to 5 times in case of concurrent update exceptions`() {
            every { getSkillById(id) } returns skill
            every { updateSkillInDataStore(any()) } throws ConcurrentSkillUpdateException()
            assertThrows<ConcurrentSkillUpdateException> {
                updateSkillById(id) { it }
            }
            verify(exactly = 5) { getSkillById(id) }
        }

        @Test
        fun `operation does not fail if retrying fixes the problem`() {
            every { getSkillById(id) } returns skill
            every { updateSkillInDataStore(any()) }
                .throws(ConcurrentSkillUpdateException())
                .andThenThrows(ConcurrentSkillUpdateException())
                .andThen(skill)

            updateSkillById(id) { it }

            verify(exactly = 3) { getSkillById(id) }
        }

    }

    @EnableRetry
    @Import(UpdateSkillById::class)
    private class RetryTestsConfiguration {

        @Bean
        fun getSkillById(): GetSkillById = mockk(relaxed = true)

        @Bean
        fun updateSkillInDataStore(): UpdateSkillInDataStore = mockk(relaxed = true)

        @Bean
        fun publishEvent(): PublishEvent = mockk(relaxed = true)

    }

}
