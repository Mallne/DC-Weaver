package cloud.mallne.dicentra.weaver.core.specification

import cloud.mallne.dicentra.weaver.core.specification.compute.ComputationDeclaration
import cloud.mallne.dicentra.weaver.language.WeaverParser
import cloud.mallne.dicentra.weaver.language.ast.expressions.PathAccessExpr
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class LimboObjectDeclaration(
    val key: String,
    val emptyCase: EmptyCaseHandling = EmptyCaseHandling.Undefined,
    val accessor: AccessorDeclaration,
    val compute: ComputationDeclaration,
    val parameters: Map<String, ObjectType> = emptyMap()
) {
    @Transient
    val accessorAst: PathAccessExpr = WeaverParser.parseAccessorWeaverObjectLanguage(accessor)
    val accessorAst: PathAccessExpr = WeaverParser.parseAccessorWeaverObjectLanguage(accessor)
}