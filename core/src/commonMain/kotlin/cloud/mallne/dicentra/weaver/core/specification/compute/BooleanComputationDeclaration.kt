package cloud.mallne.dicentra.weaver.core.specification.compute

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import kotlinx.serialization.Serializable

@Serializable
data class BooleanComputationDeclaration(
    val bitops: String = "{}"
) : ComputationDeclaration(ObjectType.Boolean)