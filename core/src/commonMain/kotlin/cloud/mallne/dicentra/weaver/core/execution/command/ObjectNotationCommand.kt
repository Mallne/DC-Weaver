package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.execution.command.CommonCommands.ScopeKeys.PARAMDENOM
import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.MutableJson
import cloud.mallne.dicentra.weaver.core.model.json.MutableJsonNull
import cloud.mallne.dicentra.weaver.core.specification.EmptyCaseHandling
import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.exceptions.WeaverMissingPropertyException
import kotlinx.serialization.json.JsonElement

object ObjectNotationCommand : ConvenientWeaverCommand {
    override val name = "VisitObjectNotation"

    object Keys {
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, CommonCommands.ScopeKeys.NOTATION)
        val input = WeaverCommandKey(JsonElement::class, CommonCommands.ScopeKeys.INPUT_ELEMENT)
        val yield = WeaverCommandKey(MutableJson::class, CommonCommands.ScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val notation = context.get(Keys.notation)
        val input = context.get(Keys.input)
        val metaType = notation.metaAccess
        val yield = when (metaType) {
            WeaverObjectNotationInvocation.MetaAccess.Accessor -> {
                val accessor = notation.limboObject.accessorAst
                AccessorCommand.thisCommand(input, accessor, context, dispatcher)
            }
            WeaverObjectNotationInvocation.MetaAccess.Result -> {
                //precompute Params
                notation.definedParameters.forEach { (key, content) ->
                    val kv = thisCommand(content, input, context, dispatcher)
                    context.put(WeaverCommandKey(MutableJson::class, PARAMDENOM + key), kv)
                }
                if (notation.limboObject.emptyCase == EmptyCaseHandling.Null) {
                    val value = try {
                        AccessorCommand.thisCommand(input, notation.limboObject.accessorAst, context, dispatcher)
                    } catch (_: WeaverMissingPropertyException) {
                        null
                    }
                    if (value == null || value.type() == ObjectType.Undefined) MutableJsonNull()
                    else ComputationCommand.thisCommand(notation.limboObject.compute, input, notation, context, dispatcher)
                } else {
                    ComputationCommand.thisCommand(notation.limboObject.compute, input, notation, context, dispatcher)
                }
            }
            WeaverObjectNotationInvocation.MetaAccess.Parameter -> {
                context.get(WeaverCommandKey(MutableJson::class, PARAMDENOM + notation.parameter!!))
            }
        }

        context.log("ObjectNotation:Resolved") {
            debug("Resolved Object Notation to $yield")
        }
        context.put(Keys.yield, yield)
    }

    fun thisCommand(
        notation: WeaverObjectNotationInvocation,
        input: JsonElement,
        context: WeaverContext,
        dispatcher: CommandDispatcher
    ): MutableJson {
        val res = dispatcher.dispatch(
            name,
            context,
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
