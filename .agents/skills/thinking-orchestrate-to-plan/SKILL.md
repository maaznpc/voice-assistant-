---
name: thinking-orchestrate-to-plan
description: "Orchestrate a reported bug or new feature through mandatory thinking skills into an implementation plan with a user-selected final review: one local plan review or, when its dependencies are available, Codex-Claude agreement review. Use when the user wants the full investigation-to-reviewed-plan workflow without calling each skill manually. Route one workflow, keep stage context, continue automatically, and pause only for human decisions, disclosure approval, missing authority, or blocking evidence."
metadata:
  targets:
    - codex
---

# Orchestrate To Plan

Run one complete thinking workflow. Child skills own their stage. This skill owns routing, companion coordination, handoffs, pauses, and the final review gate. Stop before implementation.

## Route

| Input | Workflow |
| --- | --- |
| Existing behavior is wrong, regressed, or caused an incident | Read [Bug workflow](references/bug-workflow.md) |
| A new capability or behavior is wanted | Read [Feature workflow](references/feature-workflow.md) |
| A proposed feature is a possible fix for wrong behavior | Bug workflow; keep the feature as a solution candidate |
| Bug correction and independent feature expansion are mixed | Ask which outcome to plan first |

Use supplied context and safe read-only discovery before asking. If still ambiguous, ask one routing question. Load only the selected workflow. One run produces one plan.

## Check Dependencies

Before stage 1, confirm that every skill required by the selected workflow is available,
including `plan-review` for the bundled review route. If a required skill is missing,
report its name and pause before beginning the workflow. Do not install dependencies
or skip a mandatory stage automatically.

`plan-agreement-review` and its `claude-code-reviewer` dependency are bundled.
Agreement Review is optional and requires an installed, authenticated Claude CLI.
Check its skill, dependencies, and required tools through read-only inspection before
presenting it as available. Availability does not authorize external disclosure.
Optional tracking and complementary skills must also be available before being offered.
If an already-selected route becomes unavailable, pause and report the missing
requirements; never silently substitute another route.

## Show Workflow Progress

Before the first child call, tell the user what was selected, why, and the full path:

```text
[Workflow: Bug | Feature]
Why: <short routing reason>
Path: <mandatory skill calls in order, ending with the stage 6 review choice>
Tracking: <Not needed | Awaiting opt-in | Awaiting path/scope | Declined | Active - record path>
Git workflow: <Not applicable | Preflight pending | Passed - checkout | Paused - reason>
```

Show status when routing completes, a stage starts or finishes, the workflow pauses or resumes, and the plan becomes ready. Keep it short and derive it from the run ledger:

```text
[Bug | Feature · Progress <completed>/<total>]
Done: <completed stage calls with one short result, or None>
Working: <stage n/total · skill · current purpose>
Next: <next stage or approval gate>
Tracking: <state and record path>
Git workflow: <state and checkout>
```

At a pause, replace `Working` with `Paused`, name the one decision or blocker, and show `Next after approval`. On completion, show all stages done and `Next: implementation approval`. Do not repeat full stage artifacts in status updates.

If new evidence changes the route, announce the new workflow and reason before continuing. Reconcile which completed stages remain valid; never switch silently or mark an unvalidated stage done.

## Companion Workflows

After routing, read [Companion workflows](references/companion-workflows.md). Apply the repository workflow when its scope matches. Suggest durable tracking once when its trigger matches and its skill is available; after approval, keep it active for the run without counting its operations as workflow stages.

Companions preserve state or govern where work happens. They never replace or satisfy a mandatory thinking stage.

## Coordinate

