package cloud.mallne.dicentra.weaver.language.ast.expressions

data class UnnamedParameterList(
    val list: List<WeaverExpression> = emptyList()
) : WeaverExpression