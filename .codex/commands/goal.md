Create or resume the long-running AlphaSeries4j refactor goal.

Use `REFACTOR.md` and the current repository state as the source of truth. If no
active goal exists, create one for the objective below. If an active goal already
exists, continue it without resetting progress. Do not mark the goal complete
until the documented legacy surfaces are gone or intentionally retained behind
stable compatibility boundaries.

Objective:

Refactor the VB6-port-shaped Java code into maintainable Java packages with
typed domain APIs, prepared DAO access, fluent packet builders, smaller classes,
module-level managers/registries, and tests kept passing throughout, while
preserving runtime behavior and source compatibility during each migration
slice.

Required startup checks:

1. Read `REFACTOR.md`.
2. Run `git status --short`.
3. Inspect any uncommitted changes before editing, and do not overwrite user
   work.
4. Re-measure legacy-surface counts when the slice changes those surfaces.

Refactor rules:

- Preserve current source compatibility and runtime behavior for every slice.
  Temporary compatibility shims are acceptable when they keep callers working
  while implementation moves behind typed Java boundaries.
- Follow the package structure already established in this repo and reflected by
  Havana/Roseau-style organization: `dao.mysql` for persistence, `game.*` for
  domain state and module managers, `messages.*` for packet payloads,
  `protocol` for wire encoding/building/parsing, `server.*` for runtime/server
  concerns, and `util` for shared helpers.
- Replace raw `MySQL.Proc_5_*` SQL concatenation with typed DAO methods using
  prepared statements.
- Load database rows into typed classes or records with named fields. Do not
  map or pass tab-delimited row strings such as
  `resultSet.getString(1) + "\t" + resultSet.getString(2)` across new
  boundaries except at an explicit, documented legacy compatibility boundary.
- Rename `Proc_*` and VB-port helper behavior into named Java methods as each
  call path is migrated. Keep old entry points only as compatibility aliases
  while callers are being moved.
- Remove VB-style helper usage and duplicated local string/number conversion
  helpers. Use `StringUtils`, `NumberUtils`, `PacketReader`, `PacketBuilder`,
  `WireEncoding`, and other shared utilities already in the project.
- Build outgoing payloads with fluent packet/payload builders, for example
  `.appendInt(3).appendString("test")`, where protocol support exists. Avoid new
  ad hoc packet string concatenation.
- Split oversized root classes by coherent behavior into small services,
  registries, DAOs, payload builders, or domain state holders.
- Introduce polymorphism where it removes branching around type-specific
  behavior, but keep migrations narrow and behavior-preserving.
- Each domain module should expose one module-level singleton manager or
  registry for its live instances and cached module state instead of adding new
  reliance on `Licence.java` globals. `Licence` accessors are temporary
  compatibility bridges only.
- Keep tests passing. Run `./gradlew test --no-daemon` before committing any
  behavior-affecting slice.

Slice workflow:

1. Pick one small, measurable cluster from the next remaining raw SQL,
   tab-delimited row mapping, payload concatenation, VB-style helper, oversized
   class, or `Licence` global usage.
2. Read the surrounding code and tests before editing.
3. Implement the slice using existing project patterns and minimal compatible
   changes.
4. Update `REFACTOR.md` completed slices and current metrics when the legacy
   surface changes.
5. Run `./gradlew test --no-daemon` for behavior-affecting changes.
6. Run `git diff --check`.
7. Commit only verified milestones with a focused message. Do not commit
   failing or partially verified behavior changes unless the failure is
   explicitly documented and the user requested it.

Progress expectations:

- Continue autonomously through implementation, verification, metric updates,
  and a focused commit whenever feasible.
- Prefer small commits over broad rewrites.
- Report blockers concretely, including the file, command, or test that blocks
  progress.
