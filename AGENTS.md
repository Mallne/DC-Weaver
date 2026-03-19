# Weaver Project Overview

DiCentra Weaver is a high-performance, schema-driven transformation framework designed to process and map JSON data into structured objects using the **Weaver Object Language (WOL)**. It is built as a Kotlin Multiplatform (KMP) project, ensuring compatibility across JVM, Android, iOS, JavaScript, WebAssembly, and Linux.

## Project Structure

The project is divided into two main modules:

### 1. `tokenizer`
Responsible for parsing the Weaver Object Language.
- **Technologies:** ANTLR 4 (via `antlr-kotlin`).
- **Location:** `tokenizer/antlr/` contains the grammar definitions (`.g4` files).
- **Output:** Generates a Kotlin-based AST (Abstract Syntax Tree) for WOL expressions.
- **Key Components:**
    - `WeaverObjectNotation.g4`: Main grammar for the object notation.
    - `AccessorWolAstBuilder.kt`: Maps ANTLR parse trees to the internal AST.

### 2. `core`
The execution engine and schema definitions.
- **Technologies:** `kotlinx.serialization`, `ktor-http`.
- **Key Components:**
    - `WeaverEngine`: The primary entry point for executing transformations.
    - `WeaverSchema`: Defines the structure and rules for transformations.
    - `WeaverContext`: Holds state, plugins, and functions during execution.
    - `CommandDispatcher`: Routes WOL commands to their respective implementations.
- **Execution Flow:** The engine takes a `WeaverSchema` and input JSON, parses any embedded WOL expressions using the `tokenizer`, and produces a transformed JSON result.

## Weaver Object Language (WOL)

WOL is a powerful expression language used within schemas for dynamic data access and transformation. It supports:
- **Path Access:** `menu.id`, `popup.menuitem[0]`
- **Schema Interpolation:** `{otherSchema.path}`
- **LimboObjects:** `{#menuKey#}` (Internal transformation units)
- **Parameters:** `{##parameterName##}`
- **Function Calls:** `{#func#(param=value)}`
- **Type Coercion:** `{|expression|type|}` (e.g., `{|#val#|i|}` for integer coercion)

## Building and Running

The project uses Gradle with the Kotlin Multiplatform plugin.

### Prerequisites
- JDK 21+

### Common Commands
- **Build everything:**
  ```bash
  ./gradlew build
  ```
- **Generate ANTLR sources:**
  ```bash
  ./gradlew :tokenizer:generateKotlinGrammarSource
  ```
- **Run Tests:**
  ```bash
  ./gradlew test # Runs JVM tests
  ./gradlew allTests # Runs tests for all configured KMP targets
  ```
- **Publish to Local Maven:**
  ```bash
  ./gradlew publishToMavenLocal
  ```

## Development Conventions

- **Kotlin First:** Strictly follow Kotlin idiomatic patterns and the project's existing coding style.
- **Multiplatform Compatibility:** Ensure any new code in `commonMain` does not use platform-specific APIs. Use `expect`/`actual` if platform-specific logic is required.
- **AST Integrity:** When modifying the `tokenizer`, ensure that the `AstBuilder` classes are updated to reflect grammar changes.
- **Testing:** Always add unit tests in `commonTest` for new features or bug fixes. Use `AccessorParserTest` and `ComputationParserTest` as references for language features.
- **Internal API:** Use `@InternalAPI` for components that are part of the engine's internals but exposed for plugin development or cross-module usage.

## Architecture Highlights
- **Schema-Driven:** Everything starts with a `WeaverSchema`.
- **Plugin System:** Extendable via `WeaverPlugin`, allowing for custom context rewriting and command dispatching.
- **Stable Outputs:** The engine ensures that transformed objects are "stable" before final serialization.
