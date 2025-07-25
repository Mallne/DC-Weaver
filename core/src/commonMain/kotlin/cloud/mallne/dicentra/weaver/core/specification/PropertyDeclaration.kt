package cloud.mallne.dicentra.weaver.core.specification

import cloud.mallne.dicentra.weaver.language.ast.expressions.WeaverContent
import kotlinx.serialization.Serializable

@Serializable
data class PropertyDeclaration(
    val name: String,
    val value: LimboObjectIdentifier,
) {
    val content: WeaverContent = value.weaverContent()
}
