package cloud.mallne.dicentra.weaver.core.model.plugins

interface WeaverPlugin<ConfigScope : WeaverPluginConfigScope> {
    val identity: String
    fun install(config: ConfigScope.() -> Unit): WeaverPluginInstance
}