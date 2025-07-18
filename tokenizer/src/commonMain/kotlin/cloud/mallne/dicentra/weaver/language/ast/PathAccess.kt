package cloud.mallne.dicentra.weaver.language.ast

data class PathAccess(
    val parts: List<String>,
    val arrayIndex: ArrayIndex? = null
) : AccessorExpression