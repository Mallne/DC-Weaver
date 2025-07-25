package cloud.mallne.dicentra.weaver.core.model.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
sealed interface MutableJson {
    companion object {
        object Serializer : KSerializer<MutableJson> {
            override val descriptor: SerialDescriptor = SerialDescriptor(
                "cloud.mallne.dicentra.weaver.core.model.json.MutableJson",
                JsonElement.serializer().descriptor
            )

            override fun serialize(
                encoder: Encoder,
                value: MutableJson
            ) {
                when (value) {
                    is MutableJsonObject -> encoder.encodeSerializableValue(
                        MutableJsonObject.Companion.Serializer,
                        value
                    )

                    is MutableJsonArray -> encoder.encodeSerializableValue(MutableJsonArray.Companion.Serializer, value)
                    is MutableJsonProxy -> encoder.encodeSerializableValue(MutableJsonProxy.Companion.Serializer, value)
                }
            }

            override fun deserialize(decoder: Decoder): MutableJson {
                val el = JsonElement.serializer().deserialize(decoder)
                return convertToMutable(el)
            }

            private fun convertToMutable(el: JsonElement): MutableJson = when (el) {
                is JsonObject -> MutableJsonObject(
                    el.map { (key, value) -> key to convertToMutable(value) }.toMap().toMutableMap()
                )

                is JsonArray -> MutableJsonArray(el.map { convertToMutable(it) }.toMutableList())
                else -> MutableJsonProxy(el)
            }

        }
    }
}