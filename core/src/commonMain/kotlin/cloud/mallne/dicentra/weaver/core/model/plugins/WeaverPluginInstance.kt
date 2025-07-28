package cloud.mallne.dicentra.weaver.core.model.plugins

import cloud.mallne.dicentra.weaver.core.model.CommandDispatcher
import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.core.model.WeaverEngineConfiguration

interface WeaverPluginInstance {
    val configurationBundle: WeaverPluginConfigScope
    val identity: String
    val reconfigure: WeaverEngineConfiguration.() -> Unit
        get() = {}

    fun rewriteContext(context: WeaverContext): WeaverContext = context

    fun rewriteDispatcher(dispatcher: CommandDispatcher) = dispatcher
}