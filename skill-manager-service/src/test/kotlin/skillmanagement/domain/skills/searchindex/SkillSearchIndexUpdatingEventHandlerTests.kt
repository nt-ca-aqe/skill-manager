package skillmanagement.domain.skills.searchindex

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableAsync
import skillmanagement.common.events.Event
import skillmanagement.domain.skills.model.SkillAddedEvent
import skillmanagement.domain.skills.model.SkillDeletedEvent
import skillmanagement.domain.skills.model.SkillUpdatedEvent
import skillmanagement.domain.skills.model.skill_java
import skillmanagement.domain.skills.model.skill_kotlin
import skillmanagement.domain.skills.model.skill_python
import skillmanagement.test.ResetMocksAfterEachTest
import skillmanagement.test.TechnologyIntegrationTest

@ResetMocksAfterEachTest
@TechnologyIntegrationTest
@SpringBootTest(classes = [SkillSearchIndexUpdateEventHandlerTestsConfiguration::class])
internal class SkillSearchIndexUpdatingEventHandlerTests(
    @Autowired val eventPublisher: ApplicationEventPublisher,
    @Autowired val skillSearchIndex: SkillSearchIndex
) {

    @Test
    fun `SkillAddedEvent will add a new index entry`() {
        publishEvent(SkillAddedEvent(skill_kotlin))
        verify(timeout = 1_000) { skillSearchIndex.index(skill_kotlin) }
    }

    @Test
    fun `SkillUpdatedEvent will update an existing index entry`() {
        publishEvent(SkillUpdatedEvent(skill_java))
        verify(timeout = 1_000) { skillSearchIndex.index(skill_java) }
    }

    @Test
    fun `SkillDeletedEvent will delete an existing index entry`() {
        publishEvent(SkillDeletedEvent(skill_python))
        verify(timeout = 1_000) { skillSearchIndex.deleteById(skill_python.id) }
    }

    private fun publishEvent(event: Event) = eventPublisher.publishEvent(event)
}

@EnableAsync
@Import(SkillSearchIndexUpdatingEventHandler::class)
private class SkillSearchIndexUpdateEventHandlerTestsConfiguration {

    @Bean
    fun skillSearchIndex(): SkillSearchIndex = mockk(relaxUnitFun = true)

}
