package cloud.mallne.dicentra.weaver.core.specification

enum class ObjectType(val compound: Boolean = false) {
    Boolean,
    String,
    Number,
    List(true),
    Map(true),
    Object(true),
}
