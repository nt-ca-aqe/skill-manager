package skillmanagement.common.stereotypes

import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * An [EventHandler] is a [Component] that reacts to domain events.
 */
@Component
@Retention
@Target(CLASS)
annotation class EventHandler
