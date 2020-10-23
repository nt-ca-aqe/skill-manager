package skillmanagement.common.stereotypes

import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ResponseBody
import kotlin.annotation.AnnotationTarget.CLASS

@Controller
@ResponseBody
@Retention
@Target(CLASS)
@Transactional
annotation class HttpAdapter
