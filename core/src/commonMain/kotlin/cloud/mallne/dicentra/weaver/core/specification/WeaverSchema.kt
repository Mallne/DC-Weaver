package cloud.mallne.dicentra.weaver.core.specification

import cloud.mallne.dicentra.weaver.core.specification.compute.expression.resolve
import kotlinx.serialization.Serializable

@Serializable
data class WeaverSchema(
    val key: String,
    val root: PropertyDeclaration,
    val transforms: List<LimboObjectDeclaration>,
    val nested: List<WeaverSchema> = emptyList(),
) {
    fun getRootLimboObject() = root.content.resolve(this)
}