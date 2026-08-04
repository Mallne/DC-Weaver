# Architecture -- Weaver

## Purpose

Weaver is a schema-driven JSON transformation engine that processes and maps JSON data using the Weaver Object Language (WOL). It takes a `WeaverSchema` and input JSON, parses embedded WOL expressions, and produces transformed JSON output.

## Tech Stack

- **Language:** Kotlin Multiplatform (KMP)
- **Parser:** ANTLR 4 (via `antlr-kotlin`)
- **Serialization:** kotlinx.serialization
- **HTTP:** ktor-http (for content types)

## Project Structure

```
weaver/
+-- tokenizer/               # ANTLR grammar and AST builders
|   +-- antlr/               # .g4 grammar files
|   +-- .../                 # AccessorWolAstBuilder, generated parser
+-- core/                    # Execution engine and schema definitions
    +-- WeaverEngine         # Primary entry point
    +-- WeaverSchema         # Schema definition and rules
    +-- WeaverContext         # State, plugins, functions during execution
    +-- CommandDispatcher     # Routes WOL commands to implementations
```

## Data Flow

1. `WeaverSchema` is loaded (JSON with embedded WOL expressions)
2. `WeaverEngine` takes the schema + input JSON
3. `tokenizer` parses WOL expressions within the schema
4. `CommandDispatcher` routes parsed commands to their implementations
5. `WeaverContext` holds execution state, plugins, and functions
6. Engine produces transformed JSON output (objects are "stable" before serialization)

## WOL Language Features

- **Path Access**: `menu.id`, `popup.menuitem[0]`
- **Schema Interpolation**: `{otherSchema.path}`
- **LimboObjects**: `{#menuKey#}` (internal transformation units)
- **Parameters**: `{##parameterName##}`
- **Function Calls**: `{#func#(param=value)}`
- **Type Coercion**: `{|expression|type|}` (e.g., `{|#val#|i|}` for integer)

## Dependencies on Other Modules

- None (standalone module)

## Non-negotiable Rules

- ANTLR grammar changes MUST be accompanied by AST builder updates
- Code in `commonMain` must remain platform-agnostic
- Use `@InternalWeaverAPI` for engine internals exposed for plugin development
