package cloud.mallne.dicentra.api.settings

interface KeyValueSetting {
    val key: String
    val value: String
    val forcedByLicense: Boolean
}


