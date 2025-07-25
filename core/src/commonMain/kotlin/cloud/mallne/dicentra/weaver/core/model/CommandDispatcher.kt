package cloud.mallne.dicentra.weaver.core.model

import cloud.mallne.dicentra.weaver.exceptions.WeaverComputationException

class CommandDispatcher(
    private val commands: Map<String, WeaverCommand> = emptyMap()
) : Map<String, WeaverCommand> by commands {
    fun dispatch(name: String, context: WeaverContext, vararg inputs: WeaverCommandKey.DataHolder<*>): Result {
        try {
            inputs.forEach { context.put(it.key, it.value) }
            if (!commands.containsKey(name)) {
                context.log("Dispatcher:NoCommand") { error("The CommandDispatcher does not contain a command with name: $name") }
                return Result.Failure(WeaverComputationException("there is no command with name: $name"))
            }
            commands[name]?.execute(context, this)
        } catch (e: Throwable) {
            context.log("Dispatcher:Error") { error("An error occurred while executing the command: $name", e) }
            return Result.Failure(e)
        }
        return Result.Success
    }

    sealed class Result {
        object Success : Result()
        data class Failure(val throwable: Throwable) : Result()
    }
}