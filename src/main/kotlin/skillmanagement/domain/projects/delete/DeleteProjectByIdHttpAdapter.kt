package skillmanagement.domain.projects.delete

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import skillmanagement.domain.HttpAdapter
import java.util.*

@HttpAdapter
@RequestMapping("/api/projects/{id}")
class DeleteProjectByIdHttpAdapter(
    private val deleteProjectById: DeleteProjectById
) {

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        deleteProjectById(id)
    }

}
