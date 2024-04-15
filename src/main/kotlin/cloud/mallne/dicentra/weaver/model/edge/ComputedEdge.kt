package cloud.mallne.dicentra.weaver.model.edge

import cloud.mallne.dicentra.weaver.model.WeaverID

interface ComputedEdge {
    val id: WeaverID
    val source: Waypoint
    val target: Waypoint
}