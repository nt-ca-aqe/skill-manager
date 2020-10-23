package skillmanagement.common.validation

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ValidationException(label: String, val problems: List<String>) :
    RuntimeException("Value of $label is invalid: ${problems.joinToString(separator = "; ")}")
