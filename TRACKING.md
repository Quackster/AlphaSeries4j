# AlphaSeries4j Tracking

## Refactor Rules

- Remove indexed parsed-field access such as `handlingField(fields, 0)` and replace it with typed class or record instances that expose explicit field names.
- New or migrated code should not pass around `\t`/`\2` split arrays, positional row strings, or unnamed protocol fields except at a documented legacy compatibility boundary. Parse legacy wire/database rows once, load them into named objects, and use those objects everywhere else.
