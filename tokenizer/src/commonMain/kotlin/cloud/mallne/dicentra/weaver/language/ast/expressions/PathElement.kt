package cloud.mallne.dicentra.weaver.language.ast.expressions

data class PathElement(
    val segment: PathSegment = PathSegment(),
    val arrayAccess: ArrayAccess? = null
): WeaverExpression