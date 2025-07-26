package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.*
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
        val yield = WeaverCommandKey(MutableJson::class, CommonScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val expression = context.get(Keys.expression)
        val notation = context.get(Keys.notation)
        val input = context.get(Keys.input)

        val yield = when (expression) {
            is TopLevelTypeCoercion -> coerceExpression(dispatcher, context, expression, notation, input)

            is BooleanLiteral -> {
                MutableJsonBoolean(expression.value)
            }

            is NumberLiteral -> {
                MutableJsonNumber(expression.value)
            }

            is StringLiteral -> {
                MutableJsonString(expression.value)
            }

            is TernaryExpression -> ternaryExpression(dispatcher, context, expression, notation, input)
            is BinaryExpression -> binaryExpression(dispatcher, context, expression, notation, input)
            is UnaryExpression -> unaryExpression(dispatcher, context, expression, notation, input)
            is FunctionCallExpression -> TODO()
            is NestedExpression -> nestedExpression(dispatcher, context, expression, notation, input)
            is WeaverContent -> weaverContentExpression(expression, notation, dispatcher, context, input)
        }

        context.put(Keys.yield, yield)
    }

    private fun binaryExpression(
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        expression: BinaryExpression,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJson {
        return when (expression.operator) {
            is ArithmeticOperator -> ArithmeticOperatorCommand.thisCommand(
                dispatcher,
                context,
                expression.left,
                expression.right,
                expression.operator as ArithmeticOperator,
                notation,
                input
            )

            is BooleanOperator -> BooleanOperatorCommand.thisCommand(
                dispatcher,
                context,
                expression.left,
                expression.right,
                expression.operator as BooleanOperator,
                notation,
                input
            )

            is EqualityOperator -> EqualityOperatorCommand.thisCommand(
                dispatcher,
                context,
                expression.left,
                expression.right,
                expression.operator as EqualityOperator,
                notation,
                input
            )
        }
    }

    private fun unaryExpression(
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        expression: UnaryExpression,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJsonProxy {
        val interm = thisCommand(dispatcher, context, expression, notation, input)
        return when (expression.operator) {
            UnaryOperator.NOT -> {
                val bool = CoerceCommand.thisCommand(dispatcher, context, interm, TypeCoercion.Type.Boolean)
                if (bool.value.jsonPrimitive.booleanOrNull == true) {
                    MutableJsonBoolean(false)
                } else {
                    MutableJsonBoolean(true)
                }
            }
        }
    }

    private fun coerceExpression(
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        expression: TopLevelTypeCoercion,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJsonProxy {
        val interm = thisCommand(dispatcher, context, expression, notation, input)
        return CoerceCommand.thisCommand(dispatcher, context, interm, expression.type)
    }

    fun thisCommand(
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        expression: TopLevelWeaverExpression,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJson {
        val res = dispatcher.dispatch(
            NAME,
            context,
            Keys.expression.holder(expression),
            Keys.notation.holder(notation),
            Keys.input.holder(input)
        )
        return if (res is CommandDispatcher.Result.Failure) {
            throw res.throwable
        } else {
            context.get(Keys.yield)
        }
    }

    private fun ternaryExpression(
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        expression: TernaryExpression,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJson {
        val interm = thisCommand(dispatcher, context, expression.condition, notation, input)
        val condition = CoerceCommand.thisCommand(dispatcher, context, interm, TypeCoercion.Type.Boolean)
        val result = if (condition.value.jsonPrimitive.booleanOrNull == true) {
            thisCommand(dispatcher, context, expression.trueBranch, notation, input)
        } else {
            thisCommand(dispatcher, context, expression.falseBranch, notation, input)
        }
        return result
    }

    private fun nestedExpression(
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        expression: NestedExpression,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJson {
        return thisCommand(dispatcher, context, expression.expr, notation, input)
    }

    private fun weaverContentExpression(
        expression: WeaverContent,
        notation: WeaverObjectNotationInvocation,
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        input: JsonElement
    ): MutableJson {
        val newNotationInvocation = expression.resolve(notation.schemaRoot, notation.limboObject)
        val res = dispatcher.dispatch(
            ObjectNotationCommand.NAME,
            context,
            ObjectNotationCommand.Keys.notation.holder(newNotationInvocation),
            ObjectNotationCommand.Keys.input.holder(input)
        )
        return if (res is CommandDispatcher.Result.Failure) {
            throw res.throwable
        } else {
            context.get(ObjectNotationCommand.Keys.yield)
        }
    }
}