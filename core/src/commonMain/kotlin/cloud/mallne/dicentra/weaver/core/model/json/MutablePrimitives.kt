@file:Suppress("FunctionName")

package cloud.mallne.dicentra.weaver.core.model.json

import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

fun MutableJsonNumber(value: Number) = MutableJsonProxy(
    JsonPrimitive(value)
)


fun MutableJsonBoolean(value: Boolean) = MutableJsonProxy(
    JsonPrimitive(value)
)

fun MutableJsonString(value: String) = MutableJsonProxy(
    JsonPrimitive(value)
)

fun MutableJsonNull() = MutableJsonProxy(
    JsonNull
)