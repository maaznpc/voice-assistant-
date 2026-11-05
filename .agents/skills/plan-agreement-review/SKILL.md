---
name: plan-agreement-review
description: Review an existing implementation plan through a persistent, read-only Claude loop and independent Codex checks. Use for plan consensus, readiness review, or revision to implementation readiness. Revise the plan only when the user explicitly authorizes changes.
metadata:
  targets:
    - codex
---

# Plan Agreement Review

Get one named implementation plan to evidence-backed agreement. Claude reviews.
Codex independently reviews and verifies. Codex edits only in revision-loop
mode. Repo evidence wins over reviewer agreement.

## Load Dependencies

Read these sibling skills completely before acting:

- `../claude-code-reviewer/SKILL.md`
- `../plan-critique-review/SKILL.md`
- `../plan-creation/SKILL.md`
- `../plan-review/SKILL.md`

Follow their disclosure, verification, plan-shape, and revision rules. Do not
copy or weaken them here. Stop if a dependency is missing.

When a dependency runs inside this coordinator, this skill owns user-facing
output. Record dependency-required evidence and metadata in the ledger. Do not
emit a dependency's standalone report between passes.

Also read `references/review-contracts.md` before the first Claude prompt. It
contains the prompts, ledger format, materiality test, and temp layout.

## Scope

- Require one existing local Markdown plan.
- Choose and record one mode before setup:
  - **Review-only:** inspect the plan, report findings and proposed deltas, and
    do not edit it. Use this when the user asks only for review or validation.
  - **Revision loop:** review, apply supported plan changes, and re-review until
    the gate passes or the loop pauses. Use this only when the user explicitly
    asks to revise, update, or reach agreement through plan changes.
- Revision-loop authority allows edits only to the named plan. It does not
  allow implementation work.
- Read the repo instructions and the smallest useful code, tests, schemas, and
  docs before judging feedback.
- Preserve unrelated worktree changes.
- Claude may read only the approved task-scoped plan, code, tests, schemas, and
  docs. Keep unrelated code, production systems, credentials, raw production
  payloads, customer data, and unrelated private context out of its scope.
- Do not change branches, commit, push, open a PR, deploy, or implement the plan.

## Set Up the Loop

1. Resolve the mode, absolute plan path, repo root, instruction files, and
   evidence scope.
2. Tell the user the selected prompt and scoped repo content will go to
   Anthropic's Claude service. Name the included scope and exclusions.
3. Get explicit disclosure approval before calling Claude. Reuse that approval
   only while the plan, repo scope, task, and exclusions stay the same.
4. Follow `claude-code-reviewer` to check CLI availability, authentication, and
   supported flags within the host environment's execution permissions.
5. Use persistent review-loop mode. Save the first JSON `session_id`; use
   explicit `--resume` for every later pass.
6. Create a unique review directory under the operating system's writable
   temporary directory, using its temporary-directory facility. Keep it out of
   Git and free of secrets or sensitive payloads. Record its absolute path.
7. Create `findings-ledger.md` from the reference. Track the mode, plan, repo,
   scope, exclusions, pass count, Claude session, findings, and statuses.
8. Establish the strategy baseline required by the reference: minimum safe
   outcome, fixed decisions with approval provenance, plan assumptions,
   change-surface budget, and later-phase requirement mapping.
9. Run a local `plan-review` pass without sending its conclusions to Claude.
   Record its findings with stable IDs so Claude omissions cannot bypass the
   agreement gate.

## Run the Loop

### 1. Ask for an Independent Review

Use the initial-review contract. Give Claude the plan, evidence scope, repo
instructions, fixed product decisions, restrictions, questions, and output
format. Do not leak Codex's preferred verdict or suspected bugs.

Use `claude-code-reviewer`'s local-files-only profile on every initial and resumed
call. This coordinator prohibits MCP access, browser tools, shell execution,
and network tools other than the Claude service connection. Its stricter profile
wins over the reviewer's optional MCP mode. Keep the selected model, effort,
permission limits, and any user-approved budget fixed across passes.

### 2. Check Every Claim

Use `plan-critique-review` for Claude's claims and `plan-review` for Codex's
independent whole-plan check. Give each material finding a stable ID regardless
of which reviewer found it, then classify it:

- `confirmed`
- `partially confirmed`
- `not supported`
- `needs evidence`
- `needs product decision`

Reopen decisive files locally. Check current library or API claims through the
approved docs path, not memory. Add evidence and status to the ledger.

Reconcile the current plan against the strategy baseline. Recheck
proportionality, YAGNI, the change-surface budget, the minimum safe end-to-end
slice, and every later phase's requirement. Plan-authored selections and
assumptions are not fixed product decisions without recorded approval
provenance.

### 3. Change Only What Evidence Supports

In review-only mode, record the supported plan delta and proceed to the report
without editing. The remaining rules in this section apply to revision-loop
mode.

- `confirmed`: apply the smallest complete fix.
- `partially confirmed`: fix only the supported part.
- `not supported`: leave the plan alone and record why.
- `needs evidence`: continue bounded investigation within the approved scope.
  If required evidence needs user-supplied context, new access, or expanded
  scope, pause and ask one focused evidence question. If the evidence cannot be
  obtained, stop as an unrecoverable failure. Never convert missing evidence
  into `not supported` or `needs product decision`.
- `needs product decision`: first confirm the issue cannot be resolved from
  user-approved decisions with recorded provenance, repository evidence, or
  safety rules. A selection written only in the plan does not resolve human
  intent. If intent is genuinely required, use the Decision Pause Response
  contract, ask exactly one question, and wait.
