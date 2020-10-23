package skillmanagement.common.http.patch

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.github.fge.jsonpatch.JsonPatch
import skillmanagement.common.stereotypes.TechnicalFunction

@TechnicalFunction
class ApplyPatch(
    private val objectMapper: ObjectMapper
) {

    operator fun <T : Any> invoke(patch: JsonPatch, patchable: T): T {
        try {
            val patchedJsonNode = patch.apply(objectMapper.convertValue<JsonNode>(patchable))
            return objectMapper.treeToValue(patchedJsonNode, patchable::class.java)!!
        } catch (e: Exception) {
            throw InvalidPatchException(e)
        }
    }

}
