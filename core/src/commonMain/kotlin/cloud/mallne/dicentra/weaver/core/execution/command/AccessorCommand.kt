package cloud.mallne.dicentra.weaver.core.execution.command

import cloud.mallne.dicentra.weaver.core.model.CommandDispatcher
import cloud.mallne.dicentra.weaver.core.model.WeaverCommand
import cloud.mallne.dicentra.weaver.core.model.WeaverCommandKey
import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.core.model.json.MutableJson
import cloud.mallne.dicentra.weaver.core.model.json.MutableJsonProxy
import cloud.mallne.dicentra.weaver.exceptions.WeaverComputationException
import cloud.mallne.dicentra.weaver.language.ast.expressions.PathAccessExpr
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

object AccessorCommand : WeaverCommand {
    const val NAME = "Accessor"

    object Keys {
        val input = WeaverCommandKey(JsonElement::class, "input.JsonElement")
        val pathExpression = WeaverCommandKey(PathAccessExpr::class, "Accessor")
        val yield = WeaverCommandKey(MutableJson::class, CommonScopeKeys.OUTPUT_MUTABLE_ELEMENT)
    }

    override fun execute(context: WeaverContext, dispatcher: CommandDispatcher) {
        val input = context.get(Keys.input)
        val accessor = context.get(Keys.pathExpression)
        var pointer: JsonElement = input
        for (element in accessor) {
            val path = element.segment.joinToString(separator = "") {
                val result = dispatcher.dispatch(
                    InterpolatedPartCommand.NAME,
                    context,
                    InterpolatedPartCommand.Keys.part.holder(it)
                )
                if (result is CommandDispatcher.Result.Failure) {
                    throw result.throwable
                } else {
                    context.get(InterpolatedPartCommand.Keys.yield)
                }
            }
            if (path.isNotEmpty()) {
                if (pointer !is JsonObject) {
                    throw WeaverComputationException("Cannot access property on non-object value")
                }
                pointer = pointer[path] ?: throw WeaverComputationException("Property not found: $path")
                context.log("AccessorCommand:PathTraversal") {
                    debug("Retrieved property $path to $pointer")
                }
            }

            if (element.arrayAccess != null) {
                if (pointer !is JsonArray) {
                    throw WeaverComputationException("Cannot access array index on non-array value")
                }
                val result = dispatcher.dispatch(ArrayAccessCommand.NAME, context, ArrayAccessCommand.Keys.of.holder(pointer), ArrayAccessCommand.Keys.access.holder(element.arrayAccess!!))
                pointer = if (result is CommandDispatcher.Result.Failure) {
                    throw result.throwable
                } else {
                    context.get(ArrayAccessCommand.Keys.yield)
                }
                context.log("AccessorCommand:ArrayAccess") {
                    debug("Retrieved array index ${element.arrayAccess} to $pointer")
                }
            }
        }
        context.log("AccessorCommand:Resolved") {
            debug("Resolved accessor $accessor to $pointer")
        }
        val yield = MutableJsonProxy(pointer)
        context.put(Keys.yield, yield)
    }
}