package cloud.mallne.dicentra.weaver.core.model.json

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
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
    override fun type(): ObjectType = ObjectType.Object
    override fun toString(): String {
        return value.entries.joinToString(
            separator = ",",
            prefix = "{",
            postfix = "}",
            transform = { (k, v) ->
                buildString {
                    printQuoted(k)
                    append(':')
                    append(v)
                }
            }
        )
    }

    companion object {
        fun of(jsonObject: JsonObject): MutableJsonObject {
            return MutableJsonObject(jsonObject.map { (k, v) -> k to MutableJsonProxy(v) }.toMap().toMutableMap())
        }

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