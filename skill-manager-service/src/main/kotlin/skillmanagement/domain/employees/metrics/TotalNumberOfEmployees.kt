package skillmanagement.domain.employees.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import mu.KotlinLogging.logger
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import skillmanagement.common.stereotypes.LastingMetric
import skillmanagement.common.stereotypes.TechnicalFunction
import java.util.concurrent.atomic.AtomicLong

private const val RATE_PROPERTY = "\${metrics.employees.total.update.rate}"
private const val DELAY_PROPERTY = "\${metrics.employees.total.update.delay}"

@LastingMetric
class TotalNumberOfEmployees(
    private val getTotalNumberOfEmployeesFromDataStore: GetTotalNumberOfEmployeesFromDataStore
) : MeterBinder {

    private val log = logger {}
    private val totalNumberOfEmployees = AtomicLong()

    override fun bindTo(registry: MeterRegistry) {
        registry.gauge("employees.total", totalNumberOfEmployees)
    }

    @Scheduled(fixedRateString = RATE_PROPERTY, initialDelayString = DELAY_PROPERTY)
    fun updateCache() {
        log.debug { "Updating TotalNumberOfEmployees cache" }
        val newTotal = getTotalNumberOfEmployeesFromDataStore()
        totalNumberOfEmployees.set(newTotal)
        log.debug { "TotalNumberOfEmployees cache updated: $newTotal" }
    }

}

@TechnicalFunction
class GetTotalNumberOfEmployeesFromDataStore(
    private val jdbcTemplate: JdbcTemplate
) {

    private val query = "SELECT count(*) AS total FROM employees"

    operator fun invoke(): Long = jdbcTemplate.queryForObject(query, Long::class.java)!!

}
