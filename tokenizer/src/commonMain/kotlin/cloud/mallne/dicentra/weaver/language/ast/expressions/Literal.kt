package cloud.mallne.dicentra.weaver.language.ast.expressions

sealed interface Literal<T>: WeaverExpression {
    val value: T
}