- The initial invocation authorizes every mandatory child-skill call before stage 6, task-scoped read-only inspection, applicable read-only Git preflight, and creation of this run's plan. It does not authorize tracker writes, Git mutations, implementation, external disclosure, or unrelated actions.
- At stage 6, ask the user to choose among available review routes. If Agreement Review is unavailable, explain the missing requirements and ask whether to proceed with Fast Review. The choice authorizes that review route only. Fast Review stays read-only unless the user separately approves a patch to an exact named local plan. Agreement Review authorizes evidence-supported edits only to its named plan, but not disclosure to Anthropic; its exact disclosure scope still needs explicit approval.
- Read each child `SKILL.md` completely when its stage starts. Apply its method, evidence rules, stop conditions, and output contract.
- Run every stage in order. No optional stages. An existing artifact counts only after that stage validates it as current and complete.
- A child rule such as `stop`, `stand alone`, `never invoke another skill`, or `separate user request` ends that child stage or approved companion operation. It does not end this explicitly invoked parent workflow. The parent continues only after the current gate passes.
- Child approval, evidence, safety, and authority gates still apply. The parent cannot weaken them.
- Before stage 3 presents proposals, apply `thinking-propose-solutions`'s
  “Establish what is intended” gate. The child owns that classification.
- Distinguish `Waiting for product intent` (or the child's general-decision
  label) from `Waiting for solution selection` in the run ledger and progress.
  An intent-blocked stage is incomplete: surface the child's question and pause
  dependent work without marking stage 3 done or advancing to stage 4.
- After the answer, record it through any already-authorized tracking operation
  and resume stage 3 at its intent gate. The answer settles only the disclosed
  decision; it does not implicitly select a solution or authorize implementation.
  If it also explicitly selects a fully disclosed solution, apply the child's
  Agreement boundary without asking for the same selection again.
- Keep a compact run ledger: route, current stage, artifact references, decisions, assumptions, blockers, and plan location. Use a task-scoped temp file outside Git when a pause or context compaction could lose state.
- Child reports are stage artifacts. Give short progress updates, then continue. Do not return control just because a stage completed.
- The stage 5 plan must be usable without conversation history. It must carry the approved objective, necessary evidence and decisions, exact change paths, applicable interface contracts, constraints, and verification commands with expected outcomes. Stage 6 checks this completeness. Unresolved decisions that affect scope, architecture, or acceptance prevent readiness; resolve them through investigation or the appropriate human decision.
- Stage 6 uses one active route from [Review stage](references/review-stage.md): one local `plan-review` attempt or, when available, `plan-agreement-review` in revision-loop mode. Do not add a standalone `plan-review` before an initially selected Agreement Review; that skill owns its local review internally. A failed Fast Review may transition to Agreement Review only when its dependencies are available and the user explicitly switches. Never implement code here.

## Complementary Skills

When clarity, fresh framing, bias review, explanation, critique checking, or a visual could materially help, read [Complementary skills](references/complementary-skills.md).

- Complements never replace, satisfy, reorder, or become a mandatory stage.
- Suggest at most one available complement at a natural stage boundary. Say what it adds and where the main workflow resumes.
- Opt-in starts the complement's own entry gates; it does not replace required invocation, mode, disclosure, path, or edit approval. Read its `SKILL.md` completely.
- If timing matters, pause for the choice. Otherwise mention it without blocking and continue.
- A declined complement stays declined unless material context changes.

## Pause

Pause only when:

- meaning, priority, routing, solution, or scope needs a human decision;
- durable tracking needs opt-in, an exact record and possible navigation-README path, or expanded operation scope;
- an applicable Git preflight fails or needs a Git-state change;
- a timing-sensitive complementary-skill choice needs an answer;
- a required skill or a dependency of the selected route is unavailable;
- stage 6 needs a choice among available review routes or confirmation to use Fast Review alone;
- a child requires a user answer or confirmation;
- required evidence needs user context, new access, or a state-changing check;
- authority, task scope, data scope, cost, or external disclosure must expand;
- Agreement Review needs initial or expanded Anthropic disclosure approval, a new review identity, or more than five Claude passes;
- a protected invariant or product behavior needs a tradeoff; or
- the reviewed plan is ready and implementation needs approval.

At a pause, ask one question. Include the established context, options, recommendation when supported, and what resumes after the answer.

## Finish

Ready means every mandatory stage passed, decisions are recorded, and the latest plan passed the selected stage 6 readiness gate. Return the basis, approved direction and scope, plan, selected review route, verdict, applicable review metadata, and remaining assumptions. Stop before implementation.
