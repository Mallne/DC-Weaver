package cloud.mallne.dicentra.weaver.language.ast.expressions

data class UnaryExpression(val operator: UnaryOperator, val expression: WeaverExpression) : TopLevelWeaverExpression {
    constructor(operatorVal: String, expression: WeaverExpression) : this(
        UnaryOperator.Companion.byValue(operatorVal)
            ?: throw IllegalArgumentException("there is no Unary operator '$operatorVal'"),
        expression
    )
}