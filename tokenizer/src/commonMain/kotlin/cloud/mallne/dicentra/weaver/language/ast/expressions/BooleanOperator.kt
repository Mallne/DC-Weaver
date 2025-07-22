package cloud.mallne.dicentra.weaver.language.ast.expressions

enum class BooleanOperator(
    override val value: String
) : BinaryOperator {
    AND("&&"),
    OR("||");

    companion object {
        fun byValue(value: String): BooleanOperator? = entries.find { it.value == value }
    }
}