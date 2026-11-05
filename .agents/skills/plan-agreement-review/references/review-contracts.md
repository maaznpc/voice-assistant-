# Review Contracts

Read this before starting the loop. Adapt paths and questions, but keep the
evidence, disclosure, restriction, output, and agreement rules.

## Contents

- [Temporary Workspace](#temporary-workspace)
- [Findings Ledger](#findings-ledger)
- [Strategy Baseline](#strategy-baseline)
- [Materiality Test](#materiality-test)
- [Initial Review Prompt](#initial-review-prompt)
- [Re-Review Prompt](#re-review-prompt)
- [Final Agreement Prompt](#final-agreement-prompt)
- [Evidence Gap Pause](#evidence-gap-pause)
- [Evidence Pause Response](#evidence-pause-response)
- [Product Decision Pause](#product-decision-pause)
- [Decision Pause Response](#decision-pause-response)
- [Operational Pause Response](#operational-pause-response)
- [Local Checks](#local-checks)

## Temporary Workspace

Choose a unique review directory using the operating system's temporary-directory
facility. The layout below is illustrative; record the actual absolute path.
Keep temp state together:

```text
<system-temp>/plan-agreement-review-<unique-id>/
├── initial-review-prompt.md
├── rereview-pass-02.md
├── rereview-pass-03.md
├── findings-ledger.md
└── claude-pass-XX.json        # optional raw response
```

Leave the plan in its repo. Read repo evidence in place; do not copy the whole
repo into temp. Temp files are local, untracked, non-secret, and disposable.

## Findings Ledger

Use this shape in `findings-ledger.md`:

```markdown
# Plan Agreement Ledger

- Plan: /absolute/path/to/plan.md
- Repository: /absolute/path/to/repository
- Mode: review-only | revision-loop
- Approved scope: named plan plus explicit evidence paths
- Exclusions: credentials, secrets, PII, production payloads, unrelated files
- Claude session: <session-id after first response>
- Pass: 1 of 5
- Status: reviewing | awaiting-evidence | awaiting-decision | awaiting-approval | revising | ready | stopped

## Strategy Baseline

- Minimum safe outcome: <smallest complete result that preserves required invariants>
- Fixed product decisions and approval provenance:
  - <decision> — <direct user statement or authoritative product-decision source>
- Plan-authored assumptions: <items that remain challengeable>
- Change-surface budget baseline: <existing files/areas, new files, schema, connectors, shared interfaces, jobs/schedulers, operational components, exclusions>
- Current proposed surface and cumulative delta: <comparison with approval state>
- Later-phase requirement map: <phase -> acceptance criterion, approved requirement, or verified constraint>

| ID | Origin | Pass | Claim | Codex classification | Evidence | Plan action | Re-review status |
|---|---|---:|---|---|---|---|---|
| F1 | Claude or Codex plan-review | 1 | Concrete claim | confirmed | path:line | Applied minimal delta | pending |
```

Keep IDs stable. Add a new ID only for a new finding. After re-review, mark each
one `resolved`, `unresolved`, `superseded`, or `not-supported`. Keep history.

Set the overall status when the event happens:

- `reviewing`: before local or Claude review;
- `revising`: before an authorized plan edit;
- `awaiting-evidence`: when evidence or access is needed;
- `awaiting-decision`: when human product intent is needed;
- `awaiting-approval`: when disclosure, scope, authentication, execution, or
  extra-pass approval is needed;
- `ready`: only after the agreement gate passes; and
- `stopped`: after the pass guard or an unrecoverable failure.

## Strategy Baseline

Build this before the first Claude prompt and refresh the current surface after
every material revision. Only direct user decisions or authoritative
product-decision records may be fixed. Repository evidence and safety rules are
constraints, not substitutes for product intent. A choice written only in the
plan is an assumption until its approval provenance is established.

The minimum safe outcome and budget are comparison baselines, not automatic
choices or arbitrary caps. When the current surface grows materially, record
the reason, simpler alternative, budget delta, and explicit reconfirmation or
split before continuing.

## Materiality Test

A finding is material when it could mean:

- the plan misses its goal or acceptance criteria;
- an interface, schema, route, dependency, or file owner is wrong;
- implementation order blocks test-first work;
- validation cannot prove the behavior;
- security, privacy, data integrity, deployment, rollback, or operations are
  unsafe or missing;
- a product choice is hidden or dumped on the implementer;
- the strategy is disproportionate to the approved outcome or a simpler safe
  direction was never evaluated;
- the change-surface budget is materially exceeded without reconfirmation;
- future-only capability remains in implementation scope;
- the first phase is not a minimum safe end-to-end slice or a later phase lacks
  an approved requirement, acceptance criterion, or verified constraint;
- the plan conflicts with current repo evidence.

Usually not material:

- wording with no behavior change;
- an optional future extra already outside implementation scope;
- another implementation that is not safer or simpler;
- unsupported speculation;
- a repeat of a constraint already in the plan.

Unsure? Check more evidence. Do not settle materiality by vote.

## Initial Review Prompt

Write `initial-review-prompt.md` like this:

```markdown
# Independent implementation-plan review

## Objective
Review <absolute-plan-path> against the current repo. Check that it is internally
consistent, correctly scoped, evidence-backed, and executable without guessing.

## Fixed product decisions
- List only approved decisions Claude must not reopen, with the exact approval
  source for each. Do not promote plan-authored assumptions to fixed decisions.

## Strategy baseline
- Give the original goal, approved decision provenance, and current plan
  surface. Ask Claude to identify the minimum safe outcome, budget fit, and
  later-phase requirement mapping independently. Do not include Codex's
  baseline, suspected findings, or preferred verdict.

## Repository instructions
- List the exact instruction files Claude must read first.

## Evidence scope
- List the plan and smallest useful code, tests, schemas, and docs.

## Restrictions
- Read only.
- No edits, writes, Bash, browser, web/network tools, MCP, production systems,
  credentials, runtime state, raw production payloads, or unrelated files.

## Questions
1. Does the plan match the current architecture?
2. Will its files, interfaces, phases, tests, and checks achieve the goal?
3. Is its strategy proportionate to the minimum safe outcome and approved
   requirements? Name any simpler safe alternative the plan failed to address.
4. Does its proposed surface fit the budget? Identify future-only capability,
   unapproved generalization, or material cumulative expansion.
5. Is Phase 1 the minimum safe end-to-end slice, and does every later phase map
   to an acceptance criterion, approved requirement, or verified constraint?
6. What contradictions, missing decisions, unsafe assumptions, or needless
   abstractions remain?
7. Is it ready as written?

## Output
1. Verdict: ready, ready with minor changes, or not ready.
2. Findings by severity. For each: claim, exact file-line evidence, impact,
   smallest fix, and useful alternatives.
3. Verified strengths to keep.
4. Anything not verified within scope.
5. Strategy, budget, YAGNI, and phase-provenance verdict.
6. Clear statement on whether material changes remain.
7. Statement that no files or external systems were changed.
```

Do not include Codex's suspected findings or preferred verdict. Give Claude raw
scope and fixed decisions so it can find issues on its own.

## Re-Review Prompt

For each later pass, create `rereview-pass-XX.md`:

```markdown
# Re-review the revised implementation plan

Re-read <absolute-plan-path>. Scope, exclusions, restrictions, and fixed product
decisions have not changed.

Re-evaluate the latest plan against the minimum safe outcome, approved decision
provenance, current change-surface budget and cumulative delta, and later-phase
requirement map. Do not treat prior acceptance or technical completeness as
proof that the strategy is proportionate.

Codex checked the last critique against current repo evidence and changed the
plan only where supported. Check these IDs:
- F1: short factual note on the applied change
- F2: short reason no change was made, when relevant

Look again for material blockers, contradictions, missing product decisions,
impossible criteria, phase dependencies, future-only scope, unapproved
generalization, and needless cumulative complexity. Do not treat style
preferences as required changes.

Return:
1. Verdict: ready, ready with minor changes, or not ready.
2. Prior findings: resolved, partly resolved, or unresolved, with current lines.
3. New material findings with evidence, impact, and smallest fix, or none.
4. Current strategy, budget, YAGNI, and phase-provenance verdict.
5. Clear statement on whether material changes remain.
6. Statement that no files or external systems were changed.
```

Do not resend old history already held by the Claude session.

## Final Agreement Prompt

In revision-loop mode, once known material findings are handled, use the next
pass as the gate. It counts toward the approved pass limit. Review-only mode
does not use this extra pass when the unchanged plan's initial Claude verdict
and final local `plan-review` are both ready.

```markdown
# Final implementation-readiness review

Re-baseline the latest plan against the original goal and minimum safe outcome.
Check approved decision provenance, strategy fit, proportionality, cumulative
change surface, YAGNI, the minimum safe slice, later-phase necessity, and all
prior material fixes. Search one last time for blockers, contradictions,
missing decisions, impossible criteria, and phase dependencies. Do not treat
prior acceptance as proof. Ignore style-only preferences.

Return:
1. Verdict: ready or not ready.
2. Prior material findings: resolved or unresolved, with current evidence.
3. Remaining material findings with the smallest fix, or none.
4. Strategy, budget, YAGNI, and phase-provenance verdict.
5. Agreement statement: ready without more material changes, or not ready.
6. Statement that no files or external systems were changed.
```

Agreement needs Claude's `ready`, Codex's independent confirmation, and no
unresolved material evidence gaps.

## Evidence Gap Pause

Use this only when a material claim cannot be classified from the approved
evidence scope.

1. Mark the finding `needs evidence`.
2. State exactly what evidence is missing and why it matters.
3. Gather it without asking when it is available inside the approved scope.
4. If user context, access, or expanded scope is required, pause and ask one
   focused evidence question.
5. If the evidence cannot be obtained, stop without claiming agreement.

Do not present an evidence gap as a product decision.

## Evidence Pause Response

Lead with:

`Agreement status: Not reached — evidence pause <before the first Claude pass | after Claude pass n of limit>.`

Include the Claude verdict and session status when available, the Codex verdict,
the exact missing evidence, why it matters, and what was checked.
Ask one focused evidence or access question only when the user must supply it.
Do not present options about product intent or use the Decision Pause Response.

After the user supplies the evidence or access, continue automatically when the
approved scope is unchanged and the pass limit has not been reached. If no
Claude session exists yet, continue to the initial review. If disclosure scope
changes, use the fresh review-identity rule instead of resuming the old session.

## Product Decision Pause

When feedback exposes a real product choice:

1. Mark it `needs product decision`.
2. Explain options and impact; recommend one.
3. Ask one question.
4. Do not ask Claude to choose product intent.
5. Put the answer in the ledger. Update the plan only in revision-loop mode.

## Decision Pause Response

Lead exactly:

`Agreement status: Not reached — loop paused after Claude pass <n> of <limit>.`

Then include:

- Claude verdict, when available.
- Codex verdict.
- The single decision that blocks the next revision.
- Options and material impact.
- Codex's recommended option and reason.
- Confirmation that the persistent Claude session remains resumable.

End with exactly one decision question.

Do not say the review is complete, imply the loop ended, or ask whether the
user wants to continue. After the user answers, update the ledger and plan when
authorized. Resume the same Claude session automatically only when the pass
limit has not been reached and the original review identity and disclosure
scope still fit.

## Operational Pause Response

Use this for disclosure, authentication, scope, execution, or extra-pass
approval blockers.

Lead with:

`Agreement status: Not reached — operational pause <before the first Claude pass | after Claude pass n of limit>.`

State the blocker, the exact approval or action needed, current reviewer
positions when available, and whether the same session can resume. Ask one
focused approval or action question. Do not frame an operational blocker as a
product decision or use the completion report.

After the user provides the approval or action, continue automatically when the
review identity and disclosure scope are unchanged and the pass limit has not
been reached. If the approval changes scope, start a fresh review identity and
Claude session under the coordinator rule. If no Claude session exists yet,
continue to the initial review after the prerequisite succeeds.

## Local Checks

For every pass:

- reopen decisive files and verify cited lines;
- inspect implementation and relevant tests;
- check current library/API claims through approved docs;
- separate repo facts, inference, alternatives, and user choices;
- run the complete local `plan-review` before the first Claude prompt and after
  every material revision;
- at the agreement gate, reuse the latest local verdict only when its plan,
  relevant repository state, requirements, and evidence remain current and
  no new material finding invalidates it; otherwise rerun the review;
- refresh the strategy baseline, current change surface, cumulative delta, and
  later-phase requirement map;
- verify fixed decisions against recorded approval provenance;
- run plan checks and `git diff --check` when useful;
- confirm only the named plan changed;
- preserve unrelated worktree changes;
- update the ledger before the next Claude pass.
