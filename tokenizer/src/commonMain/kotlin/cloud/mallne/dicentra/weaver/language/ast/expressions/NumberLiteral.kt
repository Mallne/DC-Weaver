package cloud.mallne.dicentra.weaver.language.ast.expressions

data class NumberLiteral(override val value: Double) : Literal<Double>, TopLevelWeaverExpression