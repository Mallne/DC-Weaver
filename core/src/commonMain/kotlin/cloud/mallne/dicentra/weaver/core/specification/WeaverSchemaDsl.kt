package cloud.mallne.dicentra.weaver.core.specification

import cloud.mallne.dicentra.weaver.core.specification.compute.ArrayComputationDeclaration
import cloud.mallne.dicentra.weaver.core.specification.compute.ComputeDeclaration
import cloud.mallne.dicentra.weaver.core.specification.compute.ComputationDeclaration
import cloud.mallne.dicentra.weaver.core.specification.compute.ObjectComputationDeclaration
import kotlin.reflect.KProperty1

/**
 * Prevents accidentally resolving a DSL call against an outer Weaver builder scope.
 *
 * This annotation affects Kotlin receiver resolution only; it does not alter the generated
 * [WeaverSchema] or WOL text.
 */
@DslMarker
annotation class WeaverSchemaDsl

/**
 * Builds a [WeaverSchema] from root, transform, object, and array declarations.
 *
 * A schema has exactly one root. The root value is WOL text, usually a reference to a declared
 * transform, such as `#report#`. Each [WeaverSchemaBuilder.transform] declares a named operation
 * and its source accessor; its body chooses one computation (`value`, `objectValue`, or `array`).
 *
 * Example:
 * ```kotlin
 * val input = Wol.path("current").field("temperature")
 * val schema = weaverSchema("weather-v1") {
 *     root("weather", Wol.reference("temperature"))
 *     transform("temperature", input.asExpression()) {
 *         value("{}", ObjectType.Number)
 *     }
 * }
 * ```
 * The important strings in the resulting declaration are `root.value == "#temperature#"`,
 * the transform accessor `current.temperature`, and the value expression `{}`. These strings
 * are WOL syntax, not Kotlin expressions; the DSL constructs declarations but does not evaluate
 * or type-check WOL expressions at Kotlin compile time.
 */
fun weaverSchema(key: String, block: WeaverSchemaBuilder.() -> Unit): WeaverSchema =
    WeaverSchemaBuilder(key).apply(block).build()

/**
 * Builds one reusable transform declaration outside a [weaverSchema] block.
 *
 * [accessor] is WOL accessor text (for example `current.temperature`); [parameters] declares
 * names used by computed accessors such as `items[{##index##}]`. The block must declare exactly
 * one computation.
 */
fun weaverTransform(
    key: String,
    accessor: String,
    emptyCase: EmptyCaseHandling = EmptyCaseHandling.Undefined,
    parameters: Map<String, ObjectType> = emptyMap(),
    block: WeaverTransformBuilder.() -> Unit,
): LimboObjectDeclaration = WeaverTransformBuilder(key, accessor, emptyCase, parameters).apply(block).build()

@WeaverSchemaDsl
/** Collects one schema's root and declarations before creating the immutable schema value. */
class WeaverSchemaBuilder internal constructor(private val key: String) {
    private var root: PropertyDeclaration? = null
    private val transforms = mutableListOf<LimboObjectDeclaration>()
    private val nested = mutableListOf<WeaverSchema>()

    /**
     * Sets the single output root. [value] is retained verbatim as WOL text; for example
     * `Wol.reference("report")` produces `#report#`.
     */
    fun root(name: String, value: String) {
        check(root == null) { "A Weaver schema can declare only one root" }
        root = PropertyDeclaration(name, value)
    }

    /**
     * Declares a transform identified by [key], run against the JSON location in [accessor].
     *
     * [accessor] must be WOL accessor syntax. A parameterized array-item accessor can be written
     * with [WolAccessorPath], e.g. `Wol.path("items").index("index").asExpression()`, which
     * produces `items[{##index##}]`; in that case declare `parameters = mapOf("index" to
     * ObjectType.Number)`. The block emits one value, object, or array computation.
     */
    fun transform(
        key: String,
        accessor: String,
        emptyCase: EmptyCaseHandling = EmptyCaseHandling.Undefined,
        parameters: Map<String, ObjectType> = emptyMap(),
        block: WeaverTransformBuilder.() -> Unit,
    ) {
        val builder = WeaverTransformBuilder(key, accessor, emptyCase, parameters).apply(block)
        transforms += builder.build()
        transforms += builder.generatedTransforms
    }

    /** Adds prebuilt transform declarations without changing their WOL strings. */
    fun include(transforms: Iterable<LimboObjectDeclaration>) {
        this.transforms += transforms
    }

    /** Adds a prebuilt transform declaration. */
    fun transform(declaration: LimboObjectDeclaration) {
        transforms += declaration
    }

    /** Includes another schema as a nested schema declaration. */
    fun nested(schema: WeaverSchema) {
        nested += schema
    }

    internal fun build(): WeaverSchema = WeaverSchema(
        key = key,
        root = checkNotNull(root) { "A Weaver schema must declare a root" },
        transforms = transforms.toList(),
        nested = nested.toList(),
    )
}

