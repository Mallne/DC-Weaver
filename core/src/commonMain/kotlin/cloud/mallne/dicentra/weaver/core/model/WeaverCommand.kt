package cloud.mallne.dicentra.weaver.core.model

interface WeaverCommand {
    fun execute(context: WeaverContext, dispatcher: CommandDispatcher)
}