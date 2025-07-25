package cloud.mallne.dicentra.weaver.core.model

import kotlin.reflect.KClass

interface ResolverSpecifier<In, Out> {
    val identifier: String
}