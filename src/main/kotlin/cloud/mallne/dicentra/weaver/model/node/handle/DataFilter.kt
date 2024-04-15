package cloud.mallne.dicentra.weaver.model.node.handle

interface DataFilter<T> {
    val dataAllowed: List<T>
    fun filter(vararg data: T): List<T>
    fun canProcess(data: T) : Boolean
}