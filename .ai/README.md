# `.ai/` -- Agent Documentation for Weaver

This folder is the AI-agent onboarding guide for the Weaver submodule.

## Prerequisites

Read these root `.ai/` articles first:

- `../../.ai/coding-standards.md` -- shared lint/format conventions
- `../../.ai/testing-guidelines.md` -- shared test strategy
- `../../.ai/glossary.md` -- shared domain terms (see WOL, Weaver)

## Index

| Article | Contents |
|---------|----------|
| [architecture.md](architecture.md) | Module structure, data flow, WOL language reference |
| [business-rules.md](business-rules.md) | Module-specific invariants and edge cases |
| [commands.md](commands.md) | Scripts, env vars, local dev setup |

## Published Documentation

Full documentation is published in the DiCentra collection:

- [Weaver Hub](https://docs.mallne.cloud/doc/weaver-m0yVu6OsV2)
- [Tokenizer](https://docs.mallne.cloud/doc/tokenizer-gNEkVqcjas)
- [Core: Engine and Schema](https://docs.mallne.cloud/doc/core-engine-schema-pnfDyGewSG)
- [Core: Execution Model](https://docs.mallne.cloud/doc/core-execution-model-UStO63t1mQ)
- [Core: Plugin System](https://docs.mallne.cloud/doc/core-plugin-system-0o0fIjFEbH)
- [WOL Reference](https://docs.mallne.cloud/doc/wol-reference-1bOWuuzvN4)

## Maintenance

- Update the relevant article on **every** edit that touches architecture, structure, commands, or rules.
- Whenever you discover an **inconsistency** between the docs and the code, fix it here.
- Do not duplicate content that lives in `../../.ai/` -- reference it instead.
