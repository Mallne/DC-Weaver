package cloud.mallne.dicentra.weaver.core.specification.compute

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import kotlinx.serialization.Serializable

@Serializable
data class ArrayComputationDeclaration(
    val repeats: String = "{}",
    val loop: String = "<<value>>",
) : ComputationDeclaration(ObjectType.List)