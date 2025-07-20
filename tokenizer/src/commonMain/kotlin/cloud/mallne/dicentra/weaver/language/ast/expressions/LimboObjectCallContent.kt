package cloud.mallne.dicentra.weaver.language.ast.expressions

import cloud.mallne.dicentra.weaver.language.ast.expressions.SchemaPathContent

data class LimboObjectCallContent(
    val schemaPath: SchemaPathContent = SchemaPathContent(),
    val limboObject: String,
    val parameters: ParameterList = ParameterList() // If it has parameters
) : WeaverContent