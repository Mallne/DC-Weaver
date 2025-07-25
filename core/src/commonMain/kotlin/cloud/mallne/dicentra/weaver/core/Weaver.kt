package cloud.mallne.dicentra.weaver.core

import cloud.mallne.dicentra.weaver.core.model.WeaverEngineConfiguration
import cloud.mallne.dicentra.weaver.core.specification.WeaverSchema
import kotlinx.serialization.json.Json

class Weaver(
    val json: Json = Json
) {
    fun engine(schema: WeaverSchema, config: WeaverEngineConfiguration.()->Unit = {}){}
}