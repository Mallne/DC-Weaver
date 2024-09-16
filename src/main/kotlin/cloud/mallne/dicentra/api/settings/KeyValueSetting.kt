package cloud.mallne.dicentra.api.settings

import kotlinx.serialization.Serializable


interface KeyValueSetting<T> {
    @Serializable
    val key: String

    @Serializable
    val value: T

    @Serializable
    val forcedByLicense: Boolean
}


