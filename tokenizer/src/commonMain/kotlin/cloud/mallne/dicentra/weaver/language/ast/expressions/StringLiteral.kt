package cloud.mallne.dicentra.weaver.language.ast.expressions

data class StringLiteral(override val value: String) : Literal<String>, TopLevelWeaverExpression