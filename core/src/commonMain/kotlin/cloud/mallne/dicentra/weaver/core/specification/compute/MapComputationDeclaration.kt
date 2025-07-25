package cloud.mallne.dicentra.weaver.core.specification.compute

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.language.WeaverParser
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MapComputationDeclaration(
    val repeats: String = "|{}|ai|",
    val keyLoop: String = "{##index##}",
    val valueLoop: String = "{##value##}"
) : ComputationDeclaration {
    override val type: ObjectType = ObjectType.Map
    @Transient val repeatsAst = WeaverParser.parseComputationWeaverObjectLanguage(repeats)
    @Transient val keyLoopAst = WeaverParser.parseComputationWeaverObjectLanguage(keyLoop)
    @Transient val valueLoopAst = WeaverParser.parseComputationWeaverObjectLanguage(valueLoop)
}