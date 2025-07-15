package cloud.mallne.dicentra.weaver.core.specification.compute

import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import kotlinx.serialization.Serializable

@Serializable
data class MapComputationDeclaration(
    val repeats: String = "{}",
    val keyLoop: String = "<<index>>",
    val valueLoop: String = "<<value>>"
) : ComputationDeclaration(ObjectType.Map)