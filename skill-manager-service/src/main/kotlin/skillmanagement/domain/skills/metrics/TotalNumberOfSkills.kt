package skillmanagement.domain.skills.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import mu.KotlinLogging.logger
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import skillmanagement.common.stereotypes.LastingMetric
import skillmanagement.common.stereotypes.TechnicalFunction
import java.util.concurrent.atomic.AtomicLong

private const val RATE_PROPERTY = "\${metrics.skills.total.update.rate}"
private const val DELAY_PROPERTY = "\${metrics.skills.total.update.delay}"

@LastingMetric
class TotalNumberOfSkills(
    private val getTotalNumberOfSkillsFromDataStore: GetTotalNumberOfSkillsFromDataStore
) : MeterBinder {

    private val log = logger {}
    private val totalNumberOfSkills = AtomicLong()

    override fun bindTo(registry: MeterRegistry) {
        registry.gauge("skills.total", totalNumberOfSkills)
    }

    @Scheduled(fixedRateString = RATE_PROPERTY, initialDelayString = DELAY_PROPERTY)
    fun updateCache() {
        log.debug { "Updating TotalNumberOfSkills cache" }
        val newTotal = getTotalNumberOfSkillsFromDataStore()
        totalNumberOfSkills.set(newTotal)
        log.debug { "TotalNumberOfSkills cache updated: $newTotal" }
    }

}

@TechnicalFunction
class GetTotalNumberOfSkillsFromDataStore(
    private val jdbcTemplate: JdbcTemplate
) {

    private val query = "SELECT count(*) AS total FROM skills"

    operator fun invoke(): Long = jdbcTemplate.queryForObject(query, Long::class.java)!!

}
