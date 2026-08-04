# Business Rules -- Weaver

Rules an agent must respect when writing code for this module.

## Schema Integrity

- **Rule**: All schema transformations must start from a valid `WeaverSchema`.
- **Why**: The engine guarantees stable, predictable outputs only when operating on well-formed schemas.

## Grammar Changes

- **Rule**: Any modification to `.g4` grammar files must be accompanied by updates to `AccessorWolAstBuilder` and corresponding tests.
- **Why**: The AST builder maps ANTLR parse trees to the internal AST. Grammar changes without builder updates produce runtime failures.

## WOL Expression Safety

- **Rule**: WOL expressions must be validated before execution. Invalid expressions should produce clear error messages.
- **Why**: Malformed WOL can cause unexpected data transformations or runtime exceptions.

## Plugin System

- **Rule**: Plugins must implement `WeaverPlugin` interface. Custom context rewriting and command dispatching must go through the plugin system.
- **Why**: The plugin system is the sanctioned extension point. Bypassing it breaks the execution pipeline.

## Edge Cases

- **Stable Outputs**: The engine ensures transformed objects are "stable" before serialization. Do not bypass this stability check.
- **Empty Schemas**: Empty schemas should produce the input unchanged, not errors.
- **Nested LimboObjects**: Deeply nested `{#key#}` references must be resolved in dependency order.

## Overrides

- None. Weaver follows root coding standards without module-specific overrides.
