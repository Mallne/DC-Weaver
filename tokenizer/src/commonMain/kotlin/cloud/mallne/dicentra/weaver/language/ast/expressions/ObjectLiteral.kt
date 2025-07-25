package cloud.mallne.dicentra.weaver.language.ast.expressions

data class ObjectLiteral(
    override val value: Map<String, Literal<*>>
) : Literal<Map<String, Literal<*>>>
