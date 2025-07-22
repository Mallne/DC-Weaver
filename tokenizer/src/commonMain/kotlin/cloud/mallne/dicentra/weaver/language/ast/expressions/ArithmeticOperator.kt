package cloud.mallne.dicentra.weaver.language.ast.expressions

enum class ArithmeticOperator(override val value: String) : BinaryOperator {
    PLUS("+"),
    MINUS("-"),
    MUL("*"),
    DIV("/"),
    MOD("%");

    companion object {
        fun byValue(value: String): ArithmeticOperator? = entries.find { it.value == value }
    }
}