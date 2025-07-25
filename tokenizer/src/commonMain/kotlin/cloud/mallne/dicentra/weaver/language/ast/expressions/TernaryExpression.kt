package cloud.mallne.dicentra.weaver.language.ast.expressions

data class TernaryExpression(
    val condition: TopLevelWeaverExpression,
    val trueBranch: TopLevelWeaverExpression,
    val falseBranch: TopLevelWeaverExpression
) : TopLevelWeaverExpression