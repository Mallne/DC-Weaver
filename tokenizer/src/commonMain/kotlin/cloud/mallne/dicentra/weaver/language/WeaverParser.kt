package cloud.mallne.dicentra.weaver.language

import cloud.mallne.dicentra.weaver.language.ast.AccessorExpression
import cloud.mallne.dicentra.weaver.language.generated.AccessorWeaverObjectLanguageLexer
import cloud.mallne.dicentra.weaver.language.generated.AccessorWeaverObjectLanguageParser
import org.antlr.v4.kotlinruntime.BailErrorStrategy
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.misc.ParseCancellationException

object WeaverParser {

    fun parseAccessorWol(accessorWolString: String): AccessorExpression {
        val charStream = CharStreams.fromString(accessorWolString)
        val lexer = AccessorWeaverObjectLanguageLexer(charStream)
        lexer.removeErrorListeners() // Remove default console error logging from lexer

        val tokens = CommonTokenStream(lexer)
        val parser = AccessorWeaverObjectLanguageParser(tokens)

        parser.removeErrorListeners() // Remove default console error logging from parser
        parser.errorHandler = BailErrorStrategy() // Configure parser to fail fast on syntax errors

        try {
            val parseTree = parser.accessorExpression() // Start parsing from the top-level rule

            val astBuilder = AccessorWeaverObjectLanguageAstBuilder()

            // The visit method now returns a non-nullable AccessorExpression or throws an exception.
            val ast = astBuilder.visit(parseTree)

            return ast // Directly return the non-nullable AST

        } catch (e: ParseCancellationException) {
            // This catches syntax errors identified by BailErrorStrategy during the parsing phase.
            throw IllegalArgumentException("Invalid Accessor WOL syntax: ${e.message}", e)
        } catch (e: IllegalArgumentException) {
            // Catch specific IllegalArgumentExceptions thrown by the AST builder (e.g., from visitErrorNode)
            throw IllegalArgumentException("Error in Accessor WOL syntax or AST construction: ${e.message}", e)
        } catch (e: IllegalStateException) {
            // Catches exceptions thrown by defaultResult() in the visitor,
            // indicating an unhandled or unexpected parse tree state during AST building.
            throw RuntimeException("Unhandled parse tree node during Accessor WOL AST construction for: '${accessorWolString}'. ${e.message}", e)
        } catch (e: Exception) {
            // Catch any other unexpected exceptions during the process.
            throw RuntimeException("An unexpected error occurred during Accessor WOL parsing or AST building for: '${accessorWolString}'", e)
        }
    }

    // You'll add a parseComputationWol method here later for Computation WOL
}