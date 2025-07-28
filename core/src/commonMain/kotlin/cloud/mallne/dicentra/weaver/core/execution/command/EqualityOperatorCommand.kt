package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.MutableJson
import cloud.mallne.dicentra.weaver.language.ast.expressions.EqualityOperator
import cloud.mallne.dicentra.weaver.language.ast.expressions.TopLevelWeaverExpression
import kotlinx.serialization.json.JsonElement

object EqualityOperatorCommand : ConvenientWeaverCommand {
    override val name = "EqualityOperatorCommand"

    object Keys {
        val left = WeaverCommandKey(TopLevelWeaverExpression::class, CommonCommands.ScopeKeys.LEFT_EXPRESSION)
        val right = WeaverCommandKey(TopLevelWeaverExpression::class, CommonCommands.ScopeKeys.RIGHT_EXPRESSION)
        val operator = WeaverCommandKey(EqualityOperator::class, CommonCommands.ScopeKeys.OPERATOR)
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, CommonCommands.ScopeKeys.NOTATION)
        val input = WeaverCommandKey(JsonElement::class, CommonCommands.ScopeKeys.INPUT_ELEMENT)
        val yield = WeaverCommandKey(Boolean::class, CommonCommands.ScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    private fun compareContents(
        left: MutableJson,
        right: MutableJson,
        operator: EqualityOperator,
    ): Boolean {
        val cmp = left.compareTo(right)
        return when (operator) {
            EqualityOperator.EQUAL -> cmp == 0
            EqualityOperator.NOT_EQUAL -> cmp != 0
            EqualityOperator.LTE -> cmp <= 0
            EqualityOperator.GTE -> cmp >= 0
            EqualityOperator.LT -> cmp < 0
            EqualityOperator.GT -> cmp > 0
        }
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val left = context.get(Keys.left)
        val right = context.get(Keys.right)
        val operator = context.get(Keys.operator)
        val notation = context.get(Keys.notation)
        val input = context.get(Keys.input)

        val leftRes = WeaverExpressionCommand.thisCommand(dispatcher, context, left, notation, input)
        val rightRes = WeaverExpressionCommand.thisCommand(dispatcher, context, right, notation, input)

        val yield = compareContents(leftRes, rightRes, operator)

        context.put(Keys.yield, yield)
    }

    fun thisCommand(
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        left: TopLevelWeaverExpression,
        right: TopLevelWeaverExpression,
        operator: EqualityOperator,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): Boolean {
        val res = dispatcher.dispatch(
            name,
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