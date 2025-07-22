package cloud.mallne.dicentra.weaver.core.specification.compute

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.core.specification.PropertyDeclaration
import kotlinx.serialization.Serializable

@Serializable
data class ObjectComputationDeclaration(
    val nested: List<PropertyDeclaration>? = null
) : ComputationDeclaration {
    override val type: ObjectType = ObjectType.Object
}