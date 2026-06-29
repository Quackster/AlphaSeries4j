Resume the AlphaSeries4j refactor goal from `REFACTOR.md`.

Use the current repository state as the source of truth. Before editing, read
`REFACTOR.md`, inspect `git status --short`, and review any uncommitted changes
so you do not overwrite user work.

Goal:

- Continue refactoring the VB6-port-shaped Java into domain packages and typed
  APIs without changing runtime behavior.
- Prefer Havana/Roseau-style organization: persistence in `dao.mysql`, domain
  state and managers in `game.*`, protocol and packet logic in `protocol` and
  `messages.*`, runtime/server concerns in `server.*`, and shared helpers in
  `util`.
- Remove remaining VB-style helper surfaces and `Proc_*` call sites by moving
  behavior behind named Java methods, but keep compatibility shims until all
  callers are migrated.
- Replace raw `MySQL.Proc_5_*` SQL concatenation with typed DAO methods using
  prepared statements.
- Load database rows into typed classes or records with named fields. Do not
  pass tab-delimited row strings across new boundaries except for an explicitly
  documented legacy compatibility boundary.
- Replace ad hoc payload string concatenation with fluent packet/payload
  builders such as `.appendInt(3).appendString("test")` where protocol support
  exists.
- Split oversized classes by coherent behavior, introducing small services,
  registries, and payload builders while preserving source compatibility.
- Use module-level singleton managers/registries for live module instances and
  cached module state instead of adding new reliance on `Licence.java` globals.
- Keep tests passing throughout the refactor.

Workflow:

1. Pick one small, measurable slice from the next remaining raw SQL,
   tab-delimited row mapping, payload concatenation, VB-style helper, or
   `Licence` global cluster.
2. Implement the slice using existing project patterns and minimal compatible
   changes.
3. Update `REFACTOR.md` completed slices and legacy metrics when the legacy
   surface changes.
4. Run `./gradlew test --no-daemon` for behavior-affecting changes.
5. Run `git diff --check`.
6. Commit only when the slice is verified, using a focused commit message.

Do not mark the overall refactor complete until the documented legacy surfaces
are genuinely gone or intentionally retained behind stable compatibility
boundaries.
