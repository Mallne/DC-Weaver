package cloud.mallne.dicentra.weaver.language.ast.expressions

enum class EqualityOperator(override val value: String) : BinaryOperator {
    EQUAL("=="),
    NOT_EQUAL("!="),
    LTE("<="),
    GTE(">="),
    LT("<"),
    GT(">");

    companion object {
        fun byValue(value: String): EqualityOperator? = entries.find { it.value == value }
    }
}