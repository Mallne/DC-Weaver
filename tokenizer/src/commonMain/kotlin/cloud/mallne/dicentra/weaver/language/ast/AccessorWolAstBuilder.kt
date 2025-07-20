package cloud.mallne.dicentra.weaver.language.ast

import cloud.mallne.dicentra.weaver.language.ast.expressions.*
import cloud.mallne.dicentra.weaver.language.generated.AccessorWeaverObjectLanguageBaseVisitor
import cloud.mallne.dicentra.weaver.language.generated.AccessorWeaverObjectLanguageParser

class AccessorWolAstBuilder : AccessorWeaverObjectLanguageBaseVisitor<WeaverExpression>() {
    override fun visitComputedArrayIndexExpr(ctx: AccessorWeaverObjectLanguageParser.ComputedArrayIndexExprContext): ComputedArrayIndexExpr {
        return ComputedArrayIndexExpr(visit(ctx.weaverContent()) as WeaverContent)
    }

    override fun visitLiteralArrayIndex(ctx: AccessorWeaverObjectLanguageParser.LiteralArrayIndexContext): WeaverExpression {
        return LiteralArrayIndex(ctx.NUMBER().text.toInt())
    }

    override fun visitDirectArrayAccessExpr(ctx: AccessorWeaverObjectLanguageParser.DirectArrayAccessExprContext): PathAccessExpr {
        return PathAccessExpr(listOf(PathElement(arrayAccess = visit(ctx.arrayAccess()) as ArrayAccess)))
    }

    override fun visitExpressionInterpolation(ctx: AccessorWeaverObjectLanguageParser.ExpressionInterpolationContext): ExpressionInterpolation {
        return ExpressionInterpolation(visit(ctx.weaverContent()) as WeaverContent)
    }

    override fun visitIdentifierInterpolation(ctx: AccessorWeaverObjectLanguageParser.IdentifierInterpolationContext): IdentifierInterpolation {
        return IdentifierInterpolation(ctx.text)
    }

    override fun visitPathSegment(ctx: AccessorWeaverObjectLanguageParser.PathSegmentContext): PathSegment {
        return PathSegment(ctx.interpolatedPathPart().map { visit(it) as InterpolatedPathPart })
    }

    override fun visitPathElement(ctx: AccessorWeaverObjectLanguageParser.PathElementContext): PathElement {
        return PathElement(visitPathSegment(ctx.pathSegment()), ctx.arrayAccess()?.let { visit(it) as ArrayAccess })
    }

    override fun visitPathAccess(ctx: AccessorWeaverObjectLanguageParser.PathAccessContext): PathAccessExpr {
        return PathAccessExpr(ctx.pathElement().map { visitPathElement(it) })
    }

    override fun visitPathAccessExpr(ctx: AccessorWeaverObjectLanguageParser.PathAccessExprContext): PathAccessExpr {
        return visitPathAccess(ctx.pathAccess())
    }

    //------------------- SECTION -------------------
    // WeaverObjectNotation - WeaverContent
    override fun visitSchemaCallContent(ctx: AccessorWeaverObjectLanguageParser.SchemaCallContentContext): SchemaPathContent {
        return visitSchemaPath(ctx.schemaPath())
    }

    override fun visitLimboObjectCallContent(ctx: AccessorWeaverObjectLanguageParser.LimboObjectCallContentContext): LimboObjectCallContent {
        val key = ctx.IDENTIFIER().text
        // This node type from the grammar does not have parameters directly attached
        return LimboObjectCallContent(limboObject = key)
    }

    override fun visitSchemaPath(ctx: AccessorWeaverObjectLanguageParser.SchemaPathContext): SchemaPathContent {
        val path = ctx.IDENTIFIER().map { it.text }
        return SchemaPathContent(path)
    }

    override fun visitLimboObjectSchemaCallContent(ctx: AccessorWeaverObjectLanguageParser.LimboObjectSchemaCallContentContext): LimboObjectCallContent {
        val limbo = ctx.IDENTIFIER().text
        return LimboObjectCallContent(schemaPath = visitSchemaPath(ctx.schemaPath()), limbo)
    }

    override fun visitParameterAccessContent(ctx: AccessorWeaverObjectLanguageParser.ParameterAccessContentContext): ParameterAccessContent {
        return ParameterAccessContent(ctx.IDENTIFIER().text)
    }

    override fun visitParameterList(ctx: AccessorWeaverObjectLanguageParser.ParameterListContext): ParameterList {
        val parameters = ctx.parameter().map { visitParameter(it) }
        return ParameterList(parameters)
    }

    override fun visitLimboObjectCallWithParametersContent(ctx: AccessorWeaverObjectLanguageParser.LimboObjectCallWithParametersContentContext): LimboObjectCallContent {
        val key = ctx.IDENTIFIER().text
        val parameters = visitParameterList(ctx.parameterList())
        return LimboObjectCallContent(limboObject = key, parameters = parameters)
    }

    override fun visitLimboObjectSchemaCallWithParametersContent(ctx: AccessorWeaverObjectLanguageParser.LimboObjectSchemaCallWithParametersContentContext): LimboObjectCallContent {
        val schemaPath = visitSchemaPath(ctx.schemaPath())
        val key = ctx.IDENTIFIER().text
        val parameters = visitParameterList(ctx.parameterList())
        return LimboObjectCallContent(schemaPath, key, parameters)
    }

    override fun visitTypeCoercionWrapper(ctx: AccessorWeaverObjectLanguageParser.TypeCoercionWrapperContext): TypeCoercionWrapper {
        val content = visitCoercionPart(ctx.coercionPart()).content // Visit inner content from coercionPart
        val targetType = visitCoercionPart(ctx.coercionPart()).targetType
        return TypeCoercionWrapper(content, targetType)
    }

    override fun visitCoercionPart(ctx: AccessorWeaverObjectLanguageParser.CoercionPartContext): TypeCoercionWrapper {
        val content = visit(ctx.weaverContent()) as WeaverContent
        val type = ctx.IDENTIFIER().text
        // Return a temporary wrapper, this visitCoercionPart might be directly called
        // or used by visitTypeCoercionWrapper. It's better to build the full TypeCoercionWrapper
        // in visitTypeCoercionWrapper which is the rule labeled.
        // For now, we'll return a simple object for the delegate to pick up,
        // or directly build TypeCoercionWrapper in visitTypeCoercionWrapper.
        // Let's refine visitTypeCoercionWrapper's logic slightly.
        return TypeCoercionWrapper(content, type) // Direct construction here is fine.
    }

    override fun visitParameter(ctx: AccessorWeaverObjectLanguageParser.ParameterContext): Parameter {
        val name = ctx.IDENTIFIER().text
        val value =
            visit(ctx.weaverContent()) as WeaverContent// As per your design: parameter value must be weaverContent
        return Parameter(name, value)
    }

    override fun defaultResult(): WeaverExpression =
        throw IllegalStateException("Visitor method not implemented or rule produced no result.")
}