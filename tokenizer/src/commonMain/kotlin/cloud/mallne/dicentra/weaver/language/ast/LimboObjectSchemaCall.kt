package cloud.mallne.dicentra.weaver.language.ast

data class LimboObjectSchemaCall(val schemaName: String, val limboKey: String) : AccessorExpression