package cloud.mallne.dicentra.weaver.model.node.handle

import cloud.mallne.dicentra.weaver.model.WeaverID

interface HandleExecutionMetadata<T> {
    val data: T
    val forHandle: WeaverID
}