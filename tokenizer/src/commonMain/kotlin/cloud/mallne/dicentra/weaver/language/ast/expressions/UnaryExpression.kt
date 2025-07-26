package cloud.mallne.dicentra.weaver.language.ast.expressions

data class UnaryExpression(val operator: UnaryOperator, val expression: TopLevelWeaverExpression) : TopLevelWeaverExpression {
    constructor(operatorVal: String, expression: TopLevelWeaverExpression) : this(
        UnaryOperator.Companion.byValue(operatorVal)
            ?: throw IllegalArgumentException("there is no Unary operator '$operatorVal'"),
        expression
    )
}