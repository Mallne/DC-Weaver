package cloud.mallne.dicentra.weaver.core.specification.compute.expression

import cloud.mallne.dicentra.weaver.core.model.WeaverObjectNotationInvocation
import cloud.mallne.dicentra.weaver.core.specification.LimboObjectDeclaration
import cloud.mallne.dicentra.weaver.core.specification.WeaverSchema
import cloud.mallne.dicentra.weaver.exceptions.WeaverComputationException
import cloud.mallne.dicentra.weaver.language.ast.expressions.*

@Throws(NullPointerException::class)
fun WeaverContent.resolve(
    root: WeaverSchema,
    rootLimboObject: LimboObjectDeclaration? = null
): WeaverObjectNotationInvocation {
    when (this) {
        is CurrentAccessor -> {
            return rootLimboObject?.let {
                WeaverObjectNotationInvocation(
                    limboObject = it,
                    schemaRoot = root,
                    metaAccess = WeaverObjectNotationInvocation.MetaAccess.Accessor
                )
            }
                ?: throw WeaverComputationException("You are trying to resolve a CurrentAccessor on a null rootLimboObject")
        }

        is SchemaPathContent -> {
            return this.resolveToSchema(root).getRootLimboObject()
        }

        is LimboObjectCallContent -> {
            val base = this.schemaPath.resolveToSchema(root)
            val lo = base.transforms.find { it.key == this.limboObject }
                ?: throw WeaverComputationException("LimboObject not found: ${this.limboObject} in ${base.key}")
            val p = this.parameters.list.associate { (name, value) -> name to value.resolve(root, rootLimboObject) }
            return WeaverObjectNotationInvocation(limboObject = lo, schemaRoot = base, definedParameters = p)
        }

        is AccessorTypeCoercion -> {
            val base = this.content.resolve(root, rootLimboObject)
            return base.copy(coercion = this.type)
        }

        is ParameterAccessContent -> {
            val rlo = rootLimboObject ?: root.getRootLimboObject().limboObject
            if (rlo.parameters.none { it.key == this.paramName }) {
                throw WeaverComputationException("Parameter not found: ${this.paramName} in ${rlo.key}")
            }
            return WeaverObjectNotationInvocation(schemaRoot = root, limboObject = rlo, parameter = this.paramName, metaAccess = WeaverObjectNotationInvocation.MetaAccess.Parameter)
        }
    }
}

private fun SchemaPathContent.resolveToSchema(root: WeaverSchema): WeaverSchema {
    var accessSchema: WeaverSchema = root
    for ((index, path) in this.path.withIndex()) {
        accessSchema = if (index == 0 && root.key == path) {
            //the first Path is the Root Schema
            root
        } else {
            accessSchema.nested.find { it.key == path }
                ?: throw WeaverComputationException("Schema Path not found: $path in ${accessSchema.key}")
        }
    }
    return accessSchema
}