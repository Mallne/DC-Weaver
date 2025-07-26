package cloud.mallne.dicentra.weaver.core.specification

import cloud.mallne.dicentra.weaver.language.ast.expressions.TypeCoercion

enum class ObjectType(val compound: Boolean = false) {
    Boolean,
    String,
    Number,
    Undefined,
    List(true),
    Map(true),
    Object(true);

    companion object {
        fun TypeCoercion.Type.toObjectType(): ObjectType {
            return when (this) {
                TypeCoercion.Type.String -> String
                TypeCoercion.Type.Boolean -> Boolean
                TypeCoercion.Type.Integer -> Number
                TypeCoercion.Type.Float -> Number
                TypeCoercion.Type.AvailabilityBoolean -> Boolean
                TypeCoercion.Type.AvailabilityInteger -> Number
            }
        }
    }
}
