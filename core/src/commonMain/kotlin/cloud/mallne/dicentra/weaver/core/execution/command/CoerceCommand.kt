package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.CommandDispatcher
import cloud.mallne.dicentra.weaver.core.model.WeaverCommand
import cloud.mallne.dicentra.weaver.core.model.WeaverCommandKey
import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.core.model.json.*
import cloud.mallne.dicentra.weaver.language.ast.expressions.TypeCoercion
import kotlinx.serialization.json.*

object CoerceCommand : WeaverCommand {
    const val NAME = "CoerceCommand"

    object Keys {
        val input = WeaverCommandKey(MutableJson::class, CommonScopeKeys.INPUT_MUTABLE_ELEMENT)
        val coerceTo = WeaverCommandKey(TypeCoercion.Type::class, "TypeCoercion")
        val yield = WeaverCommandKey(MutableJsonProxy::class, CommonScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val input = context.get(Keys.input)
        val coerceTo = context.get(Keys.coerceTo)

        val yield = when (coerceTo) {
            TypeCoercion.Type.Integer -> integerCoercion(input)
            TypeCoercion.Type.Float -> floatCoercion(input)
            TypeCoercion.Type.String -> stringCoercion(input)
            TypeCoercion.Type.Boolean -> booleanCoercion(input)
            TypeCoercion.Type.AvailabilityBoolean -> avBooleanCoercion(input)
            TypeCoercion.Type.AvailabilityInteger -> avIntegerCoercion(input)
        }

        context.put(Keys.yield, yield)
    }

    private fun integerCoercion(input: MutableJson): MutableJsonProxy {
        return when (input) {
            is MutableJsonArray -> MutableJsonNumber(0)
            is MutableJsonObject -> MutableJsonNumber(0)
            is MutableJsonProxy -> {
                if (input.value is JsonPrimitive) {
                    val primitive = input.value.jsonPrimitive
                    val content =
                        primitive.intOrNull ?: (primitive.floatOrNull?.toInt())
                        ?: primitive.booleanOrNull?.let { if (it) 1 else 0 } ?: primitive.toString().toIntOrNull()
                        ?: 0
                    MutableJsonNumber(content)
                } else {
                    MutableJsonNumber(0)
                }
            }
        }
    }

    private fun floatCoercion(input: MutableJson): MutableJsonProxy {
        return when (input) {
            is MutableJsonArray -> MutableJsonNumber(0f)
            is MutableJsonObject -> MutableJsonNumber(0.0f)
            is MutableJsonProxy -> {
                if (input.value is JsonPrimitive) {
                    val primitive = input.value.jsonPrimitive
                    val content =
                        primitive.floatOrNull ?: primitive.intOrNull?.toFloat()
                        ?: primitive.booleanOrNull?.let { if (it) 1f else 0f } ?: primitive.toString().toFloatOrNull()
                        ?: 0f
                    MutableJsonNumber(content)
                } else {
                    MutableJsonNumber(0f)
                }
            }
        }
    }

    private fun stringCoercion(input: MutableJson): MutableJsonProxy {
        return MutableJsonString(input.toString())
    }

    private fun booleanCoercion(input: MutableJson): MutableJsonProxy {
        return when (input) {
            is MutableJsonArray -> booleanCoercionCollection(input)
            is MutableJsonObject -> booleanCoercionCollection(input)
            is MutableJsonProxy -> {
                when (input.value) {
                    is JsonArray -> booleanCoercionCollection(input.value.jsonArray)
                    is JsonObject -> booleanCoercionCollection(input.value.jsonObject)
                    JsonNull -> MutableJsonBoolean(false)
                    is JsonPrimitive -> {
                        val primitive = input.value.jsonPrimitive
                        var content = primitive.booleanOrNull
                        if (content == null && primitive.isString && primitive.toString()
                                .toBooleanStrictOrNull() != null
                        ) {
                            content = primitive.toString().toBooleanStrictOrNull()
                        }
                        if (content == null && primitive.intOrNull != null) {
                            content = primitive.intOrNull != 0
                        }
                        if (content == null && primitive.floatOrNull != null) {
                            content = primitive.floatOrNull != 0f
                        }
                        if (content == null && primitive.isString && primitive.toString().isNotBlank()) {
                            content = true
                        }
                        MutableJsonBoolean(content ?: false)
                    }
                }
            }
        }
    }

    private fun booleanCoercionCollection(input: List<*>): MutableJsonProxy {
        return if (input.isEmpty()) {
            MutableJsonBoolean(false)
        } else {
            MutableJsonBoolean(true)
        }
    }

    private fun booleanCoercionCollection(input: Map<*, *>): MutableJsonProxy {
        return if (input.isEmpty()) {
            MutableJsonBoolean(false)
        } else {
            MutableJsonBoolean(true)
        }
    }

    private fun avBooleanCoercion(input: MutableJson): MutableJsonProxy {
        return if (input is MutableJsonProxy && input.value == JsonNull) {
            MutableJsonBoolean(false)
        } else {
            MutableJsonBoolean(true)
        }
    }

    private fun avIntegerCoercion(input: MutableJson): MutableJsonProxy {
        return when (input) {
            is MutableJsonArray -> avIntegerCoercionCollection(input)
            is MutableJsonObject -> avIntegerCoercionCollection(input)
            is MutableJsonProxy -> {
                when (input.value) {
                    is JsonArray -> avIntegerCoercionCollection(input.value.jsonArray)
                    is JsonObject -> avIntegerCoercionCollection(input.value.jsonObject)
                    JsonNull -> MutableJsonNumber(0)
                    is JsonPrimitive -> {
                        val primitive = input.value.jsonPrimitive
                        var content: Int? = null
                        if (content == null && primitive.isString) {
                            content = primitive.toString().length
                        }
                        if (content == null && primitive.intOrNull != null) {
                            content = 1
                        }
                        if (content == null && primitive.floatOrNull != null) {
                            content = 1
                        }
                        if (content == null && primitive.booleanOrNull != null) {
                            content = 1
                        }
                        MutableJsonNumber(content ?: 0)
                    }
                }
            }
        }
    }

    private fun avIntegerCoercionCollection(input: List<*>): MutableJsonProxy {
        return MutableJsonNumber(input.size)
    }

    private fun avIntegerCoercionCollection(input: Map<*, *>): MutableJsonProxy {
        return MutableJsonNumber(input.size)
    }
}