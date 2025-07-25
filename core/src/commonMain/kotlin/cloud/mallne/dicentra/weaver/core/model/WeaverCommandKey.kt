package cloud.mallne.dicentra.weaver.core.model

import kotlin.reflect.KClass

data class WeaverCommandKey<T : Any>(
    val clazz: KClass<T>,
    val name: String = clazz.qualifiedName ?: "Unknown",
) {
    fun holder(value: T) = DataHolder(this, value)

    data class DataHolder<T : Any>(val key: WeaverCommandKey<T>, val value: T)
}