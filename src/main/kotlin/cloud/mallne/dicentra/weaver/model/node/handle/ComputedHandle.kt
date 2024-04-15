package cloud.mallne.dicentra.weaver.model.node.handle

import cloud.mallne.dicentra.weaver.model.WeaverID

interface ComputedHandle<Type> {
    val id: WeaverID
    val connectionLimit: Int
    val dataFilter: DataFilter<Type>
    suspend fun pipe(data: Type): HandleExecutionMetadata<Type>
}