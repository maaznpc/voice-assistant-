# Feature Workflow

Use for a new capability or behavior. Run every call in order.

| Stage | Call | Required handoff | Advance gate |
| --- | --- | --- | --- |
| 1 | `thinking-land-to-earth` | Feature idea and supplied context | User-confirmed Grounded Idea Card; a draft card cannot advance |
| 2 | `thinking-decompose-problem` | Stage 1 card | Complete decomposition of the underlying need, context, constraints, dependencies, gaps, and assumptions |
| 3 | `thinking-propose-solutions` | Stage 1 card plus stage 2 decomposition | Human-selected direction with priorities, constraints, validation gates, assumptions, and deferred options |
| 4 | `thinking-challenge-scope` | Selected direction and protected outcome | Human-accepted scope: original or lean version, with exclusions and residual risk |
| 5 | `plan-creation` | All accepted artifacts from stages 1-4 | Complete implementation plan grounded in the feature need and approved scope |
| 6 | `plan-review` or, when available, `plan-agreement-review` | Stage 5 plan, current implementation evidence, and human-selected review route | Fast Review returns `ready`, or Agreement Review passes its Agreement Gate |

## Feature Handoff

The parent invocation is explicit authorization to call `thinking-land-to-earth`. Complete its questions and confirmation before stage 2. If the idea is already concrete, still produce and confirm the card; do not skip the stage.

For stage 3, treat the confirmed card plus decomposition as the feature problem model expected by `thinking-propose-solutions`:

- observed problem = user need or capability gap;
- causal basis = current constraint, dependency, or repeated situation creating the need;
- desired correction = observable feature outcome; and
- recurrence scope = people, situations, or workflows where the need repeats.

Use the child skill's allowed read-only inspection to verify current capabilities, relevant constraints, and whether the gap is real before comparing options. Never invent a technical root cause. If the gap cannot be established, pause for the smallest missing evidence or decision.

## Transitions

- When durable tracking is active, after stage 2 initialize a new record or update the existing record with the confirmed Grounded Idea Card and decomposition. Use the shared decision, pause, phase-boundary, and finish checkpoints in [Companion workflows](companion-workflows.md).
- During stage 3, apply the parent Coordinate section's intent-pause handling;
  a blocked intent gate is not a completed proposal stage.
- Once stage 3 has produced an allowed comparison, pause for solution selection
  unless the user has already explicitly selected a fully disclosed direction.
  Confirm the agreement before stage 4.
- After stage 4, pause for the scope decision. Record what is kept, removed, narrowed, and deferred before stage 5.
- In stage 5, use the feature need and capability-gap evidence as the plan's evidence basis. State that no defect is asserted when applicable.
- After stage 5, follow [Review stage](review-stage.md). Ask the user to select an available route or confirm Fast Review if it is the only available route; do not choose silently.
- A plan that passes the selected review gate ends this workflow. Do not implement it.
