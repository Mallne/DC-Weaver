package cloud.mallne.dicentra.weaver.model

import cloud.mallne.dicentra.weaver.model.access.Accessor
import cloud.mallne.dicentra.weaver.model.access.Ownership
import cloud.mallne.dicentra.weaver.model.edge.ComputedEdge
import cloud.mallne.dicentra.weaver.model.node.ComputedNode
import cloud.mallne.dicentra.weaver.model.node.ComputedTrigger

interface WeaverWorkspace {
    val name: String
    val description: String?
    val owners: List<Ownership<*>>
    val accessControl: List<Accessor<*>>

    val edges: List<ComputedEdge>
    val nodes: List<ComputedNode>
    val trigger: List<ComputedTrigger>
}