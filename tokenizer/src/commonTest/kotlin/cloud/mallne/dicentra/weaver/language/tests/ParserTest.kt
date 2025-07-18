package cloud.mallne.dicentra.weaver.language.tests

import cloud.mallne.dicentra.weaver.language.WeaverParser
import cloud.mallne.dicentra.weaver.language.ast.LiteralArrayIndex
import cloud.mallne.dicentra.weaver.language.ast.ParameterAccess
import cloud.mallne.dicentra.weaver.language.ast.PathAccess
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParserTest {
    @Test
    fun testBasicPathAccess() {
        val input = "menu.id"
        val expectedAst = PathAccess(parts = listOf("menu", "id"), arrayIndex = null)
        val actualAst = WeaverParser.parseAccessorWol(input)
        assertEquals(expectedAst, actualAst, "Parsing '$input' should yield correct PathAccess AST")
    }

    @Test
    fun testArrayIndexedAccess() {
        val input = "menu.popup.menuitem[1]"
        val expectedAst = PathAccess(
            parts = listOf("menu", "popup", "menuitem"),
            arrayIndex = LiteralArrayIndex(value = 1)
        )
        val actualAst = WeaverParser.parseAccessorWol(input)
        assertEquals(expectedAst, actualAst, "Parsing '$input' should yield correct array indexed AST")
    }

    @Test
    fun testParameterAccess() {
        val input = "{<<param>>}"
        val expectedAst = ParameterAccess(paramName = "param")
        val actualAst = WeaverParser.parseAccessorWol(input)
        assertEquals(expectedAst, actualAst, "Parsing '$input' should yield correct parameter access AST")
    }

    @Test
    fun testInvalidSyntaxThrowsException() {
        val input = "invalid.syntax["
        assertFailsWith<IllegalArgumentException>("Invalid syntax should throw IllegalArgumentException") {
            WeaverParser.parseAccessorWol(input)
        }
    }
}