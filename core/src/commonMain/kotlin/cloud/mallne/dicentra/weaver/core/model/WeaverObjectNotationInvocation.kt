package cloud.mallne.dicentra.weaver.core.model

import cloud.mallne.dicentra.weaver.core.specification.LimboObjectDeclaration
import cloud.mallne.dicentra.weaver.core.specification.ObjectType
import cloud.mallne.dicentra.weaver.core.specification.ObjectType.Companion.toObjectType
import cloud.mallne.dicentra.weaver.core.specification.WeaverSchema
import cloud.mallne.dicentra.weaver.language.ast.expressions.TypeCoercion

data class WeaverObjectNotationInvocation(
    val limboObject: LimboObjectDeclaration,
    val schemaRoot: WeaverSchema,
    val definedParameters: Map<String, WeaverObjectNotationInvocation> = emptyMap(),
    val parameter: String? = null,
    val coercion: TypeCoercion.Type? = null,
    val metaAccess: MetaAccess = MetaAccess.Result,
) {
    fun finalType(): ObjectType? {
        return when (metaAccess) {
            MetaAccess.Result -> coercion?.toObjectType() ?: limboObject.type
            MetaAccess.Parameter -> limboObject.parameters[parameter]
            else -> null
        }
    }

    enum class MetaAccess {
        Result,
        Accessor,
        Parameter
    }
}