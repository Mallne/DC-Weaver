package cloud.mallne.dicentra.weaver.core

import cloud.mallne.dicentra.weaver.core.execution.BasicEngineConfig
import cloud.mallne.dicentra.weaver.core.execution.WeaverEngine
import cloud.mallne.dicentra.weaver.core.model.CommandDispatcher
import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.core.model.WeaverEngineConfiguration
import cloud.mallne.dicentra.weaver.core.specification.WeaverSchema
import kotlinx.serialization.json.Json

class Weaver(
    private val json: Json = Json,
) {
    fun engine(schema: WeaverSchema, config: WeaverEngineConfiguration.() -> Unit = {}): WeaverEngine {
        val configScope = BasicEngineConfig()
        config.invoke(configScope)
        for (plugin in configScope.plugins) {
            plugin.reconfigure.invoke(configScope)
        }
        var context = WeaverContext(logger = null, plugins = configScope.plugins, functions = configScope.functions)
        for (plugin in configScope.plugins) {
            context = plugin.rewriteContext(context)
        }
        return WeaverEngine(schema = schema, context = context, dispatcher = CommandDispatcher(), serializationEngine = json)
    }
}