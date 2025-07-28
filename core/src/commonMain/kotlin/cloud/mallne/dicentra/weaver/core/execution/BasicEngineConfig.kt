package cloud.mallne.dicentra.weaver.core.execution

import cloud.mallne.dicentra.weaver.core.model.WeaverEngineConfiguration
import cloud.mallne.dicentra.weaver.core.model.functions.WeaverFunction
import cloud.mallne.dicentra.weaver.core.model.functions.WeaverFunctionConfigScope
import cloud.mallne.dicentra.weaver.core.model.functions.WeaverFunctionInstance
import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPlugin
import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPluginConfigScope
import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPluginInstance

class BasicEngineConfig: WeaverEngineConfiguration {
    val plugins: MutableList<WeaverPluginInstance> = mutableListOf()
    val functions: MutableMap<String, WeaverFunctionInstance> = mutableMapOf()

    override fun <ConfigScope : WeaverPluginConfigScope> install(
        plugin: WeaverPlugin<out ConfigScope>,
        config: ConfigScope.() -> Unit
    ): WeaverPluginInstance {
        val o = plugin.install(config)
        add(o)
        return o
    }

    override fun add(instance: WeaverPluginInstance) {
        plugins.add(instance)
    }

    override fun remove(instance: WeaverPluginInstance) {
        plugins.remove(instance)
    }

    override fun <ConfigScope : WeaverFunctionConfigScope> register(
        function: WeaverFunction<out ConfigScope>,
        config: ConfigScope.() -> Unit
    ): Pair<String, WeaverFunctionInstance> {
        val o = function.install(config)
        add(o.name, o)
        return  o.name to o;
    }

    override fun add(
        name: String,
        instance: WeaverFunctionInstance
    ) {
        functions[name] = instance
    }

    override fun remove(
        name: String,
    ) {
        functions.remove(name)
    }
}