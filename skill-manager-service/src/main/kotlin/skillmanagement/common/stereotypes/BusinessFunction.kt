package skillmanagement.common.stereotypes

import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * A [BusinessFunction] is a reusable piece of business logic within the
 * domain context.
 *
 * In contrast to a [TechnicalFunction] a [BusinessFunction] is usually only
 * executable by certain user roles, orchestrates multiple concerns and
 * publishes domain events.
 */
@Component
@Retention
@Target(CLASS)
annotation class BusinessFunction
