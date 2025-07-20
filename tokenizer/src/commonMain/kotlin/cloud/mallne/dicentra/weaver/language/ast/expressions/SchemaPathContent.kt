package cloud.mallne.dicentra.weaver.language.ast.expressions

data class SchemaPathContent(
    val path: List<String> = emptyList()
) : WeaverContent