- Keep the accepted structure unless evidence requires a structural fix.
- Follow every `plan-creation` requirement, including the change-surface
  budget, scope checkpoints, minimum safe slice, and phase provenance. A
  material budget overrun requires explicit reconfirmation or a separate plan.
- Edit with `apply_patch`.
- After each revision, run the useful Markdown, link, structure, and diff checks.

### 4. Reuse the Same Claude Session

After a plan change, write a small re-review prompt. List the finding IDs handled
and ask Claude to check the current plan plus any remaining material gaps.

Resume the saved session ID with every original restriction. Do not use
`--continue`, start another session for the same review identity, resend
retained history, or widen scope. An approved scope change must use the fresh
review-identity rule under `Pause and Reconfirm`.

### 5. Keep Going Until the Gate Passes

In revision-loop mode, repeat: verify, patch, validate, run `plan-review` on the
revised plan, re-review with Claude, and update the ledger. `ready with minor
changes` is not agreement. Check those changes, apply supported ones, and review
again.

In review-only mode, do not enter an edit/re-review cycle. Report whether the
unchanged plan passes the gate and list supported deltas when it does not.

In revision-loop mode, once known material findings are handled, use the Final
Agreement Prompt for the next Claude pass. That pass counts toward the approved
limit. If the limit has already been reached, stop and request an extension
before running it. In review-only mode, the initial Claude review may satisfy
the Claude side of the gate because the plan has not changed; do not add a
redundant final pass.

In revision-loop mode, a `not ready` verdict is not a stopping condition.
Continue verifying, revising, and re-reviewing within the same turn unless a
condition under `Pause and Reconfirm` requires user input. Do not return control
merely to report that a pass failed.

## Agreement Gate

Before evaluating this gate, confirm that the latest local `plan-review`
covers the current plan, relevant repository state, requirements, and evidence.
Reuse its verdict when those inputs are unchanged and no new material finding
invalidates it. Otherwise rerun `plan-review`. Record the basis for reuse or
the new verdict.

Call the plan ready only when all are true:

- Claude's latest verdict is `ready`.
- Claude says no material plan changes remain.
- In revision-loop mode, Claude's latest verdict came from the Final Agreement
  Prompt. In review-only mode, it came from the initial review of the unchanged
  plan.
- The latest local `plan-review` verdict is ready from current repo evidence.
- The latest plan passes strategy fit, proportionality, YAGNI, change-surface
  budget, minimum-safe-slice, and later-phase provenance checks.
- Every fixed product decision has recorded approval provenance.
- Every material budget overrun or scope checkpoint has explicit approval or
  has been removed or split into a separate plan.
- All supported material findings are resolved or superseded with evidence.
- No material evidence gap remains unresolved.
- No product decision is waiting.
- Claude reviewed the latest plan state.
- Relevant plan and repo checks pass.

Style preferences, extras already outside implementation scope, and unsupported
feedback do not block the gate. Future-only work still inside implementation
scope does block it. Never downgrade a real issue just to finish.

## Five-Pass Guard

Stop after five Claude passes unless the user approves more. If the gate still
fails:

1. Stop the loop.
2. Report unresolved IDs, both reviewers' positions, evidence, and pass count.
3. Ask whether to run another bounded set or make a product decision.

Do not claim agreement or silently spend more review budget.

This guard overrides every automatic-resume rule. If a question is answered on
pass five, update the ledger and plan when authorized, but do not call Claude
again until the user explicitly approves another bounded set of passes.

## Pause and Reconfirm

Pause when:

- disclosure scope must grow;
- a new repo, plan, sensitive data type, or unrelated task appears;
- Claude auth or read-only execution fails;
- the plan path changes or several plans must move together;
- material evidence requires user-supplied context, new access, or expanded
  scope;
- a product decision is needed;
- implementation is requested before agreement.

Get the needed approval or answer. Resume the same session only when its original
review identity and scope still fit.

If the plan path, repository, evidence scope, disclosure scope, sensitive data
class, or task changes materially, the original review identity no longer fits.
After fresh disclosure approval, close the old identity as superseded and start
a new temp review, ledger, pass budget, and Claude session for the new scope.
Link the old ledger as read-only history; do not copy its unresolved verdicts
forward as facts. If the user does not approve the new disclosure, stop the old
review without claiming agreement.

A pause is an active, resumable review state, not a completed review. State that
agreement has not been reached, name the Claude pass and verdict, explain the
blocker, and say whether the same session can resume. Never use the completion
report for a pause.

- Use the Decision Pause Response only for a real product decision.
- Use the Evidence Pause Response for missing evidence or access.
- Use the Operational Pause Response for disclosure, authentication, scope, or
  execution failures.

Resume automatically only when the relevant response contract allows it, the
original review identity and disclosure scope still fit, and the five-pass
guard has not been reached.

## Completion or Guard-Stop Report

Use this report only after the agreement gate passes, a review-only assessment
finishes, the five-pass guard stops the loop, or an unrecoverable failure
prevents continuation. Do not use it for a pause.

Lead with whether agreement happened. Include:

1. `Assessment`: material findings and Codex classifications.
2. `Plan Delta`: changes applied and unsupported suggestions rejected.
3. `Plan`: clickable absolute plan link and whether it changed.
4. `Implementation Readiness`: ready or not, plus pending decisions and
   unresolved evidence gaps.
5. `Review Metadata`: pass count, model/effort, read-only limits, local checks,
   and whether the Claude session can resume.

Say that no implementation, production, Git, or deployment work happened. Show
the temp review path, but never present it as the durable plan location.
