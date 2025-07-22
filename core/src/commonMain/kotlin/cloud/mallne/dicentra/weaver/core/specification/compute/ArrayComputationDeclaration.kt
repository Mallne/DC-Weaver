package cloud.mallne.dicentra.weaver.core.specification.compute

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import kotlinx.serialization.Serializable

@Serializable
data class ArrayComputationDeclaration(
    val repeats: String = "{##value##}",
    val loop: String = "|{}|ai|",
) : ComputationDeclaration {
    override val type: ObjectType = ObjectType.List
}