package cloud.mallne.dicentra.weaver.core.specification.compute

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.core.specification.PropertyDeclaration
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ObjectComputationDeclaration(
    val nested: List<PropertyDeclaration>? = null
) : ComputationDeclaration {
    @SerialName("resultType") override val type: ObjectType = ObjectType.Object
}
