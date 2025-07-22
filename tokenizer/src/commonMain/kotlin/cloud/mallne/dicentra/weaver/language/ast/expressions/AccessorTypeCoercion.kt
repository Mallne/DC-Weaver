package cloud.mallne.dicentra.weaver.language.ast.expressions

data class AccessorTypeCoercion(
    // This represents the {| content | type |} structure
    override val content: WeaverContent, // As per your design, content must be WeaverContent
    override val type: TypeCoercion.Type,
) : TypeCoercion, WeaverContent {

    constructor(content: WeaverContent, targetType: String) : this(
        content = content,
        type = TypeCoercion.Type.getByValue(targetType)
            ?: throw IllegalArgumentException("targetType $targetType not found")
    )
}