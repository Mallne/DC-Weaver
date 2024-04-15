package cloud.mallne.dicentra.weaver.model.node

import cloud.mallne.dicentra.weaver.model.node.handle.HandleExecutionMetadata

interface NodeExecutionResult {
    val state: State
    val output: List<HandleExecutionMetadata<*>>
    val inputs: List<HandleExecutionMetadata<*>>

    enum class State {
        SUCCESS,
        NEEDS_MORE_DATA,
        FAILED
    }
}