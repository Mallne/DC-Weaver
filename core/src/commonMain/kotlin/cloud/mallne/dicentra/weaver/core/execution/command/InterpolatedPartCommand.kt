package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.CommandDispatcher
import cloud.mallne.dicentra.weaver.core.model.WeaverCommand
import cloud.mallne.dicentra.weaver.core.model.WeaverCommandKey
import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.language.ast.expressions.ExpressionInterpolation
import cloud.mallne.dicentra.weaver.language.ast.expressions.IdentifierInterpolation
import cloud.mallne.dicentra.weaver.language.ast.expressions.InterpolatedPathPart

object InterpolatedPartCommand : WeaverCommand {
    const val NAME = "InterpolatedPart"
    object Keys {
        val part = WeaverCommandKey(InterpolatedPathPart::class, "InterpolatedPathPart")
        val yield = WeaverCommandKey(String::class, "StringifiedPathMolecule")
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val part = context.get(Keys.part)
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