package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.specification.compute.*
import kotlinx.serialization.json.JsonElement

object ComputationCommand : WeaverCommand {
    const val NAME = "ComputationDeclaration"

    object Keys {
        val computation = WeaverCommandKey(ComputationDeclaration::class, "ComputationDeclaration")
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, "Notation")
        val input = WeaverCommandKey(JsonElement::class, CommonScopeKeys.INPUT_ELEMENT)
        val yield = WeaverCommandKey(String::class, "StringifiedPathMolecule")
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val computation = context.get(Keys.computation)
        val notation = context.get(Keys.notation)
        val input = context.get(Keys.input)

        when (computation) {
            is ComputeDeclaration -> {

            }

            is ArrayComputationDeclaration -> {}
            is MapComputationDeclaration -> {}
            is ObjectComputationDeclaration -> {}
        }
        context.put(Keys.yield, end)
    }
}