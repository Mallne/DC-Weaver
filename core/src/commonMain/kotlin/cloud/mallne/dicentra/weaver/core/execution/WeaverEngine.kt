package cloud.mallne.dicentra.weaver.core.execution

import cloud.mallne.dicentra.weaver.core.execution.command.ObjectNotationCommand
import cloud.mallne.dicentra.weaver.core.model.CommandDispatcher
import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.core.specification.WeaverSchema
import cloud.mallne.dicentra.weaver.core.model.WeaverObjectNotationInvocation
import io.ktor.utils.io.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer

data class WeaverEngine(
    val schema: WeaverSchema,
    @InternalAPI
    val context: WeaverContext,
    @InternalAPI
    val dispatcher: CommandDispatcher,
    @InternalAPI
    val serializationEngine: Json
) {
    @InternalAPI
    val outerLayout by lazy { schema.getRootLimboObject() }

    @OptIn(InternalAPI::class)
    inline fun <reified Out> execute(input: JsonElement, serializer: KSerializer<Out> = serializer<Out>()): Out? {
        val objectEl = executeElement(input)
        return serializationEngine.decodeFromJsonElement(serializer, objectEl)
    }

    @OptIn(InternalAPI::class)
    fun executeElement(input: JsonElement): JsonElement {
        return executeElement(input, outerLayout)
    }

    /** Executes a resolved entry point without losing its schema scope or argument bindings. */
    @InternalAPI
    fun executeElement(input: JsonElement, entryPoint: WeaverObjectNotationInvocation): JsonElement {
        val outerType = entryPoint.finalType()
        require(outerType != null) { "RootLimboObject must have a final type" }
        val objectObj = ObjectNotationCommand.thisCommand(entryPoint, input, context, dispatcher)
        return objectObj.toStable()
    }
}
