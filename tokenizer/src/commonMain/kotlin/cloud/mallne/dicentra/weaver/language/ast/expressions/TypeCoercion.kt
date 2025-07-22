package cloud.mallne.dicentra.weaver.language.ast.expressions

sealed interface TypeCoercion: WeaverExpression {
    // This represents the {| content | type |} structure
    val content: WeaverExpression // As per your design, content must be WeaverContent
    val type: Type

    enum class Type(val value: List<String>) {
        Integer(listOf("integer", "int", "i")),
        Float(listOf("float", "f")),
        String(listOf("string", "s")),
        Boolean(listOf("boolean", "bool", "b")),
        AvailabilityBoolean(listOf("available", "ab")),
        AvailabilityInteger(listOf("magnitude", "ai"));

        companion object {
            fun getByValue(value: String): Type? {
                for (type in entries) {
                    if (type.value.contains(value)) {
                        return type
                    }
                }
                return null
            }
        }
    }
}