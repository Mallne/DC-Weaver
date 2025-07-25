package cloud.mallne.dicentra.weaver.core.model.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject

@Serializable(with = MutableJsonObject.Companion.Serializer::class)
class MutableJsonObject(
    val value: MutableMap<String, MutableJson>
) : MutableJson, MutableMap<String, MutableJson> by value {
    companion object {
        object Serializer : KSerializer<MutableJsonObject> {
            override val descriptor: SerialDescriptor
                get() = SerialDescriptor(
                    "cloud.mallne.dicentra.weaver.core.model.json.MutableJsonObject",
                    JsonObject.serializer().descriptor
                )

            override fun serialize(
                encoder: Encoder,
                value: MutableJsonObject
            ) {
                MapSerializer(String.serializer(), MutableJson.Companion.Serializer).serialize(encoder, value.value)
            }

            override fun deserialize(decoder: Decoder): MutableJsonObject {
                return MutableJsonObject(
                    MapSerializer(
                        String.serializer(),
                        MutableJson.Companion.Serializer
                    ).deserialize(decoder).toMutableMap()
                )
            }

        }
    }
}