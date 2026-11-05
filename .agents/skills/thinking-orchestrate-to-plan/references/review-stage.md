# Review Stage

Stage 6 requires the user to select an available review route or confirm Fast Review when it is the only available route. Ask after stage 5 creates the plan. Keep only one route active and do not run both by default. After a failed Fast Review, the user may explicitly switch to Agreement Review when its dependencies are available; record the transition and make Agreement Review the selected readiness gate.

## Ask

Recheck the review skills and required tools before offering a route. If an already-selected
route is unavailable, pause and report its missing requirements without substituting
another route.

When both routes are available, show the current progress and ask:

```text
[Stage 6/6 · Choose plan review]
Fast Review — one local `plan-review` attempt; no Claude, disclosure, or automatic plan edits.
Agreement Review — `plan-agreement-review` revision loop; supported plan edits, separate Anthropic disclosure approval, and up to five Claude passes.
Recommendation: <route and one risk-based reason>
Which do you want?
```

When Agreement Review is unavailable, identify the missing skill, dependency, or tool
and ask whether to proceed with Fast Review. Do not present the unavailable route as
executable or install its dependencies automatically. Preserve any earlier explicit
selection; do not ask again unless availability or scope has changed.

Recommend Fast Review for a small, isolated plan with a clear path, low blast radius, and no sensitive or high-risk behavior. Recommend Agreement Review, when available, for data mutation or migration, payroll, billing, authentication, protected history, shared or hot paths, cross-cutting changes, material uncertainty, or high failure cost. The recommendation never replaces the user's choice.

## Fast Review

1. Read and run `plan-review` once against the stage 5 plan and current evidence.
2. Keep it read-only. Do not edit the plan or call Claude.
3. Complete stage 6 only when the verdict is `ready`.
4. If the verdict is `ready-with-fixes` or `not-ready`, pause and report the blocking result. Ask whether to end as not ready, authorize the smallest patch to an exact named local Markdown plan plus one fresh Fast Review attempt, or explicitly switch to Agreement Review if its dependencies are available. Never claim readiness.

A local Markdown plan is required only when the user, repository, or plan workflow requires one. Fast Review may inspect the current stage 5 artifact directly, but it cannot patch an unsaved artifact. A patch choice needs separate approval naming the exact plan path.

## Agreement Review

1. Confirm `plan-agreement-review`, its dependencies, and required tools are available; otherwise pause and report the missing requirements. Require one named local Markdown plan. If none exists, propose an exact path and pause for approval before writing it.
2. Read `plan-agreement-review` and all its dependencies. Use revision-loop mode; the review choice authorizes evidence-supported edits only to the named plan.
3. Show the exact prompt purpose, plan, repository evidence scope, and exclusions that will go to Anthropic. Get explicit disclosure approval before the first Claude call. Choosing Agreement Review alone is not disclosure approval unless the same question names that exact scope and explicitly asks for it.
4. Let `plan-agreement-review` own its local `plan-review`, Claude review, critique validation, supported revisions, persistent session, final agreement pass, Agreement Gate, and five-pass guard.
5. Continue automatically within the approved review identity until the gate passes or the child requires a product decision, evidence or scope expansion, fresh disclosure approval, operational input, or more than five Claude passes.

A failed pass is progress, not stage completion. Stage 6 completes only when the Agreement Gate passes on the latest plan.

## Finish

Record the selected route and verdict. Include Agreement Review metadata when applicable. Stop before implementation.
