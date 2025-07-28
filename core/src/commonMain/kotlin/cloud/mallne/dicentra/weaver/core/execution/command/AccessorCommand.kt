package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.execution.command.CommonCommands.ScopeKeys.COMMONDENOM
import cloud.mallne.dicentra.weaver.core.model.*
import cloud.mallne.dicentra.weaver.core.model.json.MutableJson
import cloud.mallne.dicentra.weaver.core.model.json.MutableJsonArray
import cloud.mallne.dicentra.weaver.core.model.json.MutableJsonProxy
import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.exceptions.WeaverComputationException
import cloud.mallne.dicentra.weaver.language.ast.expressions.PathAccessExpr
import cloud.mallne.dicentra.weaver.language.ast.expressions.TypeCoercion
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object AccessorCommand : ConvenientWeaverCommand     {
    override val name = "Accessor"

    object Keys {
        val input = WeaverCommandKey(JsonElement::class, CommonCommands.ScopeKeys.INPUT_ELEMENT)
        val pathExpression = WeaverCommandKey(PathAccessExpr::class, COMMONDENOM + "Accessor")
        val notation = WeaverCommandKey(WeaverObjectNotationInvocation::class, CommonCommands.ScopeKeys.NOTATION)
        val yield = WeaverCommandKey(MutableJson::class, CommonCommands.ScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val input = context.get(Keys.input)
        val accessor = context.get(Keys.pathExpression)
        val notation = context.get(Keys.notation)
        var pointer: MutableJson = MutableJsonProxy(input)
        for (element in accessor) {
            val path = element.segment.joinToString(separator = "") {
                InterpolatedPartCommand.thisCommand(it, context, dispatcher)
            }
            val pointerType = pointer.type()
            if (path.isNotEmpty()) {
                if (!(pointerType == ObjectType.Object || pointerType == ObjectType.Map)) {
                    throw WeaverComputationException("Cannot access property on non-object value")
                }
                pointer = pointer.asMapOrNull()?.get(path) ?: throw WeaverComputationException("Property not found: $path")
                context.log("AccessorCommand:PathTraversal") {
                    trace("Retrieved property $path to $pointer")
                }
            }

            if (element.arrayAccess != null) {
                if (pointerType != ObjectType.List) {
                    throw WeaverComputationException("Cannot access array index on non-array value")
                }
                pointer = ArrayAccessCommand.thisCommand(element.arrayAccess!!,
                    pointer as MutableJsonArray, input, notation, context, dispatcher)
                context.log("AccessorCommand:ArrayAccess") {
                    debug("Retrieved array index ${element.arrayAccess} to $pointer")
                }
            }
        }
        context.log("AccessorCommand:Resolved") {
            debug("Resolved accessor $accessor to $pointer")
        }
        val yield = pointer
        context.put(Keys.yield, yield)
    }

    fun thisCommand(
        input: JsonElement,
        pathExpression: PathAccessExpr,
        context: WeaverContext,
        dispatcher: CommandDispatcher
    ): MutableJson {
        val res = dispatcher.dispatch(
            name,
            context,
            Keys.input.holder(input),
            Keys.pathExpression.holder(pathExpression),
        )
        return if (res is CommandDispatcher.Result.Failure) {
            throw res.throwable
        } else {
            context.get(Keys.yield)
        }
    }
}