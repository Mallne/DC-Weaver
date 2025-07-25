package cloud.mallne.dicentra.weaver.core.execution.logging

import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPlugin
import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPluginInstance

object LoggingPlugin : WeaverPlugin<LoggingPluginConfig> {
    override val identity: String = "DC-WV-CORE-Logging"

    override fun install(config: LoggingPluginConfig.() -> Unit): WeaverPluginInstance {
        val pluginConfig = LoggingPluginConfig()
        config.invoke(pluginConfig)
        return LoggingPluginInstance(
            configurationBundle = pluginConfig,
            identity = identity,
        )
    }
}