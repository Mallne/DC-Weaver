package cloud.mallne.dicentra.weaver.language.ast.expressions

data class BinaryExpression(val left: WeaverExpression, val operator: BinaryOperator, val right: WeaverExpression) :
    TopLevelWeaverExpression