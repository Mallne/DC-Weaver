package cloud.mallne.dicentra.weaver.language.ast.expressions

data class TopLevelTypeCoercion(
    // This represents the {| content | type |} structure
    override val content: WeaverExpression, // As per your design, content must be WeaverContent
    override val type: TypeCoercion.Type,
) : TypeCoercion {

    constructor(content: WeaverExpression, targetType: String) : this(
        content = content,
        type = TypeCoercion.Type.getByValue(targetType)
            ?: throw IllegalArgumentException("targetType $targetType not found")
    )
}