package cloud.mallne.dicentra.weaver.core.model.functions

import cloud.mallne.dicentra.weaver.core.model.CommandDispatcher
import cloud.mallne.dicentra.weaver.core.model.WeaverContext
import cloud.mallne.dicentra.weaver.core.model.WeaverEngineConfiguration
import cloud.mallne.dicentra.weaver.core.model.json.MutableJson

interface WeaverFunctionInstance {
    val name: String
    fun invoke(context: WeaverContext, dispatcher: CommandDispatcher, args: List<MutableJson>): MutableJson
}