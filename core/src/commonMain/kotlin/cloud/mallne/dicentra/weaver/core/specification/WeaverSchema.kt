package cloud.mallne.dicentra.weaver.core.specification

import cloud.mallne.dicentra.weaver.core.specification.compute.expression.resolve
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class WeaverSchema(
    val key: String,
    val root: PropertyDeclaration,
    val transforms: List<LimboObjectDeclaration>,
    val nested: List<WeaverSchema> = emptyList(),
) {
    fun <T> compute(input: JsonElement): T? {

    }

    fun getRootLimboObject() = root.content.resolve(this)
}