@WeaverSchemaDsl
/** Defines exactly one computation for a transform. */
class WeaverTransformBuilder internal constructor(
    private val key: String,
    private val accessor: String,
    private val emptyCase: EmptyCaseHandling,
    private val parameters: Map<String, ObjectType>,
) {
    private var computation: ComputationDeclaration? = null
    internal val generatedTransforms = mutableListOf<LimboObjectDeclaration>()

    /**
     * Declares an object result. Every call to [WeaverObjectBuilder.property], [WeaverObjectBuilder.source],
     * or [WeaverObjectBuilder.use] becomes one output property declaration.
     */
    fun objectValue(block: WeaverObjectBuilder.() -> Unit) {
        check(computation == null) { "A Weaver transform can declare only one computation" }
        computation = ObjectComputationDeclaration(
            WeaverObjectBuilder(key, parameters, generatedTransforms).apply(block).build(),
        )
    }

    /**
     * Declares a scalar/computed result. [expression] is WOL text and [type] is its declared
     * Weaver result type. For a direct source accessor, use the WOL accessor expression `{}`;
     * for example a transform accessor of `current.temperature` plus `value("{}", Number)`
     * reads that location.
     */
    fun value(expression: String, type: ObjectType) {
        check(computation == null) { "A Weaver transform can declare only one computation" }
        computation = ComputeDeclaration(expression, type)
    }

    /**
     * Declares an array computation. [repeat] is the WOL item expression and [loop] is the WOL
     * iteration expression. Defaults `{##value##}` and `|{}|ai|` respectively mean “evaluate
     * each item” and “iterate from zero through the current array's last index”.
     */
    fun array(repeat: String = "{##value##}", loop: String = "|{}|ai|") {
        check(computation == null) { "A Weaver transform can declare only one computation" }
        computation = ArrayComputationDeclaration(repeat, loop)
    }

    internal fun build(): LimboObjectDeclaration = LimboObjectDeclaration(
        key = key,
        emptyCase = emptyCase,
        accessor = accessor,
        compute = checkNotNull(computation) { "Transform '$key' must declare a computation" },
        parameters = parameters,
    )
}

@WeaverSchemaDsl
/** Builds object properties. String arguments are emitted as WOL/output declaration text. */
class WeaverObjectBuilder internal constructor(
    private val ownerKey: String = "object",
    private val ownerParameters: Map<String, ObjectType> = emptyMap(),
    private val generatedTransforms: MutableList<LimboObjectDeclaration> = mutableListOf(),
) {
    private val properties = mutableListOf<PropertyDeclaration>()
    private var sourceIndex = 0

    /**
     * Emits an output property named [name] whose expression is [expression], verbatim.
     *
     * Example: `property("periodMinutes", "60")` creates
     * `PropertyDeclaration(name = "periodMinutes", value = "60")`.
     */
    fun property(name: String, expression: String) {
        properties += PropertyDeclaration(name, expression)
    }

    /**
     * Property-reference overload of [property]. Only [target]'s Kotlin name is used; its
     * receiver/type is not serialized or inspected. Thus `WeatherAmount::aggregation` is
     * equivalent to `property("aggregation", expression)`, and the emitted property key is
     * exactly the string `aggregation`.
     */
    fun property(target: KProperty1<*, *>, expression: String) = property(target.name, expression)

    /**
     * Creates a generated source transform for [path] and emits a reference to it as [name].
     * For `Wol.path("current").field("temp")`, the schema contains a source transform with
     * accessor `current.temp`, computation `{}`, and an output property referencing that transform.
     * Computed-index parameters (for example `index`) are copied to the generated transform and
     * forwarded as `##index##` arguments. [type] declares the scalar type used to coerce the
     * source value; it defaults to [ObjectType.Number].
     */
    fun source(
        name: String,
        path: WolAccessorPath,
        type: ObjectType = ObjectType.Number,
        coercion: String? = null,
    ) {
        val generatedKey = "${ownerKey}__source_${name}_${sourceIndex++}"
        val referencedParameters = path.parameters.associateWith { "##$it##" }
        val coercionType = coercion ?: when (type) {
            ObjectType.Boolean -> "b"
            ObjectType.String -> "s"
            ObjectType.Number -> "f"
            else -> error("Source transforms only support scalar output types, got $type")
        }
        generatedTransforms += LimboObjectDeclaration(
            key = generatedKey,
            emptyCase = EmptyCaseHandling.Undefined,
            accessor = path.asExpression(),
            compute = ComputeDeclaration("|{}|$coercionType|", type),
            parameters = path.parameters.associateWith { ObjectType.Number },
        )
        property(name, Wol.reference(generatedKey, referencedParameters))
    }

    /** Property-reference overload of [source]; the Kotlin property name becomes the output key.
     */
    fun source(
        target: KProperty1<*, *>,
        path: WolAccessorPath,
        type: ObjectType = ObjectType.Number,
        coercion: String? = null,
    ) = source(target.name, path, type, coercion)

    /** Emits a reference to another transform. `use("amount", "amountTransform")` emits the
     * property key `amount` and WOL value `#amountTransform#`.
     */
    fun use(name: String, transform: WolIdentifier) = property(name, Wol.reference(transform))

    /** Emits a call to [transform], forwarding the specified current transform parameters. */
    fun use(name: String, transform: String, parameters: Map<String, String>) =
        property(name, Wol.reference(transform, parameters))

    /** Property-reference overload of [use]; the referenced property's Kotlin name becomes the
     * output key, while the transform reference is stringified as `#key#`.
     */
    fun use(target: KProperty1<*, *>, transform: WolIdentifier) = use(target.name, transform)

    /** String-key overload of [use]. The [transform] string is validated as a WOL identifier. */
    fun use(name: String, transform: String) = use(name, WolIdentifier(transform))

    /**
     * Property-reference overload of [use]. For example,
     * `use(WeatherAmount::aggregation, "aggregationSum")` emits the equivalent declaration
     * `PropertyDeclaration(name = "aggregation", value = "#aggregationSum#")`.
     */
    fun use(target: KProperty1<*, *>, transform: String) = use(target.name, transform)

    internal fun build(): List<PropertyDeclaration> = properties.toList()
}
