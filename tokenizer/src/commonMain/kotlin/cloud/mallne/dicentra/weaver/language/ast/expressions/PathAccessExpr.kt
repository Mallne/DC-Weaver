package cloud.mallne.dicentra.weaver.language.ast.expressions

data class PathAccessExpr(
    val elements: List<PathElement>
) : WeaverExpression