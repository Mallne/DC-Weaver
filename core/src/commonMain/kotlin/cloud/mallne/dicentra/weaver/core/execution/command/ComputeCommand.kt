package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.MutableJson
import cloud.mallne.dicentra.weaver.core.model.json.MutableJsonProxy
import cloud.mallne.dicentra.weaver.core.specification.compute.*
import cloud.mallne.dicentra.weaver.language.ast.expressions.ExpressionInterpolation
import cloud.mallne.dicentra.weaver.language.ast.expressions.IdentifierInterpolation
import kotlinx.serialization.json.JsonElement

object ComputeCommand : WeaverCommand {
    const val NAME = "ComputeDeclaration"

    object Keys {
        val computation = WeaverCommandKey(ComputeDeclaration::class, "ComputeDeclaration")
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, "Notation")
        val input = WeaverCommandKey(JsonElement::class, CommonScopeKeys.INPUT_ELEMENT)
        val yield = WeaverCommandKey(MutableJsonProxy::class, CommonScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val computation = context.get(Keys.computation)
        val notation = context.get(Keys.notation)
        val input = context.get(Keys.input)

        computation.computeAst

        context.put(Keys.yield, end)
    }
}