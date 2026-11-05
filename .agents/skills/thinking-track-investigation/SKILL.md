---
name: thinking-track-investigation
description: "Use when the user wants durable investigation tracking: creating a canonical record, preserving supplied context across handoffs, recording supplied progress or corrections, or retrieving the recorded status and next step. Do not use when the user wants evidence gathering, causal analysis, planning, or implementation."
---

# Track Investigation

Create, maintain, or orient from the durable investigation record. Do not
perform the investigation. Preserve the initial decomposition, supplied
results, current state, protected history, and next step across handoffs.

## Boundaries

- Read the user's instructions, the approved record, and only the supplied material needed for the selected operation.
- Create or update only investigation records and their navigation-only
  `README.md` files at exact user-specified or user-approved paths.
- Organize and summarize supplied material without changing its evidentiary meaning or certainty.
- Keep record language plain and short. Relax grammar when meaning stays clear; fragments and shorthand are fine. Do not rewrite supplied content only to polish style.
- Do not inspect external sources, search code or systems, query data, gather evidence, test hypotheses, validate causes, decide a root cause, create a plan, implement, or execute follow-up work.
- If supplied material is ambiguous, preserve it as a note, unknown, or open hypothesis. Do not promote it to a finding.
- Use one canonical record per investigation. Keep large artifacts outside it and link them.
- Keep investigation, tracking, planning, and implementation as separate user-directed operations. Never invoke one of those workflows automatically. A repository-required read-only Git checkout preflight is only a write condition, not authorization for another operation.

## Parent workflow return

When an explicitly requested parent workflow offers durable tracking and the user approves the exact record path, any required navigation-only `README.md` path, and operation scope:

- Perform only the requested `Orient`, `Initialize`, `Update`, or `Checkpoint`, report the result, and return control to the parent. `Stop` ends this tracking operation, not the approved parent workflow.
- One scoped approval may cover `Orient`, `Initialize`, and repeated `Update` and `Checkpoint` operations for those paths during that parent run. It does not cover a different path, `Create a child`, `Close`, Git mutations, investigation, planning, implementation, or unrelated writes.
- Treat stage artifacts supplied by the parent as tracking input. Preserve their evidence and uncertainty without independently validating them.
- The parent decides what resumes. This skill still owns record format, consistency, history, preflight, and write boundaries.

## Git-backed record preflight

Before Initialize, Update, Checkpoint, Create a child, or Close will write an
investigation record inside a Git repository:

1. Read the applicable repository instructions and any required Git workflow.
2. Inspect the repository root, current checkout path, current branch, and
   worktree mapping.
3. Apply branch and worktree requirements only when those instructions define
   them. Verify that the current checkout satisfies any applicable requirements;
   do not invent a branch or worktree convention.

If the check fails, stop before writing. Report the current and required state.
Do not create, switch, move, or remove a branch or worktree automatically.

## Investigation folder README

Use the canonical investigation record as the source of truth. A folder
`README.md` is only a navigation map; it must not become another investigation
record or contain independent findings, history, or authorization.

When an investigation has three or more supporting files outside its canonical
record, create or maintain `README.md` at the approved investigation-folder
path. Include only:

- the linked canonical investigation record and its status;
- the current plan, or `None`;
- historical, implemented, superseded, or closed files with lifecycle labels;
- evidence-only artifacts;
- the governing workflow when deferred work is known;
- the current question or dependency; and
- the canonical record's exact next step.

Do not choose authority from file age, unchecked boxes, filename wording, or
Git timestamps. The canonical record's Current state controls. When the README
conflicts with it, treat the README as stale: report the needed tracking update
during Orient, or reconcile it during an authorized write. Do not edit a
supporting artifact merely to add a lifecycle banner unless the user separately
authorizes that broader document maintenance.

## Investigation hierarchy

When an investigation has a relevant parent or child, check their current
status before reporting or updating progress.

Identify:

- the current active investigation;
- active, closed, or paused children;
- results returned to the parent; and
- stale parent or child summaries.

Use this during Resume, Orient, Update, Checkpoint, and Close.

For a hierarchy status report, use this order:

1. **Status:** Show a compact parent/child tree. Add each status and mark the
   current investigation.
2. **What each means:** Give each record a short summary. Explain its scope,
   what is complete, and what remains or what its closed result returned. Link
   the record when its path is available.
3. **Current:** Name the question or dependency being worked now.
4. **Phase boundary:** When a known fix is deferred, show its `Now`, `Later`,
   `Handoff`, `Governing workflow`, and `Resume requirement` values from the
   record. Omit this item when no fix is deferred.
5. **Completed:** Name the relevant finished work.
6. **Paused:** Name work waiting behind the current investigation.
7. **New leads:** Show leads that need a decision and the recommended handling.
   Omit this item when there are none.
8. **Next:** Give exactly one next step.
9. **Blocker:** Name the needed approval or blocker, or `None`.

Reading linked records does not authorize editing them. If records conflict,
report the tracking update needed. Do not change another record or start new
investigation work automatically.

## Phase boundary

