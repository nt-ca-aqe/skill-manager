package skillmanagement.common.events

import mu.KotlinLogging.logger
import org.springframework.context.ApplicationEventPublisher
import skillmanagement.common.stereotypes.TechnicalFunction

/**
 * Used to publish [events][Event] to the application's context.
 *
 * In difference to just using the [ApplicationEventPublisher] directly,
 * this function will also increment the [EventCounter] for each published
 * event.
 */
@TechnicalFunction
class PublishEvent(
    private val eventPublisher: ApplicationEventPublisher,
    private val counter: EventCounter
) {

    private val log = logger {}

    operator fun invoke(event: Event) {
        log.info { "Publishing $event" }
        counter.increment(event::class)
        eventPublisher.publishEvent(event)
    }

}
