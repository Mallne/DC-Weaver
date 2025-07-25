package cloud.mallne.dicentra.weaver.core.model.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

@Serializable(with = MutableJsonProxy.Companion.Serializer::class)
class MutableJsonProxy(
    var value: JsonElement
) : MutableJson {
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