Whenever a known fix is deferred, record and report:

- **Now:** The current investigation scope.
- **Later:** The deferred planning, implementation, or remediation work.
- **Handoff:** The condition and separate approval needed before Later begins.
- **Governing workflow:** The exact supplied skill, file, and section that
  controls Later, or `Unknown`.
- **Resume requirement:** Re-read the governing workflow after a handoff or
  context compaction and before proposing or performing Later.

Keep Later outside the active question and do not start it automatically.
The governing-workflow reference is routing metadata, not authorization. Do not
copy project-specific execution rules into this skill. Record the applicable
workflow supplied by the user or repository; do not assume a particular skill,
branch, or worktree convention. If the
governing workflow is unknown or unavailable, make identifying or loading it
the handoff blocker.

## New leads

When an investigation finds a new issue or question, classify it and recommend
what to do.

Use:

- **Unknown:** Not enough evidence.
- **Hypothesis:** Possible explanation for the current problem.
- **Finding:** Evidence-backed result.
- **Validation:** A check still needed.
- **Child investigation:** A separate question needed to finish the parent.
- **Separate investigation:** An independent question that does not block the
  current work.
- **Follow-up:** The cause is known; planning, implementation, or remediation
  remains.

Explain why, whether it blocks current work, and what approval is needed.

Record or queue the lead, but keep one active question. Do not create another
investigation, start validation, or change the active work without user
approval. Include `New leads` in status reports when any need a decision.

## Choose the operation

- **Orient:** Report current status and, for an active record, one next step without editing it, then stop.
- **Initialize:** Create the root record, import supplied decomposition or context, and stop.
- **Update:** Record newly supplied notes, evidence, hypotheses, findings, or corrections, and stop.
- **Checkpoint:** Consolidate supplied progress before a pause, handoff, or expected context compaction, and stop.
- **Create a child:** Create and link a distinct related investigation at an approved path, and stop.
- **Close:** Record the user-supplied conclusion and closure state when explicitly requested, and stop.

No operation performs the investigation.

## Initialize a record

Run `scripts/initialize_investigation.py` with an installed Python 3 interpreter
from this skill directory. The script uses only the Python standard library.
It creates one Markdown record from
`assets/investigation-record.md` and refuses to overwrite an existing file.

```bash
python3 scripts/initialize_investigation.py \
  path/to/investigation.md \
  --title "Short investigation title" \
  --question "Exact question the investigation must answer"
```

Use `--parent path/to/parent.md` for a child investigation. Optional flags set
the owner, subject, related task, and authorization boundary. Fill the relevant
template fields, import any supplied decomposition, append the initialization
history entry, create the folder README when its threshold is already met,
report the record path, and stop.

## Import a decomposition

Treat a decomposition as initial, non-exhaustive working context rather than
verified truth:

1. Preserve the full supplied decomposition in Appendix A and cite its source.
2. Carry the observed problem, scope, and impact into Investigation contract.
3. Convert possible causes, connections, and causal chains into open hypotheses with stable IDs. Preserve their original uncertainty.
4. Carry priorities into Current state's Active question or Next step, or into Queued work, without treating priority as proof.
5. Carry gaps and assumptions into Current state's Material unknowns and Current evidence coverage.
6. Do not create findings solely from decomposition claims. Create findings only from supplied investigation results that identify their evidence and limits.
7. Add a history entry stating that the decomposition was imported and not independently verified by this skill.
8. Update Current state last, then stop without beginning the investigation.

## Resume from a record

1. When the investigation folder has a `README.md`, read it first for routing,
   then read the canonical record's Current state. Without a README, read
   Current state first.
2. Read every finding and hypothesis referenced there, then the recent history and pending work needed for the requested operation.
3. Read Appendix A completely when this is the first investigation handoff, the selected investigation workflow requires the full decomposition, or the original factors, assumptions, priorities, or provenance are needed.
4. During an ordinary later resume, consult Appendix A only when its historical context is relevant.
5. Treat Appendix A as an immutable source snapshot, not current truth or a current task list. Never edit it after import.
6. When Appendix A conflicts with Current state, Findings, or Hypotheses, use the current working sections and preserve the appendix unchanged.
7. If Appendix A reveals a relevant overlooked lead, report it during Orient. During an authorized write, add a hypothesis or queued item, cite Appendix A, and append a history entry. Never change the appendix.

The complete appendix satisfies a later investigation workflow's need for the
full decomposition. Current working sections control resume after the
consistency gate.

## Orient on status or next step

1. Follow **Resume from a record**.
2. Read the record status.
3. **Active:** Run the consistency gate. Report status first, then exactly one smallest unresolved next step.
4. **Closed:** Report the closed status and final conclusion, then the recorded
   phase boundary (`Now`, `Later`, `Handoff`, `Governing workflow`, and `Resume
   requirement`) and follow-up. When follow-up exists, name exactly one next
   authorization needed. Label recorded branch or worktree state as recorded,
   not currently verified, unless the governing workflow separately verifies
   it during this task. If there is no follow-up, say there is no next
   investigation step.
