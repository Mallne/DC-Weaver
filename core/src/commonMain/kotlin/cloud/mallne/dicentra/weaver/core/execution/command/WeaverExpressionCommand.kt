package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.MutableJsonBoolean
import cloud.mallne.dicentra.weaver.core.model.json.MutableJsonNumber
import cloud.mallne.dicentra.weaver.core.model.json.MutableJsonProxy
import cloud.mallne.dicentra.weaver.core.model.json.MutableJsonString
import cloud.mallne.dicentra.weaver.core.specification.compute.expression.resolve
import cloud.mallne.dicentra.weaver.language.ast.expressions.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive

object WeaverExpressionCommand : WeaverCommand {
    const val NAME = "WeaverExpressionCommand"

    object Keys {
        val expression = WeaverCommandKey(TopLevelWeaverExpression::class, "TopLevelWeaverExpression")
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, "Notation")
        val input = WeaverCommandKey(JsonElement::class, CommonScopeKeys.INPUT_ELEMENT)
        val yield = WeaverCommandKey(MutableJsonProxy::class, CommonScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val expression = context.get(Keys.expression)
        val notation = context.get(Keys.notation)
        val input = context.get(Keys.input)

        val yield = when (expression) {
            is TopLevelTypeCoercion -> {
                val res = dispatcher.dispatch(
                    NAME,
                    context,
                    Keys.expression.holder(expression.content as TopLevelWeaverExpression),
                    Keys.notation.holder(notation),
                    Keys.input.holder(input)
                )
                val interm = if (res is CommandDispatcher.Result.Failure) {
                    throw res.throwable
                } else {
                    context.get(Keys.yield)
                }
                val resCoerce = dispatcher.dispatch(
                    CoerceCommand.NAME,
                    context,
                    CoerceCommand.Keys.input.holder(interm),
                    CoerceCommand.Keys.coerceTo.holder(expression.type),
                )
                if (resCoerce is CommandDispatcher.Result.Failure) {
                    throw resCoerce.throwable
                } else {
                    context.get(CoerceCommand.Keys.yield)
                }
            }

            is BooleanLiteral -> {
                MutableJsonBoolean(expression.value)
            }

            is NumberLiteral -> {
                MutableJsonNumber(expression.value)
            }

            is StringLiteral -> {
                MutableJsonString(expression.value)
            }

            is TernaryExpression -> {
                val res = dispatcher.dispatch(
                    NAME,
                    context,
                    Keys.expression.holder(expression.condition),
                    Keys.notation.holder(notation),
                    Keys.input.holder(input)
                )
                val interm = if (res is CommandDispatcher.Result.Failure) {
                    throw res.throwable
                } else {
                    context.get(Keys.yield)
                }
                val resCoerce = dispatcher.dispatch(
                    CoerceCommand.NAME,
                    context,
                    CoerceCommand.Keys.input.holder(interm),
                    CoerceCommand.Keys.coerceTo.holder(TypeCoercion.Type.Boolean),
                )
                val condition = if (resCoerce is CommandDispatcher.Result.Failure) {
                    throw resCoerce.throwable
                } else {
                    context.get(CoerceCommand.Keys.yield)
                }
                val result = if (condition.value.jsonPrimitive.booleanOrNull == true) {
                    dispatcher.dispatch(
                        NAME,
                        context,
                        Keys.expression.holder(expression.trueBranch),
                        Keys.notation.holder(notation),
                        Keys.input.holder(input)
                    )
                } else {
                    dispatcher.dispatch(
                        NAME,
                        context,
                        Keys.expression.holder(expression.falseBranch),
                        Keys.notation.holder(notation),
                        Keys.input.holder(input)
                    )
                }
                if (result is CommandDispatcher.Result.Failure) {
                    throw result.throwable
                } else {
                    context.get(Keys.yield)
                }
            }
            is BinaryExpression -> TODO()
            is UnaryExpression -> TODO()
            is FunctionCallExpression -> TODO()
            is NestedExpression -> TODO()
            is WeaverContent -> {
                val newNotationInvocation = expression.resolve(notation.schemaRoot, notation.limboObject)
                val res = dispatcher.dispatch(
                    ObjectNotationCommand.NAME,
                    context,
                    ObjectNotationCommand.Keys.notation.holder(newNotationInvocation),
                    ObjectNotationCommand.Keys.input.holder(input)
                )
                if (res is CommandDispatcher.Result.Failure) {
                    throw res.throwable
                } else {
                    context.get(ObjectNotationCommand.Keys.yield)
                }
            }
        }

        context.put(Keys.yield, yield)
    }
}