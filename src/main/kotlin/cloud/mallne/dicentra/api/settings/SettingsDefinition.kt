package cloud.mallne.dicentra.api.settings

data class SettingsDefinition<T>(
    val key: String,
    val defaultVal: T,
    val forcedByLicense: Boolean = false,
    val requiresRole: Set<String> = setOf(),
) {
    fun to(va: T = defaultVal, forced: Boolean = forcedByLicense): BaseSetting<T> {
        return object : BaseSetting<T> {
            override val key: String = this@SettingsDefinition.key
            override val value: T = va
            override val forcedByLicense: Boolean = forced
        }
    }

    fun toKV(va: T = defaultVal, forced: Boolean = forcedByLicense): KeyValueSetting {
        return object: KeyValueSetting {
            override val forcedByLicense: Boolean = forced
            override val value: String = va.toString()
            override val key: String = this@SettingsDefinition.key
        }
    }
}