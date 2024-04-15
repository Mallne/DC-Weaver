package cloud.mallne.dicentra.weaver.model.node

import cloud.mallne.dicentra.weaver.model.WeaverID
import cloud.mallne.dicentra.weaver.model.node.handle.ComputedHandle

interface ComputedNode {
    val id: WeaverID
    val fingerprint: String?
        get() = this::class.simpleName
    val type: NodeType
    val handles: List<ComputedHandle<*>>
    val name: String
    var isDirty: Boolean
    var lastOutput: NodeExecutionResult?
    suspend fun process(data: Any): NodeExecutionResult
}