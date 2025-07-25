package cloud.mallne.dicentra.weaver.core.model

import kotlin.reflect.KClass



interface TypedResolver<In, Out> {
    fun resolve(input: In): Out
}