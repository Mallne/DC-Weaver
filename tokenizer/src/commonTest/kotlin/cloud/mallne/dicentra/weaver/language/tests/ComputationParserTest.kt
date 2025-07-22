package cloud.mallne.dicentra.weaver.language.tests

import cloud.mallne.dicentra.weaver.language.WeaverParser
import cloud.mallne.dicentra.weaver.language.ast.expressions.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ComputationParserTest {
    @Test
    fun testStringLiteral() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("'Hello World'")
        assertEquals(
            StringLiteral("Hello World"),
            value
        )
    }

    @Test
    fun testNumberLiteral() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("123")
        assertEquals(
            NumberLiteral(123.0),
            value
        )
    }

    @Test
    fun testBooleanTrue() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("true")
        assertEquals(
            BooleanLiteral(true),
            value
        )
    }

    @Test
    fun testBooleanFalse() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("false")
        assertEquals(
            BooleanLiteral(false),
            value
        )
    }

    @Test
    fun testDecimalNumber() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("0.5")
        assertEquals(
            NumberLiteral(0.5),
            value
        )
    }

    @Test
    fun testAddition() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("1 + 2")
        assertEquals(
            BinaryExpression(
                left = NumberLiteral(1.0),
                ArithmeticOperator.PLUS,
                right = NumberLiteral(2.0)
            ),
            value
        )
    }

    @Test
    fun testSubtraction() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("3 - 1.5")
        assertEquals(
            BinaryExpression(
                left = NumberLiteral(3.0),
                ArithmeticOperator.MINUS,
                right = NumberLiteral(1.5)
            ),
            value
        )
    }

    @Test
    fun testMultiplication() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("4 * 5")
        assertEquals(
            BinaryExpression(
                left = NumberLiteral(4.0),
                ArithmeticOperator.MUL,
                right = NumberLiteral(5.0)
            ),
            value
        )
    }

    @Test
    fun testDivision() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("10 / 2")
        assertEquals(
            BinaryExpression(
                left = NumberLiteral(10.0),
                ArithmeticOperator.DIV,
                right = NumberLiteral(2.0)
            ),
            value
        )
    }

    @Test
    fun testModulo() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("7 % 3")
        assertEquals(
            BinaryExpression(
                left = NumberLiteral(7.0),
                ArithmeticOperator.MOD,
                right = NumberLiteral(3.0)
            ),
            value
        )
    }

    @Test
    fun testBooleanNot() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("!true")
        assertEquals(
            UnaryExpression(
                UnaryOperator.NOT,
                BooleanLiteral(true)
            ),
            value
        )
    }

    @Test
    fun testNestedNot() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("!(1 == 2)")
        assertEquals(
            UnaryExpression(
                UnaryOperator.NOT,
                NestedExpression(
                    BinaryExpression(
                        left = NumberLiteral(1.0),
                        EqualityOperator.EQUAL,
                        right = NumberLiteral(2.0)
                    )
                )
            ),
            value
        )
    }

    @Test
    fun testLogicalAnd() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("true && false")
        assertEquals(
            BinaryExpression(
                left = BooleanLiteral(true),
                BooleanOperator.AND,
                right = BooleanLiteral(false)
            ),
            value
        )
    }

    @Test
    fun testLogicalOr() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("true || false")
        assertEquals(
            BinaryExpression(
                left = BooleanLiteral(true),
                BooleanOperator.OR,
                right = BooleanLiteral(false)
            ),
            value
        )
    }

    @Test
    fun testEquality() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("1 == 1")
        assertEquals(
            BinaryExpression(
                left = NumberLiteral(1.0),
                EqualityOperator.EQUAL,
                right = NumberLiteral(1.0)
            ),
            value
        )
    }

    @Test
    fun testInequality() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("'a' != 'b'")
        assertEquals(
            BinaryExpression(
                left = StringLiteral("a"),
                EqualityOperator.NOT_EQUAL,
                right = StringLiteral("b")
            ),
            value
        )
    }

    @Test
    fun testParenthesizedArithmetic() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("(10 + 5) * 2")
        assertEquals(
            BinaryExpression(
                left = NestedExpression(
                    BinaryExpression(
                        left = NumberLiteral(10.0),
                        ArithmeticOperator.PLUS,
                        right = NumberLiteral(5.0)
                    )
                ),
                operator = ArithmeticOperator.MUL,
                right = NumberLiteral(2.0)
            ),
            value
        )
    }

    @Test
    fun testComplexBoolean() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("(true && false) || !true")
        assertEquals(
            BinaryExpression(
                left = NestedExpression(
                    BinaryExpression(
                        left = BooleanLiteral(true),
                        BooleanOperator.AND,
                        right = BooleanLiteral(false)
                    )
                ),
                BooleanOperator.OR,
                right = UnaryExpression(
                    UnaryOperator.NOT,
                    BooleanLiteral(true)
                )
            ),
            value
        )
    }

    @Test
    fun testPowerFunction() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("pow(2, 4)")
        assertEquals(
            FunctionCallExpression(
                name = "pow",
                parameters = UnnamedParameterList(
                    listOf(
                        NumberLiteral(2.0),
                        NumberLiteral(4.0)
                    )
                )
            ),
            value
        )
    }

    @Test
    fun testPowerWithDivision() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("pow(10 / 5, 3)")
        assertEquals(
            FunctionCallExpression(
                name = "pow",
                parameters = UnnamedParameterList(
                    listOf(
                        BinaryExpression(
                            left = NumberLiteral(10.0),
                            ArithmeticOperator.DIV,
                            right = NumberLiteral(5.0)
                        ),
                        NumberLiteral(3.0)
                    )
                )
            ),
            value
        )
    }

    @Test
    fun testNestedPower() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("pow(pow(2, 1), 3)")
        assertEquals(
            FunctionCallExpression(
                name = "pow",
                parameters = UnnamedParameterList(
                    listOf(
                        FunctionCallExpression(
                            name = "pow",
                            parameters = UnnamedParameterList(
                                listOf(
                                    NumberLiteral(2.0),
                                    NumberLiteral(1.0)
                                )
                            )
                        ),
                        NumberLiteral(3.0)
                    )
                )
            ),
            value
        )
    }

    @Test
    fun testTernaryOperation() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("1 > 0 ? 'greater' : 'not greater'")
        assertEquals(
            TernaryExpression(
                condition = BinaryExpression(
                    left = NumberLiteral(1.0),
                    EqualityOperator.GT,
                    right = NumberLiteral(0.0)
                ),
                trueBranch = StringLiteral("greater"),
                falseBranch = StringLiteral("not greater")
            ),
            value
        )
    }

    @Test
    fun testTernaryWithBoolean() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("false ? 10 : 20")
        assertEquals(
            TernaryExpression(
                condition = BooleanLiteral(false),
                trueBranch = NumberLiteral(10.0),
                falseBranch = NumberLiteral(20.0)
            ),
            value
        )
    }

    @Test
    fun testElvisOperator() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("'test' ?: 'default'")
        assertEquals(
            TernaryExpression(
                TopLevelTypeCoercion(
                    content = StringLiteral("test"),
                    type = TypeCoercion.Type.AvailabilityBoolean
                ),
                trueBranch = StringLiteral("test"),
                falseBranch = StringLiteral("default")
            ),
            value
        )
    }

    @Test
    fun testElvisWithNull() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("{schema} ?: 'fallback'")
        assertEquals(
            TernaryExpression(
                TopLevelTypeCoercion(
                    content = SchemaPathContent(listOf("schema")),
                    type = TypeCoercion.Type.AvailabilityBoolean
                ),
                trueBranch = SchemaPathContent(listOf("schema")),
                falseBranch = StringLiteral("fallback")
            ),
            value
        )
    }

    @Test
    fun testTernaryWithComparison() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("(5 * 2) == 10 ? true : false")
        assertEquals(
            TernaryExpression(
                condition = BinaryExpression(
                    left = NestedExpression(
                        BinaryExpression(
                            left = NumberLiteral(5.0),
                            ArithmeticOperator.MUL,
                            right = NumberLiteral(2.0)
                        )
                    ),
                    EqualityOperator.EQUAL,
                    right = NumberLiteral(10.0)
                ),
                trueBranch = BooleanLiteral(true),
                falseBranch = BooleanLiteral(false)
            ),
            value
        )
    }

    @Test
    fun testElvisWithStrings() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("'abc' ?: 'xyz'")
        assertEquals(
            TernaryExpression(
                TopLevelTypeCoercion(
                    content = StringLiteral("abc"),
                    type = TypeCoercion.Type.AvailabilityBoolean
                ),
                trueBranch = StringLiteral("abc"),
                falseBranch = StringLiteral("xyz")
            ),
            value
        )
    }

    @Test
    fun testTernaryWithDivision() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("(10 / 2) == 5 ? 'correct' : 'incorrect'")
        assertEquals(
            TernaryExpression(
                condition = BinaryExpression(
                    left = NestedExpression(
                        BinaryExpression(
                            left = NumberLiteral(10.0),
                            ArithmeticOperator.DIV,
                            right = NumberLiteral(2.0)
                        )
                    ),
                    EqualityOperator.EQUAL,
                    right = NumberLiteral(5.0)
                ),
                trueBranch = StringLiteral("correct"),
                falseBranch = StringLiteral("incorrect")
            ),
            value
        )
    }

    @Test
    fun testComplexTernary() {
        val value =
            WeaverParser.parseComputationWeaverObjectLanguage("true && (false || true) ? 'complex_true' : 'complex_false'")
        assertEquals(
            TernaryExpression(
                condition = BinaryExpression(
                    left = BooleanLiteral(true),
                    BooleanOperator.AND,
                    right = NestedExpression(
                        BinaryExpression(
                            left = BooleanLiteral(false),
                            BooleanOperator.OR,
                            right = BooleanLiteral(true)
                        )
                    )
                ),
                trueBranch = StringLiteral("complex_true"),
                falseBranch = StringLiteral("complex_false")
            ),
            value
        )
    }

    @Test
    fun testElvisWithEquality() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("'one' == 'one' ?: 'not_equal'")
        assertEquals(
            TernaryExpression(
                TopLevelTypeCoercion(
                    content = BinaryExpression(
                        left = StringLiteral("one"),
                        EqualityOperator.EQUAL,
                        right = StringLiteral("one")
                    ),
                    type = TypeCoercion.Type.AvailabilityBoolean
                ),
                trueBranch = BinaryExpression(
                    left = StringLiteral("one"),
                    EqualityOperator.EQUAL,
                    right = StringLiteral("one")
                ),
                falseBranch = StringLiteral("not_equal")
            ),
            value
        )
    }

    @Test
    fun testStringCoercion() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("|123|s|")
        assertEquals(
            TopLevelTypeCoercion(
                content = NumberLiteral(123.0),
                type = TypeCoercion.Type.String
            ),
            value
        )
    }

    @Test
    fun testBooleanCoercion() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("|'true'|b|")
        assertEquals(
            TopLevelTypeCoercion(
                content = StringLiteral("true"),
                type = TypeCoercion.Type.Boolean
            ),
            value
        )
    }

    @Test
    fun testNumberCoercion() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("|'45.67'|f|")
        assertEquals(
            TopLevelTypeCoercion(
                content = StringLiteral("45.67"),
                type = TypeCoercion.Type.Float
            ),
            value
        )
    }

    @Test
    fun testExpressionCoercion() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("|1 + 2 * 3|s|")
        assertEquals(
            TopLevelTypeCoercion(
                content = BinaryExpression(
                    left = BinaryExpression(
                        left = NumberLiteral(value = 1.0),
                        operator = ArithmeticOperator.PLUS,
                        right = NumberLiteral(value = 2.0)
                    ), operator = ArithmeticOperator.MUL, right = NumberLiteral(value = 3.0)
                ),
                type = TypeCoercion.Type.String
            ),
            value
        )
    }

    @Test
    fun testBooleanExpressionCoercion() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("|true && false || true|b|")
        assertEquals(
            TopLevelTypeCoercion(
                content = BinaryExpression(
                    left = BinaryExpression(
                        left = BooleanLiteral(true),
                        BooleanOperator.AND,
                        right = BooleanLiteral(false)
                    ),
                    BooleanOperator.OR,
                    right = BooleanLiteral(true)
                ),
                type = TypeCoercion.Type.Boolean
            ),
            value
        )
    }

    @Test
    fun testEmptyObject() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("{}")
        assertEquals(
            CurrentAccessor,
            value
        )
    }

    @Test
    fun testSchemaPath() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("{user.id}")
        assertEquals(
            SchemaPathContent(listOf("user", "id")),
            value
        )
    }

    @Test
    fun testSchemaWithArithmetic() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("{product.price} + 5")
        assertEquals(
            BinaryExpression(
                left = SchemaPathContent(listOf("product", "price")),
                ArithmeticOperator.PLUS,
                right = NumberLiteral(5.0)
            ),
            value
        )
    }

    @Test
    fun testNestedSchema() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("{otherSchema.item.id}")
        assertEquals(
            SchemaPathContent(listOf("otherSchema", "item", "id")),
            value
        )
    }

    @Test
    fun testParameterAccess() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("{##requestParam##}")
        assertEquals(
            ParameterAccessContent("requestParam"),
            value
        )
    }

    @Test
    fun testSchemaCoercion() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("|{user.isActive}|b|")
        assertEquals(
            TopLevelTypeCoercion(
                content = SchemaPathContent(listOf("user", "isActive")),
                type = TypeCoercion.Type.Boolean
            ),
            value
        )
    }

    @Test
    fun testLimboObjectWithParams() {
        val value =
            WeaverParser.parseComputationWeaverObjectLanguage("{#orderDetail#(item_id=##param##, quantity=#otherLimbo#)}")
        assertEquals(
            LimboObjectCallContent(
                limboObject = "orderDetail",
                parameters = ParameterList(
                    listOf(
                        Parameter("item_id", ParameterAccessContent("param")),
                        Parameter("quantity", LimboObjectCallContent(limboObject = "otherLimbo"))
                    )
                )
            ),
            value
        )
    }

    @Test
    fun testComplexLimboObject() {
        val value =
            WeaverParser.parseComputationWeaverObjectLanguage("{report.#metrics#(period=##period##, type=type)}")
        assertEquals(
            LimboObjectCallContent(
                schemaPath = SchemaPathContent(listOf("report")),
                limboObject = "metrics",
                parameters = ParameterList(
                    listOf(
                        Parameter("period", ParameterAccessContent("period")),
                        Parameter("type", SchemaPathContent(listOf("type")))
                    )
                )
            ),
            value
        )
    }

    @Test
    fun testArithmeticWithSchema() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("(100 - {item.discount}) / 2")
        assertEquals(
            BinaryExpression(
                left = NestedExpression(
                    BinaryExpression(
                        left = NumberLiteral(100.0),
                        ArithmeticOperator.MINUS,
                        right = SchemaPathContent(listOf("item", "discount"))
                    )
                ),
                ArithmeticOperator.DIV,
                right = NumberLiteral(2.0)
            ),
            value
        )
    }

    @Test
    fun testTernaryWithSchema() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("{account.balance} == 0 ? 'empty' : 'not_empty'")
        assertEquals(
            TernaryExpression(
                condition = BinaryExpression(
                    left = SchemaPathContent(listOf("account", "balance")),
                    EqualityOperator.EQUAL,
                    right = NumberLiteral(0.0)
                ),
                trueBranch = StringLiteral("empty"),
                falseBranch = StringLiteral("not_empty")
            ),
            value
        )
    }

    @Test
    fun testPowerWithSchema() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("pow({config.base}, {config.exponent})")
        assertEquals(
            FunctionCallExpression(
                name = "pow",
                parameters = UnnamedParameterList(
                    listOf(
                        SchemaPathContent(listOf("config", "base")),
                        SchemaPathContent(listOf("config", "exponent"))
                    )
                )
            ),
            value
        )
    }

    @Test
    fun testNotWithSchema() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("!{user.isBlocked}")
        assertEquals(
            UnaryExpression(
                UnaryOperator.NOT,
                SchemaPathContent(listOf("user", "isBlocked"))
            ),
            value
        )
    }

    @Test
    fun testStringConcatWithSchema() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("{address.street} + ', ' + {address.city}")
        assertEquals(
            BinaryExpression(
                left = BinaryExpression(
                    left = SchemaPathContent(listOf("address", "street")),
                    ArithmeticOperator.PLUS,
                    right = StringLiteral(", ")
                ),
                ArithmeticOperator.PLUS,
                right = SchemaPathContent(listOf("address", "city"))
            ),
            value
        )
    }

    @Test
    fun testLogicalWithSchema() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("{setting.debugMode} && {user.isAdmin}")
        assertEquals(
            BinaryExpression(
                left = SchemaPathContent(listOf("setting", "debugMode")),
                BooleanOperator.AND,
                right = SchemaPathContent(listOf("user", "isAdmin"))
            ),
            value
        )
    }

    @Test
    fun testPowerComparisonWithSchema() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("pow(2, {version.major}) >= 4")
        assertEquals(
            BinaryExpression(
                left = FunctionCallExpression(
                    name = "pow",
                    parameters = UnnamedParameterList(
                        listOf(
                            NumberLiteral(2.0),
                            SchemaPathContent(listOf("version", "major"))
                        )
                    )
                ),
                EqualityOperator.GTE,
                right = NumberLiteral(4.0)
            ),
            value
        )
    }

    @Test
    fun testComparisonWithSchema() {
        val value = WeaverParser.parseComputationWeaverObjectLanguage("{array.length} > 0")
        assertEquals(
            BinaryExpression(
                left = SchemaPathContent(listOf("array", "length")),
                EqualityOperator.GT,
                right = NumberLiteral(0.0)
            ),
            value
        )
    }
}