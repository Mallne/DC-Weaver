package cloud.mallne.dicentra.weaver.core.model.json

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray

@Serializable(with = MutableJsonArray.Companion.Serializer::class)
class MutableJsonArray(
    val value: MutableList<MutableJson>
) : MutableJson, MutableList<MutableJson> by value {
    override fun toString(): String =
        value.joinToString(prefix = "[", postfix = "]", separator = ",")

    companion object {
        object Serializer : KSerializer<MutableJsonArray> {
            override val descriptor: SerialDescriptor
                get() = SerialDescriptor(
                    "cloud.mallne.dicentra.weaver.core.model.json.MutableJsonArray",
                    JsonArray.serializer().descriptor
                )

            override fun serialize(
                encoder: Encoder,
                value: MutableJsonArray
            ) {
                ListSerializer(MutableJson.Companion.Serializer).serialize(encoder, value.value)
            }

            override fun deserialize(decoder: Decoder): MutableJsonArray {
                return MutableJsonArray(
                    ListSerializer(MutableJson.Companion.Serializer).deserialize(decoder).toMutableList()
                )
            }

        }
    }
}