# Plan: FKPOC-869 — Workflow-service implementation av flödes-restart

The OpenAPI spec added `POST /handlaggning/{handlaggningId}/process` for restarting
a process instance for an existing handläggning without interrupting any currently running process.
This ticket implements that endpoint in `rimfrost-service-workflow`.

---

## Status: ✅ DONE

---

## Requirements addressed

| Tag | Requirement |
|-----|-------------|
| `FKPOC-869-AC1` | `POST /handlaggning/{handlaggningId}/process` returns 200 with the full `Handlaggning` body when the handläggning exists and the process start message is sent successfully |
| `FKPOC-869-AC2` | `POST /handlaggning/{handlaggningId}/process` returns 404 when no handläggning exists for the given `handlaggningId` |
| `FKPOC-869-AC3` | `POST /handlaggning/{handlaggningId}/process` persists the `replyTo` topic only when `replyTo` is present in the request body, replacing any previously stored value for the same `handlaggningId`; when absent, the existing stored value is retained |
| `FKPOC-869-AC4` | `POST /handlaggning/{handlaggningId}/process` sends a Kafka request message to the erbjudande topic derived from the handläggning's `erbjudandeId`, with the `handlaggningId` as payload |
| `FKPOC-869-AC5` | `POST /handlaggning/{handlaggningId}/process` returns 200 with the `Handlaggning` body even when the Kafka process-start message fails to send |
| `FKPOC-869-AC6` | `POST /handlaggning/{handlaggningId}/process` returns 500 when `replyTo` is provided but persisting it fails |

---

## Out of scope

- Aborting or checking any currently running process — the spec explicitly says the new process starts without interrupting an ongoing one
- Any changes to the `POST /yrkande` flow

---

## Design decisions

The endpoint needs to look up an existing handläggning by ID in order to:
1. Return 404 if it does not exist
2. Retrieve the `erbjudandeId` from the yrkande — needed to look up the correct Kafka process topic via `ErbjudandeTopicAdapter`
3. Return the full `Handlaggning` object in the 200 response

Use `HandlaggningAdapter.readHandlaggning(UUID id)`, which throws `HandlaggningException(ErrorType.NOT_FOUND)`
on a 404. The service catches this and re-throws as `HandlaggningNotFoundException`, which the exception mapper
translates to 404.

`replyTo` is optional — only persist it when present in the request.

Error handling follows the same pattern established in `WorkflowServiceImpl.createYrkande`:
- Handläggning not found → throw `HandlaggningNotFoundException` → 404
- Storage write failure (only when replyTo provided) → throw `HandlaggningReplyTopicWriteException` → 500
- Erbjudande topic lookup failure → throw `ErbjudandeTopicReadException` → 500
- Kafka send failure → log and swallow, return 200 (process start is best-effort)

---

## Step 1 — Release new version of `rimfrost-service-workflow-openapi-jaxrs-spec` and update dependency [x]

**Goal:** The generated `WorkflowControllerApi` with `postHandlaggningProcess` is available in the service.

**Files to create/change:**
- `rimfrost-service-workflow-openapi/openapi.yaml` — make `replyTo` optional in `PostHandlaggningProcessRequest` (remove from `required` list)
- `rimfrost-service-workflow-openapi` — cut a new release (e.g. `0.1.0`) from `main`
- `pom.xml` — bump `rimfrost-service-workflow-openapi-jaxrs-spec` version to the new release

**Deviations:**
- Used `0.1.1` (latest available) rather than `0.1.0`
- `YrkandeControllerApi` was removed in `0.1.1`; renamed `YrkandeController` → `WorkflowController` (implements `WorkflowControllerApi`) and `YrkandeControllerTest` → `WorkflowControllerTest` to restore compile. `postHandlaggningProcess` added as stub throwing `UnsupportedOperationException`.

---

## Step 2 — Write failing tests for service layer [x]

**Goal:** Tests for `restartProcess` exist and fail to compile or fail at runtime — no production code yet.

**Files to create/change:**
- `logic/service/WorkflowService.java` — add stub `HandlaggningDTO restartProcess(UUID handlaggningId, @Nullable String replyTo)` (throws `UnsupportedOperationException`)
- `logic/exception/HandlaggningNotFoundException.java` — new exception for 404 case
- `src/test/resources/mappings/get-handlaggning.json` — WireMock stub for `GET /handlaggning/{handlaggningId}`, covering 200 and 404 responses
- `WorkflowServiceTest.java` — add tests (all expected to fail)

**Tests to add:**
- `FKPOC-869-AC2`: throws `HandlaggningNotFoundException` when adapter throws `HandlaggningException(NOT_FOUND)`
- `FKPOC-869-AC3`: `storeHandlaggningReplyTopic` is called when replyTo is present; not called when absent
- `FKPOC-869-AC4`: `sendRequestMessage` is called with the correct erbjudande topic and handlaggningId
- `FKPOC-869-AC5`: returns `HandlaggningDTO` even when `sendRequestMessage` throws
- `FKPOC-869-AC6`: throws `HandlaggningReplyTopicWriteException` when storage write fails

---

## Step 3 — Implement `restartProcess` in service layer [x]

**Goal:** All Step 2 tests pass.

**Files to create/change:**
- `logic/service/impl/WorkflowServiceImpl.java` — implement: fetch handläggning via `readHandlaggning`, conditionally store replyTo, look up erbjudande topic, send Kafka request

**Note on `HandlaggningException` error types:** only `NOT_FOUND` maps to a distinct HTTP status (404). `BAD_REQUEST`, `SERVICE_UNAVAILABLE`, and `UNEXPECTED_ERROR` all propagate uncaught and resolve to 500 via `CatchAllExceptionMapper` — consistent with how the existing service handles adapter errors.

---

## Step 4 — Write failing tests for REST controller endpoint [x]

**Goal:** Tests for `postHandlaggningProcess` exist and fail — the controller stub throws `UnsupportedOperationException`.

**Files to create/change:**
- `WorkflowControllerTest.java` — add tests (expected to fail: AC1/AC2 get 500 from stub, AC6 passes by accident)

**Tests to add:**
- `FKPOC-869-AC1`: `POST /handlaggning/{id}/process` returns 200 with full handlaggning body on success — mock `restartProcess` to return a `HandlaggningDTO`
- `FKPOC-869-AC2`: `POST /handlaggning/{id}/process` returns 404 when service throws `HandlaggningNotFoundException`
- `FKPOC-869-AC6`: `POST /handlaggning/{id}/process` returns 500 when service throws `HandlaggningReplyTopicWriteException`

**Notes:**
- `WorkflowControllerTest` already has `@InjectMock WorkflowService workflowService` — no extra setup needed
- Add a `createHandlaggningDTO()` helper to `WorkflowTestData` for AC1's mock return value
- Tests hit `POST /handlaggning/{id}/process` via RestAssured (inline, no base-class helper needed)

---

## Step 5 — Implement REST controller endpoint [x]

**Goal:** All Step 4 tests pass.

**Files to create/change:**
- `presentation/WorkflowController.java` — already exists; implement `postHandlaggningProcess`: extract `replyTo` from request (nullable), call `workflowService.restartProcess(handlaggningId, replyTo)`, map result via `PresentationMapper`
- `presentation/exception/HandlaggningNotFoundExceptionMapper.java` — maps `HandlaggningNotFoundException` → 404
- `presentation/util/PresentationMapper.java` — add `toPostHandlaggningProcessResponse(HandlaggningDTO)` (reuses the existing private `toHandlaggning(HandlaggningDTO)` method)

