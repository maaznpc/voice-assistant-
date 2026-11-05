---
name: plan-creation
description: Create clear, implementation-ready plans with a goal, success criteria, bounded change surface, phases, and risks. Use when the user asks for an implementation, execution, migration, rollout, or similar plan before coding. Also use before presenting a plan drafted in plan mode, even when the user did not explicitly ask for one.
---

# Plan Creation

## Trigger Guard

Use this skill before presenting any implementation, execution, proposed, or plan-mode plan.

## Workflow

Write implementation-ready plans for a skilled developer new to the repo. Include exact files, code, tests, docs, and validation. Use small phases. DRY. YAGNI. Follow repo rules.

Before drafting the plan, identify hidden development constraints and downstream effects the user may not have considered.

Use this as the required output shape for every plan.

Every plan must include:

- Summary/context: Summary of the situation and what we are doing.
- Goal: What is the goal.
- Problem or Need & Evidence: Why the goal is needed. For a bug fix, include
  the supported root cause. For other work, describe the established need or
  constraint.
- Scope Definition:
  - In-Scope: What will explicitly be built, delivered, or updated.
  - Out-of-Scope: What is intentionally left out for this iteration to prevent scope creep.
- Constraints & Operational Boundaries: Constraints prevent a technically correct outcome from being completely useless for the goal.
- Change-Surface Budget: A task-specific forecast of the smallest credible
  implementation surface. Include expected existing files or bounded areas,
  new files, schema or tables, connectors or domains, shared interfaces,
  jobs/schedulers, operational components, explicit exclusions, and material
  uncertainty. Line estimates are advisory unless the user explicitly sets a
  hard cap.
- Definition of Done (Acceptance Criteria):
  - Checklist of questions/conditions required that answer if the goal was achieved and the results are good.
- Phases: ordered chunks with validation.
- Test Plan: concrete behaviors and scenarios to verify.
- Validation Commands: exact commands or checks to run. Use "Not applicable" only when no local validation exists.
- Risks & Notes: blockers, unknowns, safety issues, or follow-ups.
- Assumptions: defaults chosen and constraints the implementer should not reinterpret.

The budget is a decision boundary, not permission to fill the allowance. Avoid
universal file or line caps: semantic complexity and mandatory safety matter
more than raw diff size. If the plan materially exceeds its budget, do not
silently revise the estimate. Show the reason, the budget delta, and the
simpler alternative, then require explicit user approval or split the extra
work into a separate plan.

Creating a new table, cross-connector behavior, shared abstraction, scheduler,
watchdog, durable coordination mechanism, or reusable migration framework is a
scope checkpoint unless the accepted solution direction already requires it.
Identify the approved requirement that forces it. Future-only capability stays
out of scope or becomes an explicit follow-up.

When the plan crosses contexts where a material term, identity, rule, source of
truth, or owner has different local meaning, keep each context's rules inside
its owner. Name the identifier, event, data, or result that crosses the
boundary; specify any translation in meaning or representation; and cover that
contract in the relevant phase, acceptance criteria, and tests. Shared storage
or a common model name doesn't justify global scope. Don't add context-map
ceremony to a single-context plan.

## Preserve Adjacent Behavior

For a bug fix, say what behavior changes and what nearby behavior stays the
same. Preserve existing behavior unless the verified defect, a safety
requirement, or an approved decision requires changing it.

## Revision Stability

When revising an existing plan, preserve the accepted structure and level of
detail unless the user explicitly asks to compress, summarize, or restructure it.

Do not preserve an accepted structure merely because it already exists when
new evidence shows that the strategy is disproportionate, exceeds the approved
change surface, or implements a different solution direction. Surface the
conflict and obtain a new scope decision before carrying that architecture
forward.

Apply critique as a plan delta first, then produce a complete replacement plan
using the same headings as the prior accepted version.

Token efficiency means removing repetition, not removing required sections or
decision-critical implementation details.

## Phase Design

- Before presenting the plan, break it into small phases. Each phase should build on the previous one.
- Make Phase 1 the **minimum safe end-to-end slice**: the smallest complete
  delivery path that demonstrates the intended outcome. For code changes,
  trace entry point to observable behavior. Include every safety, security, data-integrity,
  protected-history, and failure-handling invariant required for that slice;
  do not postpone mandatory safeguards as later polish.
- For every later phase, cite the acceptance criterion, verified constraint,
  or evidence that makes it necessary. Dividing a broad architecture into
  small phases does not make the overall scope proportional.
- You reason best about code you can hold in context at once, and your edits are more reliable when files are focused. Prefer iterative, testable changes over large rewrites.
- Design phases with clear boundaries and well-defined interfaces. Each file should have one clear responsibility.
- In existing codebases, follow established patterns. If the codebase uses large files, don't unilaterally restructure - but if a file you're modifying has grown unwieldy, including a split in the plan is reasonable.
- Refine phases until they are sequential, incremental, and right-sized for safe implementation and validation.
- The final plan should be implementation-ready: a developer can follow it without guessing order or validation.

## Test-First Order

- Follow repo testing rules.
- For bugs and behavior changes with automated coverage, use this loop in each phase: add the smallest public-behavior test, run it and confirm the expected failure, make the smallest production change, rerun green.
- Do not put implementation phases before a later test phase. `Test Plan` summarizes coverage; it is not the final testing phase.
- If test-first is impractical, say why and give the closest safe validation before coding.

## No Placeholders

Every phase must contain the actual content a developer needs. These are **plan failures** — never write them:
- "TBD", "TODO", "implement later", "fill in details"
- "Add appropriate error handling" / "add validation" / "handle edge cases"
- "Write tests for the above" (without actual test code)
- "Similar to Task N" (repeat the code — the engineer may be reading tasks out of order)
- Steps that describe what to do without showing how (code blocks required for code steps)
- References to types, functions, or methods that are neither verified in the
  existing code or documented dependencies nor defined in the plan
