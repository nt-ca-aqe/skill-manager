package skillmanagement.domain.skills.usecases.update

import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.transaction.annotation.Transactional

@Retention
@Retryable(
    ConcurrentSkillUpdateException::class,
    maxAttempts = 5,
    backoff = Backoff(
        delay = 100,
        multiplier = 1.5,
        random = true
    )
)
@Transactional
annotation class RetryOnConcurrentSkillUpdate
