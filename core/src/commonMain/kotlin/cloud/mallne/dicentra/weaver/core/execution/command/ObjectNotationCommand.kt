package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.MutableJson
import cloud.mallne.dicentra.weaver.language.ast.expressions.ExpressionInterpolation
import cloud.mallne.dicentra.weaver.language.ast.expressions.IdentifierInterpolation
import kotlinx.serialization.json.JsonElement

object ObjectNotationCommand : WeaverCommand {
    const val NAME = "VisitObjectNotation"

    object Keys {
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, "Notation")
        val input = WeaverCommandKey(JsonElement::class, CommonScopeKeys.INPUT_ELEMENT)
        val yield = WeaverCommandKey(MutableJson::class, CommonScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val notation = context.get(Keys.notation)
        val input = context.get(Keys.input)
        val accessor = notation.limboObject.accessorAst
        val res = dispatcher.dispatch(
            AccessorCommand.NAME,
            context,
            AccessorCommand.Keys.input.holder(input),
            AccessorCommand.Keys.pathExpression.holder(accessor)
        )
        if (res is CommandDispatcher.Result.Failure) {
            throw res.throwable
        } else {
            context.get(AccessorCommand.Keys.yield)
        }
        context.log("ObjectNotationCommand:Accessor") {
            debug("Resolved accessor $accessor to ${context.get(AccessorCommand.Keys.yield)}")
        }
        schema.getRootLimboObject()
        val end = when (part) {
            is IdentifierInterpolation -> part.identifier
            is ExpressionInterpolation -> TODO()
        }
        context.log("InterpolatedPartCommand:Resolved") {
            debug("Resolved interpolated part $part to $end")
        }
        context.put(Keys.yield, end)
    }
}