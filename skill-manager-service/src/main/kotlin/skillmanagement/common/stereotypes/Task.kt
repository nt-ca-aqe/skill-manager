package skillmanagement.common.stereotypes

import org.springframework.stereotype.Component
import kotlin.annotation.AnnotationTarget.CLASS

@Component
@Retention
@Target(CLASS)
annotation class Task
