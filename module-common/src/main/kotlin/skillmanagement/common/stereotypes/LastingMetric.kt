package skillmanagement.common.stereotypes

import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * A [LastingMetric] is a [Component] that exposes one or more meters who's
 * values are calculated from persistent data. They are usually _KPIs_ or
 * other analysis and alerting numbers. Since they are based on data that is
 * held outside the application's memory, they survive application restarts.
 *
 * These metrics can be read using either the _Spring Boot Actuator_ `metrics`
 * or `prometheus` endpoints.
 *
 * @see TransientMetric
 */
@Component
@Retention
@Target(CLASS)
annotation class LastingMetric
