package skillmanagement.domain.projects.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import mu.KotlinLogging.logger
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import skillmanagement.common.stereotypes.LastingMetric
import skillmanagement.common.stereotypes.TechnicalFunction
import java.util.concurrent.atomic.AtomicLong

private const val RATE_PROPERTY = "\${metrics.projects.total.update.rate}"
private const val DELAY_PROPERTY = "\${metrics.projects.total.update.delay}"

@LastingMetric
class TotalNumberOfProjects(
    private val getTotalNumberOfProjectsFromDataStore: GetTotalNumberOfProjectsFromDataStore
) : MeterBinder {

    private val log = logger {}
    private val totalNumberOfProjects = AtomicLong()

    override fun bindTo(registry: MeterRegistry) {
        registry.gauge("projects.total", totalNumberOfProjects)
    }

    @Scheduled(fixedRateString = RATE_PROPERTY, initialDelayString = DELAY_PROPERTY)
    fun updateCache() {
        log.debug { "Updating TotalNumberOfProjects cache" }
        val newTotal = getTotalNumberOfProjectsFromDataStore()
        totalNumberOfProjects.set(newTotal)
        log.debug { "TotalNumberOfProjects cache updated: $newTotal" }
    }

}

@TechnicalFunction
class GetTotalNumberOfProjectsFromDataStore(
    private val jdbcTemplate: JdbcTemplate
) {

    private val query = "SELECT count(*) AS total FROM projects"

    operator fun invoke(): Long = jdbcTemplate.queryForObject(query, Long::class.java)!!

}
