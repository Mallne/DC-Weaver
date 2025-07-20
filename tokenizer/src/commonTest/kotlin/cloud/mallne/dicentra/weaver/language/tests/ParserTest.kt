package cloud.mallne.dicentra.weaver.language.tests

import cloud.mallne.dicentra.weaver.language.WeaverParser
import cloud.mallne.dicentra.weaver.language.ast.expressions.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {
    @Test
    fun testSimpleDotPath() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("menu.id")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(
                            listOf(
                                IdentifierInterpolation("menu"),
                            )
                        )
                    ),
                    PathElement(
                        PathSegment(
                            listOf(
                                IdentifierInterpolation("id")
                            )
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testSchemaInterpolation() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("{otherSchema}")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(
                            listOf(
                                ExpressionInterpolation(
                                    SchemaPathContent(listOf("otherSchema"))
                                )
                            )
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testMixedPathWithInterpolation() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("me{nuitem}s.nested")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(
                            listOf(
                                IdentifierInterpolation("me"),
                                ExpressionInterpolation(
                                    SchemaPathContent(listOf("nuitem"))
                                ),
                                IdentifierInterpolation("s")
                            )
                        )
                    ),
                    PathElement(
                        PathSegment(
                            listOf(
                                IdentifierInterpolation("nested")
                            )
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testNestedSchemaInterpolation() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("{otherSchema.nested}")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(
                            listOf(
                                ExpressionInterpolation(
                                    SchemaPathContent(listOf("otherSchema", "nested"))
                                )
                            )
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testLimboObjectInterpolation() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("{<menuKey>}")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(
                            listOf(
                                ExpressionInterpolation(
                                    LimboObjectCallContent(limboObject = "menuKey")
                                )
                            )
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testLimboObjectSchemaInterpolation() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("{otherSchema.<menuKey>}")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(
                            listOf(
                                ExpressionInterpolation(
                                    LimboObjectCallContent(
                                        schemaPath = SchemaPathContent(listOf("otherSchema")),
                                        limboObject = "menuKey"
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testParameterAccess() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("{<<param>>}")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(
                            listOf(
                                ExpressionInterpolation(
                                    ParameterAccessContent("param")
                                )
                            )
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testLimboObjectWithParameters() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("{<menuKey>(param=<menuValue>)}")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(
                            listOf(
                                ExpressionInterpolation(
                                    LimboObjectCallContent(
                                        limboObject = "menuKey",
                                        parameters = ParameterList(
                                            listOf(
                                                Parameter(
                                                    "param",
                                                    LimboObjectCallContent(limboObject = "menuValue")
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testArrayIndexLiteral() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("menu.popup.menuitem[1]")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("menu"))),
                    ),
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("popup"))),
                    ),
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("menuitem"))),
                        arrayAccess = LiteralArrayIndex(1)
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testArrayIndexDynamic() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("menu.popup.menuitem[{<indexvalue>}]")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("menu"))),
                    ),
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("popup"))),
                    ),
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("menuitem"))),
                        arrayAccess = ComputedArrayIndexExpr(
                            LimboObjectCallContent(limboObject = "indexvalue")
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testArrayIndexTypeCoercion() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("menu.popup.menuitem[{|<indexvalue>|i|}]")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("menu"))),
                    ),
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("popup"))),
                    ),
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("menuitem"))),
                        arrayAccess = ComputedArrayIndexExpr(
                            TypeCoercionWrapper(
                                LimboObjectCallContent(limboObject = "indexvalue"),
                                "i"
                            )
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testComplexPathWithParam() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("menu.{otherschema.nested}.menuitem[{<<param>>}]")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("menu")))
                    ),
                    PathElement(
                        PathSegment(
                            listOf(
                                ExpressionInterpolation(
                                    SchemaPathContent(listOf("otherschema", "nested"))
                                )
                            )
                        )
                    ),
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("menuitem"))),
                        arrayAccess = ComputedArrayIndexExpr(
                            ParameterAccessContent("param")
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testParameterAccessWithPath() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("{<<param>>}.menu.nested")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(
                            listOf(
                                ExpressionInterpolation(
                                    ParameterAccessContent("param")
                                )
                            )
                        )
                    ),
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("menu")))
                    ),
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("nested")))
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testComplexSchemaAccessWithParam() {
        val result =
            WeaverParser.parseAccessorWeaverObjectLanguage("menu.{otherschema.<limbo>(param=<<parameter>>)}[{<limboval>}]")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("menu")))
                    ),
                    PathElement(
                        PathSegment(
                            listOf(
                                ExpressionInterpolation(
                                    LimboObjectCallContent(
                                        schemaPath = SchemaPathContent(listOf("otherschema")),
                                        limboObject = "limbo",
                                        parameters = ParameterList(
                                            listOf(
                                                Parameter("param", ParameterAccessContent("parameter"))
                                            )
                                        )
                                    )
                                )
                            )
                        ),
                        arrayAccess = ComputedArrayIndexExpr(
                            LimboObjectCallContent(limboObject = "limboval")
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testNestedComplexAccess() {
        val result =
            WeaverParser.parseAccessorWeaverObjectLanguage("menu.{otherschema.<limbo>(param=<<parameter>>)}[{<limboval>}].nesteable")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("menu")))
                    ),
                    PathElement(
                        PathSegment(
                            listOf(
                                ExpressionInterpolation(
                                    LimboObjectCallContent(
                                        schemaPath = SchemaPathContent(listOf("otherschema")),
                                        limboObject = "limbo",
                                        parameters = ParameterList(
                                            listOf(
                                                Parameter("param", ParameterAccessContent("parameter"))
                                            )
                                        )
                                    )
                                )
                            )
                        ),
                        arrayAccess = ComputedArrayIndexExpr(
                            LimboObjectCallContent(limboObject = "limboval")
                        )
                    ),
                    PathElement(
                        PathSegment(listOf(IdentifierInterpolation("nesteable")))
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testNestedParameterAccess() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("{otherschema.<limbo>(param=menu.id.<schema>)}")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        PathSegment(
                            listOf(
                                ExpressionInterpolation(
                                    LimboObjectCallContent(
                                        schemaPath = SchemaPathContent(listOf("otherschema")),
                                        limboObject = "limbo",
                                        parameters = ParameterList(
                                            listOf(
                                                Parameter(
                                                    "param",
                                                    LimboObjectCallContent(
                                                        schemaPath = SchemaPathContent(listOf("menu", "id")),
                                                        limboObject = "schema"
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            result
        )
    }

    @Test
    fun testArrayInterpolation() {
        val result = WeaverParser.parseAccessorWeaverObjectLanguage("[{otherschema.<limbo>(param=menu.id.<schema>)}]")
        assertEquals(
            PathAccessExpr(
                listOf(
                    PathElement(
                        arrayAccess = ComputedArrayIndexExpr(
                            LimboObjectCallContent(
                                schemaPath = SchemaPathContent(listOf("otherschema")),
                                limboObject = "limbo",
                                parameters = ParameterList(
                                    listOf(
                                        Parameter(
                                            "param",
                                            LimboObjectCallContent(
                                                schemaPath = SchemaPathContent(listOf("menu", "id")),
                                                limboObject = "schema"
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            result
        )
    }
}