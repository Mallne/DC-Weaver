package cloud.mallne.dicentra.weaver.language.ast.expressions

data class TernaryExpression(
    val condition: WeaverExpression,
    val trueBranch: WeaverExpression,
    val falseBranch: WeaverExpression
) : WeaverExpression