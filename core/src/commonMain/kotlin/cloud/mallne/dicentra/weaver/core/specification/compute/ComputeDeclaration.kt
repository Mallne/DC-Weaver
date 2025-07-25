package cloud.mallne.dicentra.weaver.core.specification.compute

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.language.WeaverParser
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ComputeDeclaration(
    val compute: String = "{}",
    override val type: ObjectType = ObjectType.String,
): ComputationDeclaration {
    init {
        require(!type.compound) { "Compute type must not be a compound, use List, Map or Object Types instead." }
    }
    @Transient
    val computeAst = WeaverParser.parseComputationWeaverObjectLanguage(compute)
}