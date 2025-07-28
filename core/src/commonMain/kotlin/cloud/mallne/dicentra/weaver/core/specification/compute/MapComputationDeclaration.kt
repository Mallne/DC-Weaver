package cloud.mallne.dicentra.weaver.core.specification.compute

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.language.WeaverParser
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MapComputationDeclaration(
    val loop: String = "|{}|ai|",
    val keyRepeat: String = "{##index##}",
    val valueRepeat: String = "{##value##}"
) : ComputationDeclaration {
    override val type: ObjectType = ObjectType.Map
    @Transient
    val loopAst = WeaverParser.parseComputationWeaverObjectLanguage(loop)
    @Transient
    val keyRepeatAst = WeaverParser.parseComputationWeaverObjectLanguage(keyRepeat)
    @Transient
    val valueRepeatAst = WeaverParser.parseComputationWeaverObjectLanguage(valueRepeat)
}