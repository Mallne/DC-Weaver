package cloud.mallne.dicentra.weaver.core.model.plugins

import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.core.model.WeaverEngineConfiguration

interface WeaverPluginInstance {
    val configurationBundle: WeaverPluginConfigScope
    val identity: String
    val reconfigure: WeaverEngineConfiguration.() -> Unit
    fun rewriteContext(context: WeaverContext): WeaverContext
}