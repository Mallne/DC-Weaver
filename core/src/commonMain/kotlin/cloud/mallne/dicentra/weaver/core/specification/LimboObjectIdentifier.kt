package cloud.mallne.dicentra.weaver.core.specification

import cloud.mallne.dicentra.weaver.language.WeaverParser

typealias LimboObjectIdentifier = String

fun LimboObjectIdentifier.weaverContent() = WeaverParser.parseWeaverObjectNotation(this)