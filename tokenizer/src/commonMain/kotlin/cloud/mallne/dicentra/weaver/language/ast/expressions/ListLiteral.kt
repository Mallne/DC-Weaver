package cloud.mallne.dicentra.weaver.language.ast.expressions

data class ListLiteral(
    override val value: List<Literal<*>>
) : Literal<List<Literal<*>>>
