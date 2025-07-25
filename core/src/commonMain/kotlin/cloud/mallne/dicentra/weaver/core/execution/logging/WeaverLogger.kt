package cloud.mallne.dicentra.weaver.core.execution.logging

interface WeaverLogger {
    fun error(message: String)
    fun error(message: String, cause: Throwable)
    fun warn(message: String)
    fun warn(message: String, cause: Throwable)
    fun info(message: String)
    fun info(message: String, cause: Throwable)
    fun debug(message: String)
    fun debug(message: String, cause: Throwable)
    fun trace(message: String)
    fun trace(message: String, cause: Throwable)
}
