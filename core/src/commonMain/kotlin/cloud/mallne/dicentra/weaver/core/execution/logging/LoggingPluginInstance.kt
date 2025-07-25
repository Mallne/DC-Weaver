package cloud.mallne.dicentra.weaver.core.execution.logging

import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.core.model.WeaverEngineConfiguration
import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPluginInstance

class LoggingPluginInstance(
    override val configurationBundle: LoggingPluginConfig,
    override val identity: String,
    override val reconfigure: WeaverEngineConfiguration.() -> Unit = {},
) : WeaverPluginInstance {
    override fun rewriteContext(context: WeaverContext): WeaverContext {
        return context.copy(
            logger = configurationBundle.logger,
        )
    }
}