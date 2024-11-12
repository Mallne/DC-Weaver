package cloud.mallne.dicentra.api.settings

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import kotlinx.serialization.json.JsonElement

class KVSSerializer : JsonSerializer<JsonElement>() {
    override fun serialize(el: JsonElement?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeStartObject()
        gen?.writeRaw(el?.toString())
        gen?.writeEndObject()
    }
}