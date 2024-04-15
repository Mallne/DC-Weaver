package cloud.mallne.dicentra.weaver.model.access

import cloud.mallne.dicentra.api.Principal

interface  Ownership<T : Principal> {
    val principal: T
}