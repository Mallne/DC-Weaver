package cloud.mallne.dicentra.weaver.language.ast.expressions

data class BooleanLiteral(override val value: Boolean) : Literal<Boolean>, TopLevelWeaverExpression