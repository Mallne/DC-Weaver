package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.execution.command.CommonCommands.ScopeKeys.COMMONDENOM
import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.MutableJson
import cloud.mallne.dicentra.weaver.exceptions.WeaverComputationException
import cloud.mallne.dicentra.weaver.language.ast.expressions.FunctionCallExpression
import cloud.mallne.dicentra.weaver.language.ast.expressions.InterpolatedPathPart
import kotlinx.serialization.json.JsonElement

typealias MapOfArgs = Map<String, MutableJson>

object FunctionCallCommand : ConvenientWeaverCommand {
    override val name = "FunctionCallCommand"

    object Keys {
        val call = WeaverCommandKey(FunctionCallExpression::class, COMMONDENOM + "FunctionCall")
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, CommonCommands.ScopeKeys.NOTATION)
        val input = WeaverCommandKey(JsonElement::class, CommonCommands.ScopeKeys.INPUT_ELEMENT)
        val yield = WeaverCommandKey(MutableJson::class, CommonCommands.ScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val call = context.get(Keys.call)
        val notation = context.get(Keys.notation)
        val input = context.get(Keys.input)
        val function = context.functions[call.name]
        val params =
            call.parameters.map { WeaverExpressionCommand.thisCommand(dispatcher, context, it, notation, input) }
        if (function == null) {
            throw WeaverComputationException("No function found for name ${call.name}")
        }
        val yield = function.invoke(context, dispatcher, params)

        context.log("$name:Resolved") {
            debug("Resolved function ${call.name} with arguments: $params to Object: $yield")
        }
        context.put(Keys.yield, yield)
    }

    fun thisCommand(
        call: FunctionCallExpression,
        notation: WeaverObjectNotationInvocation,
        input: JsonElement,
        context: WeaverContext,
        dispatcher: CommandDispatcher
    ): MutableJson {
        val res = dispatcher.dispatch(
            name,
            context,
            Keys.call.holder(call),
            Keys.input.holder(input),
            Keys.notation.holder(notation),
        )
        return if (res is CommandDispatcher.Result.Failure) {
            throw res.throwable
        } else {
            context.get(Keys.yield)
        }
    }
}