package cloud.mallne.dicentra.weaver.model.edge

import cloud.mallne.dicentra.weaver.model.WeaverID

interface Waypoint {
    val node: WeaverID
    val handle: WeaverID
}