package cloud.mallne.dicentra.weaver.core.model.functions

interface WeaverFunction<ConfigScope : WeaverFunctionConfigScope> {
    val name: String
    fun install(config: ConfigScope.() -> Unit): WeaverFunctionInstance
}