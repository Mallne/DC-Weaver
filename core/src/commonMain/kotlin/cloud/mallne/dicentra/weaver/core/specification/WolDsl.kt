package cloud.mallne.dicentra.weaver.core.specification

import kotlin.jvm.JvmInline

/**
 * A validated WOL identifier. Identifiers start with a letter or `_` and contain only letters,
 * digits, or `_`.
 *
 * Example: `Wol.reference(WolIdentifier("weather"))` stringifies to `#weather#`.
 */
@JvmInline
value class WolIdentifier(val value: String) {
    init {
        require(value.isNotEmpty() && value.first().isWolIdentifierStart()) {
            "WOL identifiers must start with a letter or underscore: '$value'"
        }
        require(value.all { it.isWolIdentifierPart() }) {
            "WOL identifiers may contain only letters, digits, and underscores: '$value'"
        }
    }
}

private fun Char.isWolIdentifierStart() = this == '_' || this in 'a'..'z' || this in 'A'..'Z'

private fun Char.isWolIdentifierPart() = isWolIdentifierStart() || this in '0'..'9'

/**
 * Constructs accessor WOL paths without hand-authoring computed-index delimiters.
 *
 * Examples:
 * ```kotlin
 * Wol.path("current").field("temperature").asExpression() // "current.temperature"
 * Wol.path("hourly").field("temperature").index("index").asExpression()
 * // "hourly.temperature[{##index##}]"
 * Wol.path("items").element(0).asExpression() // "items[0]"
 * ```
 * [asExpression] returns the raw accessor string. [Wol.accessor] wraps that string in WOL accessor
 * braces, producing e.g. `{current.temperature}`.
 */
@JvmInline
value class WolAccessorPath private constructor(private val expression: String) {
    /** Appends one object field, e.g. `current` + `temperature` -> `current.temperature`. */
    fun field(identifier: WolIdentifier) = WolAccessorPath("$expression.${identifier.value}")

    /** String convenience overload of [field]; [name] is validated as a WOL identifier. */
    fun field(name: String) = field(WolIdentifier(name))

    /** Appends a computed index, e.g. `items` + `index` -> `items[{##index##}]`. */
    fun index(parameter: WolIdentifier) = WolAccessorPath("$expression[{##${parameter.value}##}]")

    /** String convenience overload of [index]; [parameter] is validated as a WOL identifier. */
    fun index(parameter: String) = index(WolIdentifier(parameter))

    /** Appends a literal zero-based array index, e.g. `items[0]`. */
    fun element(index: Int) = WolAccessorPath("$expression[$index]")

    /** Returns the raw accessor syntax, without WOL `{...}` braces. */
    fun asExpression(): String = expression

    /** Names inserted through computed-index segments, e.g. `index` in `items[{##index##}]`. */
    val parameters: Set<String>
        get() {
            val names = linkedSetOf<String>()
            var cursor = 0
            while (cursor < expression.length) {
                val start = expression.indexOf("{##", cursor)
                if (start < 0) break
                val end = expression.indexOf("##}", start + 3)
                if (end < 0) break
                names += expression.substring(start + 3, end)
                cursor = end + 3
            }
            return names
        }

    companion object {
        fun root(identifier: WolIdentifier) = WolAccessorPath(identifier.value)

        fun root(name: String) = root(WolIdentifier(name))
    }
}

/**
 * Small builders for common WOL constructs and escaped string literals.
 *
 * These functions return syntax strings. For example, `Wol.divide(Wol.path("speed"), 3.6)`
 * returns `{speed} / 3.6`; it does not calculate a value in Kotlin.
 */
object Wol {
    /** Starts an accessor path from a validated root identifier. */
    fun path(root: String): WolAccessorPath = WolAccessorPath.root(root)

    /** Wraps a raw accessor path in WOL accessor braces: `path` -> `{path}`. */
    fun accessor(path: WolAccessorPath): String = "{${path.asExpression()}}"

    /** Makes a transform reference: `weather` -> `#weather#`. */
    fun reference(identifier: WolIdentifier): String = "#${identifier.value}#"

    /** String convenience overload; validates [key] as a WOL identifier. */
    fun reference(key: String): String = reference(WolIdentifier(key))

    /** Builds a parameterized transform call, stringifying parameter accesses such as `##index##`. */
    fun reference(key: String, parameters: Map<String, String>): String {
        if (parameters.isEmpty()) return reference(key)
        return "#${WolIdentifier(key).value}#" + parameters.entries.joinToString(",", "(", ")") { (name, value) ->
            "${WolIdentifier(name).value}=$value"
        }
    }

    /** Builds WOL division syntax, e.g. `{wind_kmh} / 3.6`. */
    fun divide(path: WolAccessorPath, divisor: Double): String = "${accessor(path)} / $divisor"

    /** Builds division syntax using the current transform accessor, e.g. `{} / 3.6`. */
    fun divideCurrent(divisor: Double): String = "{} / $divisor"

    /** Builds WOL multiplication syntax, e.g. `{solar_mj} * 1000.0`. */
    fun multiply(path: WolAccessorPath, multiplier: Double): String = "${accessor(path)} * $multiplier"

    /** Builds multiplication syntax using the current transform accessor, e.g. `{} * 60.0`. */
    fun multiplyCurrent(multiplier: Double): String = "{} * $multiplier"

    /**
     * Builds a nested WOL equality/ternary expression for mapping strings.
     *
     * For `mapString(path("condition"), mapOf("dry" to "Clear"), "Unknown")`, the returned
     * syntax is `{condition} == "dry" ? "Clear" : ("Unknown")`.
     */
    fun mapString(path: WolAccessorPath, values: Map<String, String>, default: String): String {
        val input = accessor(path)
        return values.entries.toList().foldRight(stringLiteral(default)) { (source, target), fallback ->
            "$input == ${stringLiteral(source)} ? ${stringLiteral(target)} : ($fallback)"
        }
    }

    /** Builds the same mapping expression as [mapString], using the current transform accessor. */
    fun mapCurrentString(values: Map<String, String>, default: String): String =
        values.entries.toList().foldRight(stringLiteral(default)) { (source, target), fallback ->
            "{} == ${stringLiteral(source)} ? ${stringLiteral(target)} : ($fallback)"
        }

    /** Escapes [value] and wraps it in a WOL double-quoted string literal. */
    fun stringLiteral(value: String): String = buildString(value.length + 2) {
        append('"')
        value.forEach { character ->
            when (character) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                else -> append(character)
            }
        }
        append('"')
    }

    /**
     * WOL expression for the last valid zero-based index of the current accessor's array,
     * or `-1` when it is empty. The exact emitted text is `|{}|ai| - 1`.
     */
    const val currentArrayLastIndex: String = "|{}|ai| - 1"
}
