package skillmanagement.domain.skills.delete

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import skillmanagement.domain.HttpAdapter
import java.util.*

@HttpAdapter
@RequestMapping("/api/skills/{id}")
class DeleteSkillByIdHttpAdapter(
    private val deleteSkillById: DeleteSkillById
) {

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        deleteSkillById(id)
    }

}
