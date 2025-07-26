package cloud.mallne.dicentra.weaver.language.ast.expressions

data class UnnamedParameterList(
    val list: List<TopLevelWeaverExpression> = emptyList()
) : WeaverExpression, List<TopLevelWeaverExpression> by list