package cloud.mallne.dicentra.weaver.language.ast

import cloud.mallne.dicentra.weaver.language.ast.expressions.*
import cloud.mallne.dicentra.weaver.language.generated.ComputationNotationBaseVisitor
import cloud.mallne.dicentra.weaver.language.generated.ComputationNotationParser

class ComputationWolAstBuilder : ComputationNotationBaseVisitor<WeaverExpression>() {
    //------------------- SECTION -------------------
    // Literals
    override fun visitStringLiteral(ctx: ComputationNotationParser.StringLiteralContext): StringLiteral {
        return StringLiteral(ctx.STRING_LITERAL().text.removePrefix("\"").removeSuffix("\""))
    }

    override fun visitSStringLiteral(ctx: ComputationNotationParser.SStringLiteralContext): StringLiteral {
        return StringLiteral(ctx.SSTRING_LITERAL().text.removePrefix("\'").removeSuffix("\'"))
    }

    override fun visitNumberLiteral(ctx: ComputationNotationParser.NumberLiteralContext): NumberLiteral {
        return NumberLiteral(ctx.NUMBER().text.toDouble())
    }

    override fun visitBooleanLiteral(ctx: ComputationNotationParser.BooleanLiteralContext): BooleanLiteral {
        return BooleanLiteral(ctx.BOOLEAN_LITERAL().text.toBooleanStrict())
    }

    //------------------- SECTION -------------------
    // Operators
    override fun visitEqualityOperatorExpr(ctx: ComputationNotationParser.EqualityOperatorExprContext): BinaryExpression {
        return BinaryExpression(
            left = visit(ctx.first!!) as TopLevelWeaverExpression,
            right = visit(ctx.second!!) as TopLevelWeaverExpression,
            operator = EqualityOperator.byValue(ctx.EQUALITY_OPERATIONS().text)
                ?: throw IllegalArgumentException("equality operator expression expected")
        )
    }

    override fun visitBooleanOperatorExpr(ctx: ComputationNotationParser.BooleanOperatorExprContext): BinaryExpression {
        return BinaryExpression(
            left = visit(ctx.first!!) as TopLevelWeaverExpression,
            right = visit(ctx.second!!) as TopLevelWeaverExpression,
            operator = BooleanOperator.byValue(ctx.BOOLEAN_OPERATIONS().text)
                ?: throw IllegalArgumentException("boolean operator expression expected")
        )
    }

    override fun visitArithmeticOperatorExpr(ctx: ComputationNotationParser.ArithmeticOperatorExprContext): BinaryExpression {
        return BinaryExpression(
            left = visit(ctx.first!!) as TopLevelWeaverExpression,
            right = visit(ctx.second!!) as TopLevelWeaverExpression,
            operator = ArithmeticOperator.byValue(ctx.ARITHMETIC_OPERATIONS().text)
                ?: throw IllegalArgumentException("arithmetic operator expression expected")
        )
    }

    //------------------- SECTION -------------------
    // IO
    override fun visitFunctionInvocationExpr(ctx: ComputationNotationParser.FunctionInvocationExprContext): FunctionCallExpression {
        return FunctionCallExpression(
            ctx.IDENTIFIER().text,
            parameters = visitFunctionParameterList(ctx.functionParameterList())
        )
    }

    override fun visitFunctionParameterList(ctx: ComputationNotationParser.FunctionParameterListContext): UnnamedParameterList {
        return UnnamedParameterList(ctx.baseComputationExpression().map { visit(it) as TopLevelWeaverExpression })
    }

    override fun visitParenBaseComputationExpr(ctx: ComputationNotationParser.ParenBaseComputationExprContext): NestedExpression {
        return NestedExpression(visit(ctx.baseComputationExpression()) as TopLevelWeaverExpression)
    }

    override fun visitAccessorAccessExpr(ctx: ComputationNotationParser.AccessorAccessExprContext): WeaverContent {
        return visit(ctx.accessorAccess()) as WeaverContent
    }

    override fun visitNegatedCompute(ctx: ComputationNotationParser.NegatedComputeContext): UnaryExpression {
        return UnaryExpression(ctx.BANG().text, visit(ctx.baseComputationExpression()) as TopLevelWeaverExpression)
    }

    override fun visitTopLevelCoercion(ctx: ComputationNotationParser.TopLevelCoercionContext): TopLevelTypeCoercion {
        return TopLevelTypeCoercion(
            content = visit(ctx.baseComputationExpression()),
            targetType = ctx.IDENTIFIER().text
        )
    }

