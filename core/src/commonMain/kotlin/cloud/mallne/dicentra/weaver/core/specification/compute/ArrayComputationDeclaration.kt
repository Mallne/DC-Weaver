package cloud.mallne.dicentra.weaver.core.specification.compute

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.language.WeaverParser
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ArrayComputationDeclaration(
    val repeats: String = "{##value##}",
    val loop: String = "|{}|ai|",
) : ComputationDeclaration {
    override val type: ObjectType = ObjectType.List
    @Transient val repeatsAst = WeaverParser.parseComputationWeaverObjectLanguage(repeats)
    @Transient val loopAst = WeaverParser.parseComputationWeaverObjectLanguage(loop)
}