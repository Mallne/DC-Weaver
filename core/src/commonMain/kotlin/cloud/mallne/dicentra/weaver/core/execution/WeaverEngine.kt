package cloud.mallne.dicentra.weaver.core.execution

import cloud.mallne.dicentra.weaver.core.model.CommandDispatcher
import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.core.model.functions.WeaverFunctionInstance
import cloud.mallne.dicentra.weaver.core.model.json.*
import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPluginInstance
import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.core.specification.WeaverSchema
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer

class WeaverEngine(
    val schema: WeaverSchema,
    val plugins: List<WeaverPluginInstance>,
    val context: WeaverContext,
    val functions: List<WeaverFunctionInstance>,
    val dispatcher: CommandDispatcher,
    val serializationEngine: Json
) {
    inline fun <reified Out> execute(input: JsonElement, serializer: KSerializer<Out> = serializer<Out>()) {
        val outerLayout = schema.getRootLimboObject()
        val outerType = outerLayout.finalType()
        require(outerType != null) { "RootLimboObject must have a final type" }
        outerLayout.limboObject.accessorAst
        when (outerType) {
            ObjectType.Boolean -> MutableJsonBoolean()
            ObjectType.String -> MutableJsonString()
            ObjectType.Number -> MutableJsonNumber()
            ObjectType.List -> MutableJsonArray()
            ObjectType.Map -> MutableJsonObject()
            ObjectType.Object -> MutableJsonObject()
        }


    }
}