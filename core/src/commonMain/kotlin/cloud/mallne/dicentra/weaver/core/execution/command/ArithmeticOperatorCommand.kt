package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.*
import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.language.ast.expressions.ArithmeticOperator
import cloud.mallne.dicentra.weaver.language.ast.expressions.TopLevelWeaverExpression
import cloud.mallne.dicentra.weaver.language.ast.expressions.TypeCoercion
import kotlinx.serialization.json.JsonElement

object ArithmeticOperatorCommand : WeaverCommand {
    const val NAME = "ArithmeticOperatorCommand"

    object Keys {
        val left = WeaverCommandKey(TopLevelWeaverExpression::class, "TopLevelWeaverExpression.left")
        val right = WeaverCommandKey(TopLevelWeaverExpression::class, "TopLevelWeaverExpression.right")
        val operator = WeaverCommandKey(ArithmeticOperator::class, "Operator")
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, "Notation")
        val input = WeaverCommandKey(JsonElement::class, CommonScopeKeys.INPUT_ELEMENT)
        val yield = WeaverCommandKey(MutableJson::class, CommonScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }


    private fun forPlus(
        left: MutableJson,
        right: MutableJson,
        dispatcher: CommandDispatcher,
        context: WeaverContext
    ): MutableJson {
        val sourceType = right.type()
        val leftType = left.type()
        return when (leftType) {
            ObjectType.String -> {
                MutableJsonString(left.toString() + right.toString())
            } // Anything can become a string for '+'
            ObjectType.Number -> {
                when (sourceType) {
                    ObjectType.Undefined -> left
                    ObjectType.Number -> {
                        val sum = right.asFloatOrNull()?.let { left.asFloatOrNull()?.plus(it) }
                        MutableJsonNumber(sum ?: Double.NaN)
                    }

                    else -> {
                        val coerced = CoerceCommand.thisCommand(dispatcher, context, right, TypeCoercion.Type.Float)
                        val sum = coerced.asFloatOrNull()?.let { left.asFloatOrNull()?.plus(it) }
                        MutableJsonNumber(sum ?: Double.NaN)
                    }

                }
            }

            ObjectType.Boolean -> {
                when (sourceType) {
                    ObjectType.Undefined -> left
                    ObjectType.Boolean -> {
                        val sum = right.asBooleanOrNull()?.let { left.asBooleanOrNull()?.and(it) }
                        MutableJsonBoolean(sum == true)
                    }

                    else -> {
                        val coerced = CoerceCommand.thisCommand(dispatcher, context, right, TypeCoercion.Type.Boolean)
                        val sum = coerced.asBooleanOrNull()?.let { left.asBooleanOrNull()?.and(it) }
                        MutableJsonBoolean(sum == true)
                    }
                }
            }

            ObjectType.List -> {
                when (sourceType) {
                    ObjectType.Undefined -> left
                    ObjectType.List -> {
                        val sum = right.asListOrNull()?.let { left.asListOrNull()?.plus(it) }?.toMutableList()
                        MutableJsonArray(sum ?: mutableListOf())
                    }

                    else -> {
                        val list = left.asListOrNull()
                        list?.add(right)
                        MutableJsonArray(list ?: mutableListOf())
                    }
                }
            }

            ObjectType.Map,
            ObjectType.Object -> {
                when (sourceType) {
                    ObjectType.Undefined -> left
                    ObjectType.Map,
                    ObjectType.Object -> {
                        val sum = right.asMapOrNull()?.let { left.asMapOrNull()?.plus(it) }?.toMutableMap()
                        MutableJsonObject(sum ?: mutableMapOf())
                    }

                    ObjectType.List -> {
                        val sum = right.asListOrNull()?.let {
                            left.asMapOrNull()?.plus(it.mapIndexed { i, v -> "$i" to v }.toMap())
                        }?.toMutableMap()
                        MutableJsonObject(sum ?: mutableMapOf())
                    }

                    else -> {
                        val map = left.asMapOrNull()
                        map?.put(sourceType.name, right)
                        MutableJsonObject(map ?: mutableMapOf())
                    }
                }
            }

            ObjectType.Undefined -> when (sourceType) {
                ObjectType.Undefined -> MutableJsonNull()
                else -> right
            }
        }
    }

