package cloud.mallne.dicentra.weaver.language.ast.expressions

data class ExpressionInterpolation( // Note: As per your design, this wraps WeaverContent
    val expression: WeaverContent
) : InterpolatedPathPart