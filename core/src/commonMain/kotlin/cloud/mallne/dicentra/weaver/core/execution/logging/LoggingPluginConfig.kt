package cloud.mallne.dicentra.weaver.core.execution.logging

import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPluginConfigScope

class LoggingPluginConfig : WeaverPluginConfigScope {
    override val silentLoggingTags: MutableList<String> = mutableListOf()
    var logger: WeaverLogger? = null
}