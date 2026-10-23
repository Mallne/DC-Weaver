# DiCentra Weaver

[![DiCentra](https://img.shields.io/badge/DiCentra-grey.svg)](https://code.mallne.cloud)
[![Kotlin](https://img.shields.io/badge/kotlin-grey.svg?logo=kotlin)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-blue.svg?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)

DiCentra Weaver is a schema-driven JSON transformation engine built with Kotlin Multiplatform. It uses the Weaver Object
Language (WOL) to describe accessors, computations, and transformations across JVM, Android, iOS, JS, and Wasm targets.

## Features

- **Schema-driven transformations:** Define input-to-output mappings as reusable schemas and named transforms.
- **Weaver Object Language (WOL):** Expressions for data access, computation, coercion, and transform invocation.
- **Kotlin Multiplatform:** Share transformation logic across supported targets.
- **Extensible architecture:** Add custom functions and plugins to an engine.

## Installation

### Kotlin Multiplatform

The Maven group is `cloud.mallne.dicentra.weaver`. Add the core dependency (which brings in the tokenizer) to
`build.gradle.kts`; use the release version required by your project:

```kotlin
commonMain.dependencies {
    implementation("cloud.mallne.dicentra.weaver:core:1.0.1")
}
```

Resolve DiCentra artifacts from `https://registry.mallne.cloud/repository/DiCentraArtefacts/` with registry credentials
configured outside the build script.

### JavaScript / TypeScript

Weaver publishes `@dicentra/weaver-core` (runtime) and `@dicentra/weaver-tokenizer` (WOL tokenizer/parser) to
`https://registry.mallne.cloud/repository/NodeRegistry/`. The current release is **1.0.1**. Configure the `@dicentra`
npm scope and authenticate using `NPM_TOKEN`, then install the core package:

```bash
npm install @dicentra/weaver-core@1.0.1
```

The core package uses the tokenizer. Install `@dicentra/weaver-tokenizer@1.0.1` directly only when using the parser
independently.

## Weaver Object Language (WOL)

WOL has accessor syntax for navigating input JSON and computation syntax for producing values. A transform declares the
input accessor it runs against; inside its computation, `{}` denotes the current value. WOL is evaluated by Weaver at
runtime, not by Kotlin.

### Accessors and interpolation

| Syntax | Meaning |
|:--|:--|
| `user.id` | Access the `id` field of `user`. |
| `items[0]` | Access the first (zero-based) array element. |
| `items[{##index##}]` | Access an array element using the numeric transform parameter `index`. |
| `{#transform#}` | Reference/call a named transform. |
| `{##param##}` | Access a named transform parameter. |
| `|{value}|i|` | Coerce an expression to an integer. |

Accessor expressions used inside computations are wrapped in braces, for example `{user.id}`. A transform using
`items[{##index##}]` declares `index` in its parameter map with type `ObjectType.Number`.

### Computations

| Category | Examples |
|:--|:--|
| Arithmetic | `1 + 2`, `({price} * 10) / 2`, `5 % 2` |
| Boolean | `true && {isAdmin}`, `!{isBlocked}`, `(5 > 2) || false` |
| Comparison | `{age} >= 18`, `"admin" == {role}`, `5 != 10` |
| Ternary | `{score} > 50 ? "Pass" : "Fail"` |
| Elvis | `{name} ?: "Anonymous"` |
| Coercion | `|{count}|s|` (string), `|{active}|b|` (boolean), `|{price}|f|` (number) |
| Functions | `pow(2, 8)`, `max({a}, {b})` |

The special expression `|{}|ai|` returns the current array's last valid zero-based index, or `-1` when it is empty.

## Kotlin schema builder DSL

The Kotlin DSL constructs schema declarations; expressions passed as strings remain WOL text and are evaluated by
Weaver. A schema has one root, and each transform declares exactly one value, object, or array computation.

```kotlin
import cloud.mallne.dicentra.weaver.core.Weaver
import cloud.mallne.dicentra.weaver.core.specification.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

val temperaturePath = Wol.path("current").field("temperature")
val schema = weaverSchema("weather-v1") {
    root("weather", Wol.reference("temperature"))
    transform("temperature", temperaturePath.asExpression()) {
        value("{}", ObjectType.Number) // `{}` reads the transform's current value
    }
}

val output = Weaver().engine(schema).executeElement(
    buildJsonObject { put("current", buildJsonObject { put("temperature", 18) }) },
)
// output is the JSON number 18
```

Use `objectValue { ... }` to build object output. Within it, `property(name, expression)` emits an expression verbatim,
`source(name, Wol.path(...), type)` creates a source transform and property reference, and `use(name, transform)`
references another named transform. Use `array()` for array output; its default item expression is `{##value##}` and its
default iteration expression is `|{}|ai|`.

`WolAccessorPath` composes identifier-based accessors: `Wol.path("items").index("index")` produces
`items[{##index##}]`, while `.element(0)` produces `items[0]`. `Wol.reference`, `Wol.stringLiteral`, `Wol.divide`,
`Wol.multiply`, and `Wol.mapString` construct common WOL snippets. `WolIdentifier` values must start with a letter or
underscore and contain only letters, digits, or underscores.

## Modules

- **`core`**: Schema model, Kotlin DSL, execution engine, and runtime.
- **`tokenizer`**: WOL tokenizer, lexer, and parser.

## License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.

---
<p align="center">
  Built with ❤️ by Mallne under the DiCentra umbrella
</p>
