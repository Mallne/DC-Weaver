package cloud.mallne.dicentra.weaver.core.specification

import cloud.mallne.dicentra.weaver.language.WeaverParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WolDslTest {
    @Test
    fun buildsNestedAndComputedIndexAccessors() {
        val path = WolAccessorPath.root(WolIdentifier("hourly"))
            .field(WolIdentifier("temperature_2m"))
            .index(WolIdentifier("index"))

        val accessor = path.asExpression()
        assertEquals("hourly.temperature_2m[{##index##}]", accessor)
        WeaverParser.parseAccessorWeaverObjectLanguage(accessor)
    }

    @Test
    fun buildsReferencesAndEscapedStringLiterals() {
        assertEquals("#weather#", Wol.reference(WolIdentifier("weather")))
        val literal = Wol.stringLiteral("a\"b\\c")
        assertEquals("\"a\\\"b\\\\c\"", literal)
        WeaverParser.parseComputationWeaverObjectLanguage(literal)
        WeaverParser.parseComputationWeaverObjectLanguage(Wol.currentArrayLastIndex)
    }

    @Test
    fun buildsAccessorArithmeticAndFixedArrayIndices() {
        val path = WolAccessorPath.root(WolIdentifier("current"))
            .field(WolIdentifier("wind_speed_10"))
        assertEquals("current.wind_speed_10[0]", path.element(0).asExpression())
        assertEquals("{current.wind_speed_10} / 3.6", Wol.divide(path, 3.6))
        WeaverParser.parseComputationWeaverObjectLanguage(Wol.divide(path, 3.6))
    }

    @Test
    fun buildsStringMappingExpression() {
        val path = WolAccessorPath.root(WolIdentifier("weather"))
            .field(WolIdentifier("condition"))
        val expression = Wol.mapString(path, mapOf("dry" to "Clear", "fog" to "Fog"), "Unknown")

        assertEquals(
            "{weather.condition} == \"dry\" ? \"Clear\" : ({weather.condition} == \"fog\" ? \"Fog\" : (\"Unknown\"))",
            expression,
        )
        WeaverParser.parseComputationWeaverObjectLanguage(expression)
    }

    @Test
    fun rejectsInvalidIdentifiersBeforeBuildingWol() {
        assertFailsWith<IllegalArgumentException> { WolIdentifier("not-valid") }
    }
}
