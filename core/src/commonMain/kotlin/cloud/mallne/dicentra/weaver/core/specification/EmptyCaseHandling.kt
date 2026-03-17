package cloud.mallne.dicentra.weaver.core.specification

import kotlinx.serialization.Serializable

@Serializable
enum class EmptyCaseHandling {
    Undefined,
    Null
}
