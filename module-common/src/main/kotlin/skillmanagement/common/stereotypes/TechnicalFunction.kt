package skillmanagement.common.stereotypes

import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationTarget.CLASS

/**
 * A [TechnicalFunction] is a reusable piece of technical logic within the
 * domain context.
 *
 * In contrast to a [BusinessFunction] a [TechnicalFunction] is executable by
 * any other piece of code and focuses on one specific small operation.
 */
@Component
@Retention
@Target(CLASS)
annotation class TechnicalFunction
