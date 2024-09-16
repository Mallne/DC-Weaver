package cloud.mallne.dicentra.api.settings

import kotlinx.serialization.Serializable

open class SettingsDefinition<T>(
    val key: String,
    @Serializable
    val defaultVal: T?,
    val forcedByLicense: Boolean = false,
    val requiresRole: Set<String> = setOf(),
) {

    fun toKV(va: T? = defaultVal, forced: Boolean = forcedByLicense): KeyValueSetting<T>? {
        if (va == null) return null
        return object : KeyValueSetting<T> {
            override val forcedByLicense: Boolean = forced
            override val value: T = va
            override val key: String = this@SettingsDefinition.key
        }
    }
}