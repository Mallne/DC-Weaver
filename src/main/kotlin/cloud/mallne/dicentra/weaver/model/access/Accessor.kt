package cloud.mallne.dicentra.weaver.model.access

import cloud.mallne.dicentra.api.Principal

interface Accessor<T: Principal> : Ownership<T> {
    val canRead: Boolean
    val canWrite: Boolean
    val canDelete: Boolean
    val canEffect: Boolean
}