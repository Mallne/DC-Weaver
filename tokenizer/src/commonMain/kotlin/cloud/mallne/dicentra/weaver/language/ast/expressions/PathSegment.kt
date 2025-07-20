package cloud.mallne.dicentra.weaver.language.ast.expressions

data class PathSegment(
    val parts: List<InterpolatedPathPart> = emptyList()
): WeaverExpression