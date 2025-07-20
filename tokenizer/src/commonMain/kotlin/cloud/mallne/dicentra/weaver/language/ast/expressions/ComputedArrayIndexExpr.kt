package cloud.mallne.dicentra.weaver.language.ast.expressions

data class ComputedArrayIndexExpr( // As per your design, this wraps WeaverContent
    val indexExpression: WeaverContent
) : ArrayAccess