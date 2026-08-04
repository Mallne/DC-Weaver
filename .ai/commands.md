# Commands and Environment -- Weaver

## Scripts

```bash
# Build the entire module
./gradlew build

# Generate ANTLR sources (required after grammar changes)
./gradlew :weaver:tokenizer:generateKotlinGrammarSource

# Run all tests (JVM)
./gradlew :weaver:test

# Run tests for all KMP targets
./gradlew :weaver:allTests

# Publish to local Maven
./gradlew :weaver:publishToMavenLocal
```

## Local Dev Setup

1. Ensure JDK 21+ is installed
2. Run `./gradlew build` from the `weaver/` directory
3. After modifying `.g4` files, run `./gradlew :weaver:tokenizer:generateKotlinGrammarSource` before building

## Environment Variables

No environment variables required for development.

## Runtime Notes

- ANTLR source generation must happen before compilation when grammar files change.
- The tokenizer module generates Kotlin sources from `.g4` files using `antlr-kotlin`.
