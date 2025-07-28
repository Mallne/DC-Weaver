package cloud.mallne.dicentra.weaver.core

import cloud.mallne.dicentra.weaver.core.execution.BasicEngineConfig
import cloud.mallne.dicentra.weaver.core.execution.WeaverEngine
import cloud.mallne.dicentra.weaver.core.execution.command.CommonCommands
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
        var dispatcher = CommandDispatcher(CommonCommands.baseCommands.associateBy { it.name })
        for (plugin in configScope.plugins) {
            context = plugin.rewriteContext(context)
            dispatcher = plugin.rewriteDispatcher(dispatcher)
        }

        return WeaverEngine(schema = schema, context = context, dispatcher = dispatcher, serializationEngine = json)
    }
}