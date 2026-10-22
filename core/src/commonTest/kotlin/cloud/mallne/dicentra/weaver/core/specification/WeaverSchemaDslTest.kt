package cloud.mallne.dicentra.weaver.core.specification

import cloud.mallne.dicentra.weaver.core.Weaver
import cloud.mallne.dicentra.weaver.core.specification.compute.ArrayComputationDeclaration
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class WeaverSchemaDslTest {
    private data class Amount(val value: Double?, val aggregation: String, val periodMinutes: Int?)

    @Test
    fun propertyReferencesEmitTheirNamesAndWolExpressions() {
        val amountValue = Wol.path("current").field("precipitation")
        val properties = WeaverObjectBuilder().apply {
            source(Amount::value, amountValue)
            property(Amount::periodMinutes, Wol.reference("aggregation"))
            use(Amount::aggregation, "aggregation")
        }.build()

        assertEquals(listOf("value", "periodMinutes", "aggregation"), properties.map { it.name })
        assertEquals(listOf("#object__source_value_0#", "#aggregation#", "#aggregation#"), properties.map { it.value })
    }

    @Test
    fun buildsAndExecutesAValueSchema() {
        val source = WolAccessorPath.root(WolIdentifier("current"))
            .field(WolIdentifier("temperature"))
        val schema = weaverSchema("weather-contract-v1") {
            root("weather", Wol.reference("temperatureValue"))
            transform("temperatureValue", source.asExpression()) {
                value("{}", ObjectType.Number)
            }
        }

        val result = Weaver().engine(schema).executeElement(
            buildJsonObject { put("current", buildJsonObject { put("temperature", 18) }) },
        )

        assertEquals(JsonPrimitive(18), result)
    }

    @Test
    fun sourceHelperEmitsAWorkingSourceTransform() {
        val schema = weaverSchema("source-v1") {
            root("result", Wol.reference("result"))
            transform("result", "record") {
                objectValue { source("temperature", Wol.path("record").field("temperature")) }
            }
        }

        val result = Weaver().engine(schema).executeElement(
            buildJsonObject { put("record", buildJsonObject { put("temperature", 18) }) },
        )

        assertEquals("{\"temperature\":18.0}", result.toString())
    }

    @Test
    fun buildsArrayComputationsAndRoundTripsSchemas() {
        val schema = weaverSchema("array-v1") {
            root("values", Wol.reference(WolIdentifier("values")))
            transform("item", "items[{##index##}]", parameters = mapOf("index" to ObjectType.Number)) {
                value("{}", ObjectType.Number)
            }
            transform("values", "items") {
                array()
            }
        }

        val decoded = Json.decodeFromString(WeaverSchema.serializer(), Json.encodeToString(WeaverSchema.serializer(), schema))

        assertEquals(schema, decoded)
        assertIs<ArrayComputationDeclaration>(decoded.transforms.single { it.key == "values" }.compute)
    }

    @Test
    fun requiresRootAndComputationDeclarations() {
        assertFailsWith<IllegalStateException> { weaverSchema("missing-root") {} }
        assertFailsWith<IllegalStateException> {
            weaverSchema("missing-computation") {
                root("result", "#missing#")
                transform("missing", "value") {}
            }
        }
    }
}