    private fun forMinus(
        left: MutableJson,
        right: MutableJson,
        dispatcher: CommandDispatcher,
        context: WeaverContext
    ): MutableJson {
        val sourceType = right.type()
        val leftType = left.type()
        return when (leftType) {
            ObjectType.String -> {
                MutableJsonString(left.toString().replace(right.toString(), ""))
            } // Anything can become a string for '+'
            ObjectType.Number -> {
                when (sourceType) {
                    ObjectType.Undefined -> left
                    ObjectType.Number -> {
                        val sum = right.asFloatOrNull()?.let { left.asFloatOrNull()?.minus(it) }
                        MutableJsonNumber(sum ?: Double.NaN)
                    }

                    else -> {
                        val coerced = CoerceCommand.thisCommand(dispatcher, context, right, TypeCoercion.Type.Float)
                        val sum = coerced.asFloatOrNull()?.let { left.asFloatOrNull()?.minus(it) }
                        MutableJsonNumber(sum ?: Double.NaN)
                    }

                }
            }

            ObjectType.Boolean -> {
                when (sourceType) {
                    ObjectType.Undefined -> left
                    ObjectType.Boolean -> {
                        val sum = right.asBooleanOrNull()?.let { left.asBooleanOrNull()?.or(it) }
                        MutableJsonBoolean(sum == true)
                    }

                    else -> {
                        val coerced = CoerceCommand.thisCommand(dispatcher, context, right, TypeCoercion.Type.Boolean)
                        val sum = coerced.asBooleanOrNull()?.let { left.asBooleanOrNull()?.or(it) }
                        MutableJsonBoolean(sum == true)
                    }
                }
            }

            ObjectType.List -> {
                when (sourceType) {
                    ObjectType.Undefined -> left
                    ObjectType.List -> {
                        val sum = right.asListOrNull()?.let { left.asListOrNull()?.minus(it) }?.toMutableList()
                        MutableJsonArray(sum ?: mutableListOf())
                    }

                    else -> {
                        val list = left.asListOrNull()
                        list?.remove(right)
                        MutableJsonArray(list ?: mutableListOf())
                    }
                }
            }

            ObjectType.Map,
            ObjectType.Object -> {
                when (sourceType) {
                    ObjectType.Undefined -> left
                    ObjectType.Map,
                    ObjectType.Object -> {
                        val sum =
                            right.asMapOrNull()?.let { rightMap -> left.asMapOrNull()?.minus(rightMap.map { it.key }) }
                                ?.toMutableMap()
                        MutableJsonObject(sum ?: mutableMapOf())
                    }

                    ObjectType.List -> {
                        val sum = right.asListOrNull()?.let {
                            left.asMapOrNull()?.minus(it.map { v -> v.toString() })
                        }?.toMutableMap()
                        MutableJsonObject(sum ?: mutableMapOf())
                    }

                    else -> {
                        val map = left.asMapOrNull()
                        map?.remove(right.toString())
                        MutableJsonObject(map ?: mutableMapOf())
                    }
                }
            }

            ObjectType.Undefined -> when (sourceType) {
                ObjectType.Undefined -> MutableJsonNull()
                ObjectType.Number -> right.asFloatOrNull()?.let { MutableJsonNumber(-it) } ?: MutableJsonNull()
                ObjectType.List -> right.asListOrNull()?.let { MutableJsonArray(it.reversed().toMutableList()) }
                    ?: MutableJsonNull()

                else -> right
            }
        }
    }

