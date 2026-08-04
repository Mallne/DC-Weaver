# Weaver

**Stack**: KMP library. Schema-driven JSON transformation engine (WOL).

> **Full docs**: [weaver/.ai/](.ai/) | [Notary](https://docs.mallne.cloud/doc/weaver-m0yVu6OsV2)

## Critical Rules

1. Grammar (.g4) changes MUST be accompanied by AST builder updates and tests
2. Keep code in `commonMain` for cross-platform portability
3. Use `@InternalWeaverAPI` for engine internals exposed for plugins
4. All schemas must produce "stable" outputs before serialization

## Build

```bash
./gradlew build
./gradlew :weaver:tokenizer:generateKotlinGrammarSource  # after grammar changes
./gradlew :weaver:allTests
```
