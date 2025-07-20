package cloud.mallne.dicentra.weaver.language.ast.expressions

data class ParameterList(
    val list: List<Parameter> = emptyList()
) : WeaverExpression