package cloud.mallne.dicentra.weaver.language.ast.expressions

data class BinaryExpression(val left: TopLevelWeaverExpression, val operator: BinaryOperator, val right: TopLevelWeaverExpression) :
    TopLevelWeaverExpression