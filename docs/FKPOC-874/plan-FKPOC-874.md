# Plan — FKPOC-874: Make replyTo mandatory

## Revised requirements from FKPOC-869

| Tag | Original requirement (FKPOC-869) | Revised by this ticket |
|-----|----------------------------------|------------------------|
| `FKPOC-869-AC3` | `replyTo` is persisted only when present; when absent the existing stored value is retained | **Superseded**: `replyTo` is now mandatory in both `POST /yrkande` and `POST /handlaggning/{id}/process`. Omitting it or sending a blank value returns 400. The "retain existing value when absent" branch no longer exists. |

---

## Context

`replyTo` is the Kafka reply topic stored when a handläggning process starts and used to send the result back when the process completes. It was previously optional in both `POST /yrkande` and `POST /handlaggning/{id}/process`.

The OpenAPI spec was bumped to 0.1.2 (already done on this branch). Comparing 0.1.1 → 0.1.2:

- `PostYrkandeRequest.replyTo` gained `@JsonProperty(required=true)` + `@NotNull` + `@Size(min=1)`
- `PostHandlaggningProcessRequest.replyTo` gained the same annotations, plus a new all-args constructor
- `WorkflowControllerApi` has `@Valid` on both request body parameters

However, `quarkus-hibernate-validator` was missing from `pom.xml` (only the bare `jakarta.validation-api` was present), so `@Valid` had no runtime effect. Even after adding the extension, Quarkus RESTEasy does not propagate `@Valid` from interface methods to the concrete implementing class at runtime. The 400 enforcement was therefore implemented via explicit guards in `WorkflowController`.

A pre-existing bug was also discovered: `CatchAllExceptionMapper` was catching `WebApplicationException` (and its subclasses such as `BadRequestException`) and returning 500, which would have swallowed any intentional 4xx response from the application.

---

## Steps

### Step 1 — Remove @Nullable from service interface

`WorkflowService.java:25` declares `restartProcess(UUID handlaggningId, @Nullable String replyTo)`.

- Remove `@Nullable`
- Remove the import for `jakarta.annotation.Nullable` if it becomes unused
- Update the Javadoc: remove the sentence about `null` meaning "don't update"

File: `WorkflowService.java`

---

### Step 2 — Remove null guard from service implementation

`WorkflowServiceImpl.java:105–112`: `restartProcess` has an `if (replyTo != null)` guard before `storeHandlaggningReplyTopic`.

- Remove `@Nullable` from the override signature
- Remove the `if (replyTo != null)` block and call `storeHandlaggningReplyTopic(handlaggningId, replyTo)` unconditionally
- Remove unused `@Nullable` import

File: `WorkflowServiceImpl.java`

---

### Step 3 — Update WorkflowServiceTest

The service tests have several cases that pass `null` as `replyTo` — these are now invalid.

- **Remove**: `should_not_store_reply_topic_when_reply_to_is_absent_during_restart_process` — tests the null branch that is being deleted
- **Fix**: four remaining tests that call `restartProcess(handlaggningId, null)` — replace `null` with `WorkflowTestData.REPLY_TO`

File: `WorkflowServiceTest.java`

---

### Step 4 — Update WorkflowControllerTest and fix pre-existing issues

**Unplanned: `pom.xml`** — replaced `jakarta.validation-api` (bare spec API, no runtime) with `quarkus-hibernate-validator` (Quarkus extension with RESTEasy integration). Without this, `@Valid` annotations in the generated JAR had no runtime effect.

**Unplanned: `CatchAllExceptionMapper`** — fixed a pre-existing bug where all `WebApplicationException` subclasses (including `BadRequestException`) were caught and returned as 500. Added a pass-through for `WebApplicationException` so it returns its own status code.

**`WorkflowController`** — added `requireReplyTo()` guard (throws `BadRequestException` for null or blank) called from both `postYrkande` and `postHandlaggningProcess`, since Quarkus RESTEasy does not inherit `@Valid` from interface methods at runtime.

**`WorkflowControllerTest`** — updated existing process tests to use `new PostHandlaggningProcessRequest(WorkflowTestData.REPLY_TO)` so validation passes and the service mock is reached. Added two new 400 tests:
- `POST /handlaggning/{id}/process` without `replyTo` → 400
- `POST /yrkande` without `replyTo` → 400

**`WorkflowTestData`** — extracted `public static final String REPLY_TO = "replyTopic"` and replaced all hardcoded reply-to strings across `WorkflowTestData`, `WorkflowServiceTest`, and `WorkflowControllerTest`.
