package cloud.mallne.dicentra.weaver.language.ast

data class SchemaNestedCall(val schemaName: String, val nestedName: String) : AccessorExpression