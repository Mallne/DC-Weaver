package cloud.mallne.dicentra.weaver.language.ast

import cloud.mallne.dicentra.weaver.language.ast.expressions.*
import cloud.mallne.dicentra.weaver.language.generated.WeaverObjectNotationBaseVisitor
import cloud.mallne.dicentra.weaver.language.generated.WeaverObjectNotationParser

class WeaverObjectNotationAstBuilder : WeaverObjectNotationBaseVisitor<WeaverExpression>() {
    override fun visitSchemaCallContent(ctx: WeaverObjectNotationParser.SchemaCallContentContext): SchemaPathContent {
        return visitSchemaPath(ctx.schemaPath())
    }

    override fun visitLimboObjectCallContent(ctx: WeaverObjectNotationParser.LimboObjectCallContentContext): LimboObjectCallContent {
        val key = ctx.IDENTIFIER().text
        // This node type from the grammar does not have parameters directly attached
        return LimboObjectCallContent(limboObject = key)
    }

    override fun visitSchemaPath(ctx: WeaverObjectNotationParser.SchemaPathContext): SchemaPathContent {
        val path = ctx.IDENTIFIER().map { it.text }
        return SchemaPathContent(path)
    }

    override fun visitLimboObjectSchemaCallContent(ctx: WeaverObjectNotationParser.LimboObjectSchemaCallContentContext): LimboObjectCallContent {
        val limbo = ctx.IDENTIFIER().text
        return LimboObjectCallContent(schemaPath = visitSchemaPath(ctx.schemaPath()), limbo)
    }

    override fun visitParameterAccessContent(ctx: WeaverObjectNotationParser.ParameterAccessContentContext): ParameterAccessContent {
        return ParameterAccessContent(ctx.IDENTIFIER().text)
    }

    override fun visitParameterList(ctx: WeaverObjectNotationParser.ParameterListContext): ParameterList {
        val parameters = ctx.parameter().map { visitParameter(it) }
        return ParameterList(parameters)
    }

    override fun visitLimboObjectCallWithParametersContent(ctx: WeaverObjectNotationParser.LimboObjectCallWithParametersContentContext): LimboObjectCallContent {
        val key = ctx.IDENTIFIER().text
        val parameters = visitParameterList(ctx.parameterList())
        return LimboObjectCallContent(limboObject = key, parameters = parameters)
    }

    override fun visitLimboObjectSchemaCallWithParametersContent(ctx: WeaverObjectNotationParser.LimboObjectSchemaCallWithParametersContentContext): LimboObjectCallContent {
        val schemaPath = visitSchemaPath(ctx.schemaPath())
        val key = ctx.IDENTIFIER().text
        val parameters = visitParameterList(ctx.parameterList())
        return LimboObjectCallContent(schemaPath, key, parameters)
    }

    override fun visitTypeCoercionWrapper(ctx: WeaverObjectNotationParser.TypeCoercionWrapperContext): AccessorTypeCoercion {
        val content = visitCoercionPart(ctx.coercionPart()).content // Visit inner content from coercionPart
        val targetType = visitCoercionPart(ctx.coercionPart()).type
        return AccessorTypeCoercion(content, targetType)
    }

    override fun visitCoercionPart(ctx: WeaverObjectNotationParser.CoercionPartContext): AccessorTypeCoercion {
        val content = visit(ctx.weaverContent()) as WeaverContent
        val type = ctx.IDENTIFIER().text
        // Return a temporary wrapper, this visitCoercionPart might be directly called
        // or used by visitTypeCoercionWrapper. It's better to build the full TypeCoercionWrapper
        // in visitTypeCoercionWrapper which is the rule labeled.
        // For now, we'll return a simple object for the delegate to pick up,
        // or directly build TypeCoercionWrapper in visitTypeCoercionWrapper.
        // Let's refine visitTypeCoercionWrapper's logic slightly.
        return AccessorTypeCoercion(content, type) // Direct construction here is fine.
    }

    override fun visitParameter(ctx: WeaverObjectNotationParser.ParameterContext): Parameter {
        val name = ctx.IDENTIFIER().text
        val value =
            visit(ctx.weaverContent()) as WeaverContent// As per your design: parameter value must be weaverContent
        return Parameter(name, value)
    }

    // You might want to override visitChildren or defaultResult if a rule isn't specifically handled.
    override fun defaultResult(): WeaverContent =
        throw IllegalStateException("Visitor method not implemented or rule produced no result.")
}