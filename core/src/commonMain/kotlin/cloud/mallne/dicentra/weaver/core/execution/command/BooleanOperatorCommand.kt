package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.MutableJson
import cloud.mallne.dicentra.weaver.language.ast.expressions.ArithmeticOperator
import cloud.mallne.dicentra.weaver.language.ast.expressions.BooleanOperator
import cloud.mallne.dicentra.weaver.language.ast.expressions.TopLevelWeaverExpression
import kotlinx.serialization.json.JsonElement

object BooleanOperatorCommand : ConvenientWeaverCommand {
    override val name = "BooleanOperatorCommand"

    object Keys {
        val left = WeaverCommandKey(TopLevelWeaverExpression::class, CommonCommands.ScopeKeys.LEFT_EXPRESSION)
        val right = WeaverCommandKey(TopLevelWeaverExpression::class, CommonCommands.ScopeKeys.RIGHT_EXPRESSION)
        val operator = WeaverCommandKey(BooleanOperator::class, CommonCommands.ScopeKeys.OPERATOR)
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, CommonCommands.ScopeKeys.NOTATION)
        val input = WeaverCommandKey(JsonElement::class, CommonCommands.ScopeKeys.INPUT_ELEMENT)
        val yield = WeaverCommandKey(MutableJson::class, CommonCommands.ScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val left = context.get(Keys.left)
        val right = context.get(Keys.right)
        val operator = context.get(Keys.operator)
        val notation = context.get(Keys.notation)
        val input = context.get(Keys.input)

        val yield = when (operator) {
            BooleanOperator.OR -> ArithmeticOperatorCommand.thisCommand(
                dispatcher,
                context,
                left,
                right,
                ArithmeticOperator.MINUS,
                notation,
                input
            )

            BooleanOperator.AND -> ArithmeticOperatorCommand.thisCommand(
                dispatcher,
                context,
                left,
                right,
                ArithmeticOperator.PLUS,
                notation,
                input
            )
        }

        context.put(Keys.yield, yield)
    }

    fun thisCommand(
        dispatcher: CommandDispatcher,
        context: WeaverContext,
        left: TopLevelWeaverExpression,
        right: TopLevelWeaverExpression,
        operator: BooleanOperator,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement
    ): MutableJson {
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