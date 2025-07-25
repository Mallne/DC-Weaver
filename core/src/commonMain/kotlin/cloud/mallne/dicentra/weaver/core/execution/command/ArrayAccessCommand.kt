package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.CommandDispatcher
import cloud.mallne.dicentra.weaver.core.model.WeaverCommand
import cloud.mallne.dicentra.weaver.core.model.WeaverCommandKey
import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.language.ast.expressions.ArrayAccess
import cloud.mallne.dicentra.weaver.language.ast.expressions.ComputedArrayIndexExpr
import cloud.mallne.dicentra.weaver.language.ast.expressions.ExpressionInterpolation
import cloud.mallne.dicentra.weaver.language.ast.expressions.IdentifierInterpolation
import cloud.mallne.dicentra.weaver.language.ast.expressions.InterpolatedPathPart
import cloud.mallne.dicentra.weaver.language.ast.expressions.LiteralArrayIndex
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement

object ArrayAccessCommand : WeaverCommand {
    const val NAME = "ArrayAccess"
    object Keys {
        val access = WeaverCommandKey(ArrayAccess::class, "ArrayAccess")
        val of = WeaverCommandKey(JsonArray::class, "of.JsonArray")
        val yield = WeaverCommandKey(JsonElement::class, "output.ArrayElement")
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val access = context.get(Keys.access)
        val of = context.get(Keys.of)
        val yield = when (access) {
            is ComputedArrayIndexExpr -> TODO()
            is LiteralArrayIndex -> {

                of[access.index]
            }
        }
        context.log("ArrayAccessCommand:Resolved") {
            debug("Resolved array access $access to $yield")
        }
        context.put(Keys.yield, yield)
    }
}