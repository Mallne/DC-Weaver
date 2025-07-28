package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.execution.command.CommonCommands.ScopeKeys.COMMONDENOM
import cloud.mallne.dicentra.weaver.core.model.CommandDispatcher
import cloud.mallne.dicentra.weaver.core.model.ConvenientWeaverCommand
import cloud.mallne.dicentra.weaver.core.model.WeaverCommand
import cloud.mallne.dicentra.weaver.core.model.WeaverCommandKey
import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.core.model.WeaverObjectNotationInvocation
import cloud.mallne.dicentra.weaver.core.model.json.MutableJson
import cloud.mallne.dicentra.weaver.core.specification.compute.expression.resolve
import cloud.mallne.dicentra.weaver.language.ast.expressions.ExpressionInterpolation
import cloud.mallne.dicentra.weaver.language.ast.expressions.IdentifierInterpolation
import cloud.mallne.dicentra.weaver.language.ast.expressions.InterpolatedPathPart
import cloud.mallne.dicentra.weaver.language.ast.expressions.TypeCoercion
import kotlinx.serialization.json.JsonElement

object InterpolatedPartCommand : ConvenientWeaverCommand {
    override val name = "InterpolatedPart"

    object Keys {
        val part = WeaverCommandKey(InterpolatedPathPart::class, COMMONDENOM + "InterpolatedPathPart")
        val input = WeaverCommandKey(JsonElement::class, CommonCommands.ScopeKeys.INPUT_ELEMENT)
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, CommonCommands.ScopeKeys.NOTATION)
        val yield = WeaverCommandKey(String::class, COMMONDENOM + "StringifiedPathMolecule")
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val part = context.get(Keys.part)
        val input = context.get(Keys.input)
        val notation = context.get(Keys.notation)
        val end = when (part) {
            is IdentifierInterpolation -> part.identifier
            is ExpressionInterpolation -> {
                val won = part.expression.resolve(notation.schemaRoot, notation.limboObject)
                val onc = ObjectNotationCommand.thisCommand(won, input, context, dispatcher)
                onc.asStringOrNull() ?: CoerceCommand.thisCommand(dispatcher, context, onc, TypeCoercion.Type.String).toString()
            }
        }
        context.log("InterpolatedPartCommand:Resolved") {
            debug("Resolved interpolated part $part to $end")
        }
        context.put(Keys.yield, end)
    }

    fun thisCommand(
        part: InterpolatedPathPart,
        context: WeaverContext,
        dispatcher: CommandDispatcher
    ): String {
        val res = dispatcher.dispatch(
            name,
            context,
            Keys.part.holder(part)
        )
        return if (res is CommandDispatcher.Result.Failure) {
            throw res.throwable
        } else {
            context.get(Keys.yield)
        }
    }
}