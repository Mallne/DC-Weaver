package cloud.mallne.dicentra.weaver.core.model.json

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = MutableJsonProxy.Companion.Serializer::class)
class MutableJsonProxy(
    var value: JsonElement
) : MutableJson {
    override fun type(): ObjectType {
        return when (value) {
            is JsonArray -> ObjectType.List
            is JsonObject -> ObjectType.Object
            JsonNull -> ObjectType.Undefined
            is JsonPrimitive -> {
                if ((value as JsonPrimitive).isString) ObjectType.String
                else if ((value as JsonPrimitive).booleanOrNull != null) ObjectType.Boolean
                else if ((value as JsonPrimitive).intOrNull != null) ObjectType.Number
                else if ((value as JsonPrimitive).floatOrNull != null) ObjectType.Number
                else ObjectType.Undefined
            }
        }
    }

    override fun toString(): String = value.toString()

    companion object {
        object Serializer : KSerializer<MutableJsonProxy> {
            override val descriptor: SerialDescriptor
                get() = SerialDescriptor(
                    "cloud.mallne.dicentra.weaver.core.model.json.MutableJsonProxy",
                    JsonElement.serializer().descriptor
                )

            override fun serialize(
                encoder: Encoder,
                value: MutableJsonProxy
            ) {
                JsonElement.serializer().serialize(encoder, value.value)
            }

            override fun deserialize(decoder: Decoder): MutableJsonProxy {
                return MutableJsonProxy(JsonElement.serializer().deserialize(decoder))
            }

        }
    }
}