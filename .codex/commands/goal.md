Start or resume the AlphaSeries4j refactor goal.

This command is for long-running continuation work. Treat `REFACTOR.md`, the
current git state, and the tests as the source of truth. If no active Codex
goal exists, create one with the objective below. If one already exists,
continue it without resetting progress. Do not mark it complete until the
legacy surfaces listed in `REFACTOR.md` are removed or intentionally retained
behind stable compatibility boundaries.

Objective:

Refactor AlphaSeries4j from VB6-port-shaped Java into maintainable Java
packages with typed domain APIs, prepared DAO access, fluent packet builders,
smaller classes, module-level managers/registries, and passing tests, while
preserving runtime behavior and source compatibility for each migration slice.

Startup:

1. Read `REFACTOR.md` before choosing work.
2. Run `git status --short`.
3. Inspect existing uncommitted changes before editing files. Assume they are
   user work or unfinished prior-agent work, and do not overwrite them.
4. If the worktree already contains a partial refactor slice, finish or
   stabilize that slice before starting a different one.
5. Measure legacy-surface counts when the selected slice changes those
   surfaces.

Refactor rules:

- Preserve current source compatibility and runtime behavior for every slice.
  Temporary compatibility shims are acceptable when they keep callers working
  while implementation moves behind typed Java boundaries.
- Follow the package structure already established in this repo and reflected
  by the Havana/Roseau-style organization: `dao.mysql` for persistence,
  `game.*` for domain state and module managers, `messages.*` for packet
  payloads, `protocol` for wire encoding/building/parsing, `server.*` for
  runtime/server concerns, and `util` for shared helpers.
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
  `.appendInt(3).appendString("test")`, where protocol support exists. Avoid
  new ad hoc packet string concatenation.
- Split oversized root classes by coherent behavior into small services,
  registries, DAOs, payload builders, or domain state holders.
- Introduce polymorphism where it removes branching around type-specific
  behavior, but keep migrations narrow and behavior-preserving.
- Each domain module should expose one module-level singleton manager or
  registry for live instances and cached module state instead of adding new
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
