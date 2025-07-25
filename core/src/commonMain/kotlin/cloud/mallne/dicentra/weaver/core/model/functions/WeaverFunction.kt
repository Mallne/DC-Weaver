package cloud.mallne.dicentra.weaver.core.model.functions

interface WeaverFunction<ConfigScope : WeaverPluginConfigScope> {
    val identity: String
    fun install(config: ConfigScope.() -> Unit): WeaverFunctionInstance
}