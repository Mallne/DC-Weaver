package cloud.mallne.dicentra.weaver.language.ast.expressions

data class Parameter(
    val name: String,
    val value: WeaverContent // As per your design, parameter values must be weaverContent
) : WeaverExpression