    override fun visitFullTernary(ctx: ComputationNotationParser.FullTernaryContext): TernaryExpression {
        return TernaryExpression(
            visit(ctx.condition!!) as TopLevelWeaverExpression,
            trueBranch = visit(ctx.trueBranch!!) as TopLevelWeaverExpression,
            falseBranch = visit(ctx.falseBranch!!) as TopLevelWeaverExpression
        )
    }

    override fun visitAvailabilityTernaryShorthand(ctx: ComputationNotationParser.AvailabilityTernaryShorthandContext): TernaryExpression {
        val valueContent = visit(ctx.value!!)
        return TernaryExpression(
            TopLevelTypeCoercion(
                content = valueContent,
                type = TypeCoercion.Type.AvailabilityBoolean
            ),
            trueBranch = valueContent as TopLevelWeaverExpression,
            falseBranch = visit(ctx.other!!) as TopLevelWeaverExpression
        )
    }

    override fun visitCurrentAccessorExpr(ctx: ComputationNotationParser.CurrentAccessorExprContext): CurrentAccessor {
        return CurrentAccessor
    }

    override fun visitAccessorExpr(ctx: ComputationNotationParser.AccessorExprContext): WeaverContent {
        return visit(ctx.weaverContent()) as WeaverContent
    }

    //------------------- SECTION -------------------
    // WeaverObjectNotation - WeaverContent
    override fun visitSchemaCallContent(ctx: ComputationNotationParser.SchemaCallContentContext): SchemaPathContent {
        return visitSchemaPath(ctx.schemaPath())
    }

    override fun visitLimboObjectCallContent(ctx: ComputationNotationParser.LimboObjectCallContentContext): LimboObjectCallContent {
        val key = ctx.IDENTIFIER().text
        // This node type from the grammar does not have parameters directly attached
        return LimboObjectCallContent(limboObject = key)
    }

    override fun visitSchemaPath(ctx: ComputationNotationParser.SchemaPathContext): SchemaPathContent {
        val path = ctx.IDENTIFIER().map { it.text }
        return SchemaPathContent(path)
    }

    override fun visitLimboObjectSchemaCallContent(ctx: ComputationNotationParser.LimboObjectSchemaCallContentContext): LimboObjectCallContent {
        val limbo = ctx.IDENTIFIER().text
        return LimboObjectCallContent(schemaPath = visitSchemaPath(ctx.schemaPath()), limbo)
    }

    override fun visitParameterAccessContent(ctx: ComputationNotationParser.ParameterAccessContentContext): ParameterAccessContent {
        return ParameterAccessContent(ctx.IDENTIFIER().text)
    }

    override fun visitParameterList(ctx: ComputationNotationParser.ParameterListContext): ParameterList {
        val parameters = ctx.parameter().map { visitParameter(it) }
        return ParameterList(parameters)
    }

    override fun visitLimboObjectCallWithParametersContent(ctx: ComputationNotationParser.LimboObjectCallWithParametersContentContext): LimboObjectCallContent {
        val key = ctx.IDENTIFIER().text
        val parameters = visitParameterList(ctx.parameterList())
        return LimboObjectCallContent(limboObject = key, parameters = parameters)
    }

    override fun visitLimboObjectSchemaCallWithParametersContent(ctx: ComputationNotationParser.LimboObjectSchemaCallWithParametersContentContext): LimboObjectCallContent {
        val schemaPath = visitSchemaPath(ctx.schemaPath())
        val key = ctx.IDENTIFIER().text
        val parameters = visitParameterList(ctx.parameterList())
        return LimboObjectCallContent(schemaPath, key, parameters)
    }

    override fun visitTypeCoercionWrapper(ctx: ComputationNotationParser.TypeCoercionWrapperContext): AccessorTypeCoercion {
        val content = visitCoercionPart(ctx.coercionPart()).content // Visit inner content from coercionPart
        val targetType = visitCoercionPart(ctx.coercionPart()).type
        return AccessorTypeCoercion(content, targetType)
    }

    override fun visitCoercionPart(ctx: ComputationNotationParser.CoercionPartContext): AccessorTypeCoercion {
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

    override fun visitParameter(ctx: ComputationNotationParser.ParameterContext): Parameter {
        val name = ctx.IDENTIFIER().text
        val value =
            visit(ctx.weaverContent()) as WeaverContent// As per your design: parameter value must be weaverContent
        return Parameter(name, value)
    }

    override fun defaultResult(): WeaverExpression =
        throw IllegalStateException("Visitor method not implemented or rule produced no result.")
}