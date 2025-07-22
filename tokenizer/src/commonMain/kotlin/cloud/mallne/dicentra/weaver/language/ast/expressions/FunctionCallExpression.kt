package cloud.mallne.dicentra.weaver.language.ast.expressions

data class FunctionCallExpression(val name: String, val parameters: UnnamedParameterList) : WeaverExpression