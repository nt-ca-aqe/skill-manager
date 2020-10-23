package skillmanagement.common.http.error

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.exc.ValueInstantiationException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import skillmanagement.common.http.patch.InvalidPatchException
import skillmanagement.common.validation.ValidationException
import java.time.Clock
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class GlobalRestControllerAdvice(
    private val clock: Clock
) {

    private val log = KotlinLogging.logger {}

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(e: HttpMessageNotReadableException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val cause = (e.cause as? ValueInstantiationException)?.cause
        if (cause is ValidationException) {
            val body = errorResponse(
                clock = clock,
                request = request,
                status = HttpStatus.BAD_REQUEST,
                message = "Request body validation failed!",
                details = cause.problems
            )
            log.info(e) { "Received bad request, responding with: $body" }
            return ResponseEntity.badRequest().body(body)
        }
        throw e
    }

    @ExceptionHandler(InvalidPatchException::class)
    fun handle(e: InvalidPatchException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val body = errorResponse(
            clock = clock,
            request = request,
            status = HttpStatus.BAD_REQUEST,
            message = "Invalid Patch",
            details = detailsFor(e)
        )
        log.warn(e) { "Received bad request, responding with: $body" }
        return ResponseEntity.badRequest().body(body)
    }

    private fun detailsFor(e: InvalidPatchException): List<String>? =
        when (val cause = e.cause) {
            is ValueInstantiationException -> {
                when (val causeOfCause = cause.cause) {
                    is ValidationException -> causeOfCause.problems
                    else -> causeOfCause?.message?.let { listOf(it) }
                }
            }
            is MissingKotlinParameterException -> {
                listOf("Parameter '${cause.parameter.name}' is not allowed to be 'null'!")
            }
            is MismatchedInputException -> {
                listOf("Patch value is of unsupported data type!")
            }
            else -> cause?.message?.let { listOf(it) }
        }

}
