package cloud.mallne.dicentra.weaver.core.model

import cloud.mallne.dicentra.weaver.core.execution.logging.WeaverLogger
import cloud.mallne.dicentra.weaver.core.model.plugins.WeaverPluginInstance
import kotlin.reflect.KClass
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class WeaverContext(
    private val logger: WeaverLogger?,
    val plugins: List<WeaverPluginInstance>,
    private val delegationStorage: MutableMap<String, Any?> = mutableMapOf()
) {
    fun hasKey(key: String): Boolean = delegationStorage.containsKey(key)
    fun <T : Any> get(key: WeaverCommandKey<T>): T = get(key.name)!!
    fun <T : Any> getOrNull(key: WeaverCommandKey<T>): T? = get(key.name)
    fun <T : Any> put(key: WeaverCommandKey<out T>, value: T) = put(key.name, value)
    fun <T : Any> put(key: WeaverCommandKey<T>, value: T, forClass: KClass<T>) = put(key.name, value)
    private fun <T> get(key: String): T? = delegationStorage[key] as? T
    private fun <T> put(key: String, value: T) = delegationStorage.put(key, value)

    @OptIn(ExperimentalUuidApi::class)
    fun log(id: String = Uuid.random().toString(), loggerScope: WeaverLogger.() -> Unit) {
        val loggingSuppressions = getAllLoggingSuppressions()
        if (!loggingSuppressions.contains(id)) {
            logger?.loggerScope()
        }
    }

    private fun getAllLoggingSuppressions(): Set<String> {
        val set = mutableSetOf<String>()
        for (plugin in plugins) {
            set.addAll(plugin.configurationBundle.silentLoggingTags)
        }
        return set
    }
}