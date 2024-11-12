package cloud.mallne.dicentra.api.settings

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


interface KeyValueSetting {
    @Serializable
    val key: String

    @Serializable
    @get:JsonSerialize(using = KVSSerializer::class)
    @get:JsonDeserialize(using = KVSDeserializer::class)
    val value: JsonElement

    @Serializable
    val forcedByLicense: Boolean
}


