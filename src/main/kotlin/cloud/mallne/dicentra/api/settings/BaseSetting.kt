package cloud.mallne.dicentra.api.settings

interface BaseSetting<T> {
    val key: String
    val value: T
    val forcedByLicense: Boolean
}