    private fun forMultiply(
        left: MutableJson,
        right: MutableJson,
        dispatcher: CommandDispatcher,
        context: WeaverContext
    ): MutableJsonProxy {
        val sourceType = right.type()
        val leftType = left.type()

        val safeLeft = if (leftType == ObjectType.Number) {
            left
        } else {
            CoerceCommand.thisCommand(dispatcher, context, left, TypeCoercion.Type.Float)
        }
        val safeRight = if (sourceType == ObjectType.Number) {
            right
        } else {
            CoerceCommand.thisCommand(dispatcher, context, right, TypeCoercion.Type.Float)
        }
        val sum = safeRight.asFloatOrNull()?.let { safeLeft.asFloatOrNull()?.times(it) }
        return MutableJsonNumber(sum ?: Double.NaN)
    }

    private fun forDivide(
        left: MutableJson,
        right: MutableJson,
        dispatcher: CommandDispatcher,
        context: WeaverContext
    ): MutableJsonProxy {
        val sourceType = right.type()
        val leftType = left.type()

        val safeLeft = if (leftType == ObjectType.Number) {
            left
        } else {
            CoerceCommand.thisCommand(dispatcher, context, left, TypeCoercion.Type.Float)
        }
        val safeRight = if (sourceType == ObjectType.Number) {
            right
        } else {
            CoerceCommand.thisCommand(dispatcher, context, right, TypeCoercion.Type.Float)
        }
        val sum = safeRight.asFloatOrNull()?.let { safeLeft.asFloatOrNull()?.div(it) }
        return MutableJsonNumber(sum ?: Double.NaN)
    }

    private fun forModulo(
        left: MutableJson,
        right: MutableJson,
        dispatcher: CommandDispatcher,
        context: WeaverContext
    ): MutableJsonProxy {
        val sourceType = right.type()
        val leftType = left.type()

        val safeLeft = if (leftType == ObjectType.Number) {
            left
        } else {
            CoerceCommand.thisCommand(dispatcher, context, left, TypeCoercion.Type.Float)
        }
        val safeRight = if (sourceType == ObjectType.Number) {
            right
        } else {
            CoerceCommand.thisCommand(dispatcher, context, right, TypeCoercion.Type.Float)
        }
        val sum = safeRight.asFloatOrNull()?.let { safeLeft.asFloatOrNull()?.mod(it) }
        return MutableJsonNumber(sum ?: Double.NaN)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val left = context.get(Keys.left)
        val right = context.get(Keys.right)
        val operator = context.get(Keys.operator)
        val notation = context.get(Keys.notation)
        val input = context.get(Keys.input)

        val leftRes = WeaverExpressionCommand.thisCommand(dispatcher, context, left, notation, input)
        val rightRes = WeaverExpressionCommand.thisCommand(dispatcher, context, right, notation, input)
        leftRes.type()
        rightRes.type()

        val yield = when (operator) {
            ArithmeticOperator.PLUS -> forPlus(leftRes, rightRes, dispatcher, context)
            ArithmeticOperator.MINUS -> forMinus(leftRes, rightRes, dispatcher, context)
            ArithmeticOperator.MUL -> forMultiply(leftRes, rightRes, dispatcher, context)
            ArithmeticOperator.DIV -> forDivide(leftRes, rightRes, dispatcher, context)
            ArithmeticOperator.MOD -> forModulo(leftRes, rightRes, dispatcher, context)
        }

        context.put(Keys.yield, yield)
    }

    fun thisCommand(
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        left: TopLevelWeaverExpression,
        right: TopLevelWeaverExpression,
        operator: ArithmeticOperator,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJson {
        val res = dispatcher.dispatch(
            NAME,
            context,
            Keys.left.holder(left),
            Keys.right.holder(right),
            Keys.operator.holder(operator),
            Keys.notation.holder(notation),
            Keys.input.holder(input)
        )
        return if (res is CommandDispatcher.Result.Failure) {
            throw res.throwable
        } else {
            context.get(Keys.yield)
        }
    }
}