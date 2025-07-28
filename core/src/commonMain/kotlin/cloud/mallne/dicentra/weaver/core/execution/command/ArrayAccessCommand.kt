package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.execution.command.CommonCommands.ScopeKeys.COMMONDENOM
import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.MutableJson
import cloud.mallne.dicentra.weaver.core.model.json.MutableJsonArray
import cloud.mallne.dicentra.weaver.core.specification.compute.expression.resolve
import cloud.mallne.dicentra.weaver.language.ast.expressions.ArrayAccess
import cloud.mallne.dicentra.weaver.language.ast.expressions.ComputedArrayIndexExpr
import cloud.mallne.dicentra.weaver.language.ast.expressions.LiteralArrayIndex
import kotlinx.serialization.json.JsonElement

object ArrayAccessCommand : ConvenientWeaverCommand {
    override val name = "ArrayAccess"

    object Keys {
        val access = WeaverCommandKey(ArrayAccess::class, COMMONDENOM + "ArrayAccess")
        val array = WeaverCommandKey(MutableJsonArray::class, CommonCommands.ScopeKeys.INPUT_MUTABLE_ELEMENT)
        val input = WeaverCommandKey(JsonElement::class, CommonCommands.ScopeKeys.INPUT_ELEMENT)
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, CommonCommands.ScopeKeys.NOTATION)
        val yield = WeaverCommandKey(MutableJson::class, CommonCommands.ScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val access = context.get(Keys.access)
        val input = context.get(Keys.input)
        val array = context.get(Keys.array)
        val notation = context.get(Keys.notation)
        val yield = when (access) {
            is ComputedArrayIndexExpr -> {
                val won = access.indexExpression.resolve(notation.schemaRoot, notation.limboObject)
                ObjectNotationCommand.thisCommand(won, input, context, dispatcher)
            }

            is LiteralArrayIndex -> array[access.index]
        }
        context.log("ArrayAccessCommand:Resolved") {
            debug("Resolved array access $access to $yield")
        }
        context.put(Keys.yield, yield)
    }

    fun thisCommand(
        access: ArrayAccess,
        array: MutableJsonArray,
        input: JsonElement,
        notation: WeaverObjectNotationInvocation,
        context: WeaverContext,
        dispatcher: CommandDispatcher
    ): MutableJson {
        val res = dispatcher.dispatch(
            name,
            context,
            Keys.access.holder(access),
            Keys.array.holder(array),
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