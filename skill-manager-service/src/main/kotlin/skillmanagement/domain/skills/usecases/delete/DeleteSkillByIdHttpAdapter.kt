package skillmanagement.domain.skills.usecases.delete

import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import skillmanagement.common.stereotypes.HttpAdapter
import java.util.UUID

@HttpAdapter
@RequestMapping("/api/skills/{id}")
class DeleteSkillByIdHttpAdapter(
    private val deleteSkillById: DeleteSkillById
) {

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        deleteSkillById(id)
    }

}
