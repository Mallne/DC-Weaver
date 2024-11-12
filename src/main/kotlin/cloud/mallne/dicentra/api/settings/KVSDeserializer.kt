package cloud.mallne.dicentra.api.settings

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class KVSDeserializer : JsonDeserializer<JsonElement>() {
    override fun deserialize(parser: JsonParser?, context: DeserializationContext?): JsonElement {
        val s = parser?.toString()
        return Json.parseToJsonElement(s ?: "{}")
    }
}