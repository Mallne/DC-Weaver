package cloud.mallne.dicentra.weaver.core.model.json

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
sealed interface MutableJson : Comparable<MutableJson> {
    fun type(): ObjectType

    fun asListOrNull(): MutableList<MutableJson>? {
        return when (this) {
            is MutableJsonArray -> value
            is MutableJsonProxy -> (value as? JsonArray)?.let { MutableJsonArray.of(it) }
            else -> null
        }
    }

    fun asMapOrNull(): MutableMap<String, MutableJson>? {
        return when (this) {
            is MutableJsonObject -> value
            is MutableJsonProxy -> (value as? JsonObject)?.let { MutableJsonObject.of(it) }
            else -> null
        }
    }

    fun asBooleanOrNull(): Boolean? = (this as? MutableJsonProxy)?.value?.jsonPrimitive?.booleanOrNull
    fun asNumberOrNull(): Number? = (this as? MutableJsonProxy)?.value?.jsonPrimitive?.floatOrNull
        ?: (this as? MutableJsonProxy)?.value?.jsonPrimitive?.doubleOrNull
        ?: (this as? MutableJsonProxy)?.value?.jsonPrimitive?.longOrNull

    fun asIntOrNull(): Int? = (this as? MutableJsonProxy)?.value?.jsonPrimitive?.intOrNull
    fun asDoubleOrNull(): Double? = (this as? MutableJsonProxy)?.value?.jsonPrimitive?.doubleOrNull
    fun asFloatOrNull(): Float? = (this as? MutableJsonProxy)?.value?.jsonPrimitive?.floatOrNull
    fun asLongOrNull(): Long? = (this as? MutableJsonProxy)?.value?.jsonPrimitive?.longOrNull

    fun asStringOrNull(): String? =
        if ((this is MutableJsonProxy) && this.value.jsonPrimitive.isString) (this as? MutableJsonProxy)?.value?.jsonPrimitive?.content else null

    override fun compareTo(other: MutableJson): Int {
        when {
            this is MutableJsonObject && other is MutableJsonObject -> {
                if (this.size != other.size) return this.size.compareTo(other.size)
                for ((key, value) in this) {
                    val otherValue = other[key] ?: return 1
                    val comparison = value.compareTo(otherValue)
                    if (comparison != 0) return comparison
                }
                return 0
            }

            this is MutableJsonArray && other is MutableJsonArray -> {
                if (this.size != other.size) return this.size.compareTo(other.size)
                for (i in this.indices) {
                    val comparison = this[i].compareTo(other[i])
                    if (comparison != 0) return comparison
                }
                return 0
            }

            this is MutableJsonProxy && other is MutableJsonProxy -> {
                return when {
                    this.value == other.value -> 0
                    else -> this.value.toString().compareTo(other.value.toString())
                }
            }

            this is MutableJsonProxy -> {
                return when (this.value) {
                    is JsonObject -> MutableJsonObject.of(this.value as JsonObject).compareTo(other)
                    is JsonArray -> MutableJsonArray.of(this.value as JsonArray).compareTo(other)
                    else -> this.value.toString().compareTo(other.toString())
                }
            }

            other is MutableJsonProxy -> {
                return when (other.value) {
                    is JsonObject -> this.compareTo(MutableJsonObject.of(other.value as JsonObject))
                    is JsonArray -> this.compareTo(MutableJsonArray.of(other.value as JsonArray))
                    else -> this.toString().compareTo(other.value.toString())
                }
            }

            else -> return this.toString().compareTo(other.toString())
        }
    }

    fun toStable(): JsonElement {
         return when (this) {
            is MutableJsonObject -> {
                JsonObject(this.map { it.key to it.value.toStable() }.toMap())
            }
            is MutableJsonArray -> {
                JsonArray(this.map { it.toStable() })
            }
            is MutableJsonProxy -> {
                this.value
            }
        }
    }

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