package cloud.mallne.dicentra.weaver.language.ast.expressions

enum class UnaryOperator(val value: String) {
    NOT("!");

    companion object {
        fun byValue(v:String): UnaryOperator? {
            return entries.find { it.value == v }
        }
    }
}