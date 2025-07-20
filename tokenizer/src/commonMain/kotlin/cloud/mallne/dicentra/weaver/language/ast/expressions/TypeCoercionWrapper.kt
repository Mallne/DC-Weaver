package cloud.mallne.dicentra.weaver.language.ast.expressions

data class TypeCoercionWrapper( // This represents the {| content | type |} structure
    val content: WeaverContent, // As per your design, content must be WeaverContent
    val targetType: String      // The IDENTIFIER used for coercion type
) : WeaverContent