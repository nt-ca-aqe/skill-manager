package skillmanagement.common.stereotypes

import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * A [TransientMetric] is a [Component] that exposes one or more meters who's
 * values are tracked only within the memory of the application. They don't
 * survive application restarts and are therefore not useful for long term
 * analysis.
 *
 * These metrics can be read using either the _Spring Boot Actuator_ `metrics`
 * or `prometheus` endpoints.
 *
 * @see LastingMetric
 */
@Component
@Retention
@Target(CLASS)
annotation class TransientMetric
