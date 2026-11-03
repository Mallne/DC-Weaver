package cloud.mallne.dicentra.weaver.exceptions

/** A source object does not contain a property requested by an accessor. */
class WeaverMissingPropertyException(val property: String, val atRoot: Boolean = true) :
    WeaverComputationException("Property not found: $property")
