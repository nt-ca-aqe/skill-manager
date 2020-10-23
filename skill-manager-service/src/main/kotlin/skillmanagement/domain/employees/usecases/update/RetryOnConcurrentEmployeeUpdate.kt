package skillmanagement.domain.employees.usecases.update

import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.transaction.annotation.Transactional

@Retention
@Retryable(
    ConcurrentEmployeeUpdateException::class,
    maxAttempts = 5,
    backoff = Backoff(
        delay = 100,
        multiplier = 1.5,
        random = true
    )
)
@Transactional
annotation class RetryOnConcurrentEmployeeUpdate
