package cloud.mallne.dicentra.weaver.core.model

import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPlugin
import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPluginConfigScope
import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPluginInstance

interface WeaverEngineConfiguration {
    fun <ConfigScope : WeaverPluginConfigScope> install(
        plugin: WeaverPlugin<out ConfigScope>,
        config: ConfigScope.() -> Unit = {}
    ): WeaverPluginInstance

    fun add(instance: WeaverPluginInstance)
    fun remove(instance: WeaverPluginInstance)
}