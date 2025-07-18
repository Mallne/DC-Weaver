package cloud.mallne.dicentra.weaver.language

import cloud.mallne.dicentra.weaver.language.ast.*
import cloud.mallne.dicentra.weaver.language.generated.AccessorWeaverObjectLanguageBaseVisitor
import cloud.mallne.dicentra.weaver.language.generated.AccessorWeaverObjectLanguageParser

// Extend the generated BaseVisitor and specify the return type of your visit methods
// In this case, each visit method will return an AccessorExpression (or a specific subclass)
class AccessorWeaverObjectLanguageAstBuilder : AccessorWeaverObjectLanguageBaseVisitor<AccessorExpression>() {

    // Implement the abstract method: defaultResult().
    // We return null as a default. If a rule *should* always produce an AST node,
    // its specific visit method will override this default.
    override fun defaultResult(): AccessorExpression {
        throw IllegalStateException(
            "Visitor defaultResult() was called. This indicates an unhandled parse tree node " +
                    "or an invalid grammar structure that should have been caught earlier."
        )
    }

    // Example for `pathAccess` rule
    override fun visitPathAccessExpr(ctx: AccessorWeaverObjectLanguageParser.PathAccessExprContext): AccessorExpression {
        val parts = ctx.pathAccess().IDENTIFIER().map { it.text } // Get all identifiers in the path
        val arrayIndexCtx = ctx.pathAccess().arrayIndex() // Get the array index context if present

        val arrayIndex = arrayIndexCtx?.let {
            val number = it.NUMBER()?.text?.toIntOrNull()
            val identifier = it.IDENTIFIER()?.text
            val typeIdentifier = it.TYPE_IDENTIFIER()?.text
            if (number != null) {
                LiteralArrayIndex(number)
            } else if (it.LT() != null && typeIdentifier == null && identifier != null) { // {<indexvalue>}
                ComputedArrayIndex(identifier)
            } else if (it.PIPE().size == 2 && typeIdentifier != null && identifier != null) { // |{<indexvalue>}|i|
                CoercedComputedArrayIndex(identifier, typeIdentifier)
            } else {
                null // Should not happen if grammar is correct, or throw an error
            }
        }

        return PathAccess(parts, arrayIndex)
    }

    // Example for `schemaCall` rule: {otherSchema}
    override fun visitSchemaCallExpr(ctx: AccessorWeaverObjectLanguageParser.SchemaCallExprContext): AccessorExpression {
        val schemaName = ctx.schemaCall().IDENTIFIER().text // Get the text of the IDENTIFIER token
        return SchemaCall(schemaName)
    }

    // Example for `schemaNestedCall` rule: {otherSchema.nested}
    override fun visitSchemaNestedCallExpr(ctx: AccessorWeaverObjectLanguageParser.SchemaNestedCallExprContext): AccessorExpression {
        val identifiers = ctx.schemaNestedCall().IDENTIFIER()
        val schemaName = identifiers[0].text
        val nestedName = identifiers[1].text
        return SchemaNestedCall(schemaName, nestedName)
    }

    // Example for `limboObjectCall` rule: {<menuKey>}
    override fun visitLimboObjectCallExpr(ctx: AccessorWeaverObjectLanguageParser.LimboObjectCallExprContext): AccessorExpression {
        val limboKey = ctx.limboObjectCall().IDENTIFIER().text
        return LimboObjectCall(limboKey)
    }

    // Example for `limboObjectSchemaCall` rule: {otherSchema.<menuKey>}
    override fun visitLimboObjectSchemaCallExpr(ctx: AccessorWeaverObjectLanguageParser.LimboObjectSchemaCallExprContext): AccessorExpression {
        val identifiers = ctx.limboObjectSchemaCall().IDENTIFIER()
        val schemaName = identifiers[0].text
        val limboKey = identifiers[1].text
        return LimboObjectSchemaCall(schemaName, limboKey)
    }

    // Example for `parameterAccess` rule: {<<param>>}
    override fun visitParameterAccessExpr(ctx: AccessorWeaverObjectLanguageParser.ParameterAccessExprContext): AccessorExpression {
        val paramName = ctx.parameterAccess().IDENTIFIER().text
        return ParameterAccess(paramName)
    }

    // Add more override methods for other rules in your grammar (e.g., from Computation WOL)
}