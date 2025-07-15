package cloud.mallne.dicentra.weaver.core.specification

import kotlinx.serialization.Serializable

@Serializable
data class PropertyDeclaration(
    val name: String,
    val value: LimboObjectIdentifier,
)
