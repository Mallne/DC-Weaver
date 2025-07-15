package cloud.mallne.dicentra.weaver.core.specification.compute

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import kotlinx.serialization.Serializable

@Serializable
data class NumberComputationDeclaration(
    val template: String = "{}"
) : ComputationDeclaration(ObjectType.Number)