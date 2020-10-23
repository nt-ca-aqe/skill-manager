package skillmanagement.common.search

import mu.KLogger
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation
import kotlin.system.measureTimeMillis

abstract class AbstractReconstructSearchIndexTask<T : Any> : Runnable {

    protected abstract val log: KLogger
    protected abstract val searchIndex: AbstractSearchIndex<T>

    @WriteOperation
    override fun run() {
        log.info { "Reconstructing '$searchIndex':" }
        val resetDuration = resetIndex()
        val indexingDuration = repopulateIndex()
        log.info { "Reconstruction of '$searchIndex' finished. Took ${resetDuration + indexingDuration}ms." }
    }

    private fun resetIndex(): Long {
        log.debug { "Resetting '$searchIndex':" }
        val duration = measureTimeMillis {
            searchIndex.reset()
        }
        log.debug { "Reset of '$searchIndex' finished. Took ${duration}ms." }
        return duration
    }

    private fun repopulateIndex(): Long {
        log.debug { "Repopulating '$searchIndex':" }
        val duration = measureTimeMillis {
            executeForAllInstancesInDataStore { instance ->
                log.debug { "Indexing [${shortDescription(instance)}]" }
                searchIndex.index(instance)
            }
        }
        log.debug { "Repopulation of '$searchIndex' finished. Took ${duration}ms." }
        return duration
    }

    protected abstract fun executeForAllInstancesInDataStore(callback: (T) -> Unit)
    protected abstract fun shortDescription(instance: T): String

}
