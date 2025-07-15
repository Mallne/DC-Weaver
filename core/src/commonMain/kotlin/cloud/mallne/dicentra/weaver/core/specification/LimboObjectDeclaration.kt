package cloud.mallne.dicentra.weaver.core.specification

import cloud.mallne.dicentra.weaver.core.specification.compute.ComputationDeclaration
import kotlinx.serialization.Serializable

@Serializable
data class LimboObjectDeclaration(
    val key: String,
    val emptyCase: EmptyCaseHandling = EmptyCaseHandling.Undefined,
    val accessor: AccessorDeclaration,
    val compute: ComputationDeclaration,
    val parameters: Map<String, ObjectType> = emptyMap()
)