5. Name any needed approval or blocker. Do not edit the record. Stop.

For conflicting active state, report the conflict as status and give one
smallest tracking action to resolve it. Do not guess an investigation step.

## Current-state consistency gate

Run this before orienting an active record and before saving a record that will
remain active:

- The active question is unresolved, or says `None — ready for closure` when the conclusion is supplied but closure was not requested.
- The next step is open, advances the active question, and does not repeat completed work. For ready-for-closure state, use `Await explicit closure request`.
- Current state matches finding and hypothesis lifecycles, child status, and queued or blocked work.
- When a folder README is required or already exists, it identifies the same
  canonical record, lifecycle states, current dependency, and next step.
- When a known fix is deferred, the `Now`, `Later`, and `Handoff` values are present and consistent with Current state and queued work.
- When Later is not `None`, the Governing workflow and Resume requirement are
  present and reloadable, or Current state identifies the missing dependency as
  the handoff blocker.
- History explains earlier state; it does not direct current work.

When the question is resolved and no open dependency remains, keep the record
active until explicit closure. Use the ready-for-closure values above.

Lifecycle and work-item status constrain Current state. If they conflict:

- **Orient:** Report the conflict. Do not edit or guess.
- **Authorized write:** Reconcile current sections from supplied material, append a correction entry, and preserve earlier history. If support is missing, mark it unknown.

## Tracking loop

1. Follow **Resume from a record**.
2. Read only the new material supplied for this update.
3. Append a chronological entry describing the supplied material and recorded change.
4. Create or update stable hypothesis and finding IDs only as supported by the supplied material.
5. Preserve earlier statements and lifecycle history.
6. Update related, queued, and blocked work.
7. Update Current state.
8. Run the consistency gate, then update Last updated last.
9. When the folder README is required or already exists, reconcile its
   navigation and lifecycle summary from the canonical record.
10. Report what changed and stop.

Do not gather missing evidence or continue the investigation. If an update needs
independent investigation or validation, record it as pending work.

## Hypotheses and findings

Give hypotheses stable IDs such as `H-001` and use:

- `open`: supplied as plausible and not yet resolved;
- `supported`: supplied evidence supports it within the stated scope;
- `invalidated`: supplied evidence shows it is false within that scope; or
- `superseded`: a newer hypothesis replaces or materially narrows it.

Give findings and conclusions stable IDs such as `F-001` and use:

- `active`: part of the supplied best-supported current understanding;
- `disproven`: supplied evidence shows the statement is false within its scope; or
- `superseded`: a newer finding replaces or materially narrows it.

Record who or what supplied the assessment. `supported` and `active` do not mean
universally proven or independently verified by this tracking skill. A hypothesis
is an explanation under investigation; a finding is a supplied evidence-backed
result or conclusion. Do not duplicate one preliminary theory in both sections.

## Protected history

- Never silently delete or rewrite an invalidated hypothesis, finding, conclusion, or material note.
- Keep the original statement, change its lifecycle, cite the supplied contradicting evidence, and link its replacement when one exists.
- Create a new ID for a materially different replacement.
- Add a chronological entry explaining what changed, why, and which source supplied the change.
- Correct old notes with a new entry instead of rewriting history to look right in hindsight.
- Update Current state so historical ideas remain traceable but cannot be mistaken for current understanding.

## Related investigations

Keep an unapproved child or separate-investigation recommendation in Queued
work. To create an approved separate investigation, initialize it as a root
record without `--parent` and list the existing record under Related
investigations. Add the reciprocal link only when both paths are approved.
`Follow-up` describes remaining work; it is not an investigation relationship
type. To create a child:

1. Confirm the exact user-specified or user-approved child path and whether
   updating the parent record is authorized.
2. Create it with `--parent` pointing to the parent record.
3. Add the reciprocal link to the parent only when updating it is authorized.
   If only the child is authorized, keep its parent reference and report the
   reciprocal parent link as pending.
4. Keep each record's current state and history independent.
5. Reconcile the applicable folder README when it is required or already
   exists and its path is approved. Report any unauthorized README update as
   pending.

Related does not mean the same root cause. Do not merge records unless supplied
investigation results establish that relationship.

## Checkpoint

Use only supplied progress to:

1. Append the latest material to Chronological history.
2. Update affected hypotheses and findings.
3. Update queued and blocked work.
4. Update Current state with the active question, next step, and blocker.
5. Update Last updated last.
6. When the folder README is required or already exists, reconcile its
   navigation and lifecycle summary from the canonical record.

The record must contain enough current state and history for another agent to
resume without unavailable chat context. Do not invent progress or resolve an
unknown while creating the checkpoint.

## Close

Close only when the user explicitly requests it. Record the supplied conclusion,
supporting finding IDs, remaining uncertainty, completed and incomplete
verification, and follow-up categories. Set the status to `closed`, add the
closure timestamp, reconcile the folder README when it is required or already
exists, and stop. Do not apply the active-record consistency gate to this status
transition. Do not create a plan or invoke `plan-creation`.
