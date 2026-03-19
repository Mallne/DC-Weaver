# DiCentra Weaver

[![DiCentra](https://img.shields.io/badge/DiCentra-grey.svg)](https://code.mallne.cloud)
[![Kotlin](https://img.shields.io/badge/kotlin-grey.svg?logo=kotlin)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-blue.svg?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)

DiCentra Weaver is a powerful, schema-driven transformation framework designed to map and process JSON data using a
specialized expression language. Built with Kotlin Multiplatform, it provides a consistent transformation engine across
JVM, Android, iOS, JS, and Wasm targets.

## Features

- **Schema-Driven Transformations:** Define your data mapping rules in a structured schema.
- **Weaver Object Language (WOL):** A rich expression language for dynamic data access and computation.
- **Kotlin Multiplatform:** Run the same transformation logic on any platform.
- **Extensible Architecture:** Easily add custom functions and plugins.
- **High Performance:** Efficiently handles complex JSON structures.

## Installation

Add the dependency to your `build.gradle.kts`:

```kotlin
implementation("cloud.mallne.dicentra.weaver:core:1.0.0-SNAPSHOT")
```

## Weaver Object Language (WOL)

WOL is the core of Weaver's transformation capabilities. It comes in two flavors: **Accessor WOL** for path traversal
and **Computation WOL** for logic and arithmetic.

### 1. Accessor WOL

Used to navigate JSON structures and interpolate values.

| Example            | Description                                        |
|--------------------|----------------------------------------------------|
| `user.id`          | Accesses the `id` field of the `user` object.      |
| `items[0]`         | Accesses the first element of the `items` array.   |
| `{otherSchema.id}` | Interpolates a value from another schema.          |
| `{#transform#}`    | Calls a `LimboObject` (a named transformation).    |
| `{##param##}`      | Accesses a parameter passed to the transformation. |
| `{\|#val#\|i\|}`   | Coerces the result of `{#val#}` to an integer.     |

### 2. Computation WOL

Used for logic, arithmetic, and more complex transformations.

| Category           | Examples                                                  |
|--------------------|-----------------------------------------------------------|
| **Arithmetic**     | `1 + 2`, `(10 * {price}) / 2`, `5 % 2`                    |
| **Logic**          | `true && {isAdmin}`, `!{isBlocked}`, `(5 > 2) \|\| false` |
| **Comparison**     | `{age} >= 18`, `'admin' == {role}`, `5 != 10`             |
| **Ternary**        | `{score} > 50 ? 'Pass' : 'Fail'`                          |
| **Elvis Operator** | `{name} ?: 'Anonymous'`                                   |
| **Coercion**       | `\|{count}\|s\|` (ToString), `\|'true'\|b\|` (ToBoolean)  |
| **Functions**      | `pow(2, 8)`, `max({a}, {b})`                              |

## Usage Example

```kotlin
val weaver = Weaver()
val schema = WeaverSchema(
    key = "userProfile",
    root = PropertyDeclaration(
        content = "{#transformedUser#}"
    ),
    transforms = listOf(
        LimboObjectDeclaration(
            name = "transformedUser",
            // Define your transformation logic here
        )
    )
)

val engine = weaver.engine(schema)
val result = engine.execute<UserProfile>(inputJson)
```

## Module Overview

- **`core`**: The main execution engine and schema model.
- **`tokenizer`**: ANTLR-based parser for the Weaver Object Language.

## License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.

---
<p align="center">
  Built with ❤️ by Mallne under the DiCentra umbrella
</p>