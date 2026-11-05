# Bug Workflow

Use for wrong existing behavior, regression, or incident. Run every call in order.

| Stage | Call | Required handoff | Advance gate |
| --- | --- | --- | --- |
| 1 | `thinking-decompose-problem` | Reported symptom, scope, impact, and available context | Complete decomposition with hypotheses, priorities, gaps, and assumptions |
| 2 | `thinking-investigate-root-causes` | Stage 1 decomposition | Verified or probable causal model with evidence, counterevidence, scope, and uncertainty |
| 3 | `thinking-propose-solutions` | Stage 2 causal report | Human-selected direction with priorities, constraints, validation gates, assumptions, and deferred options |
| 4 | `thinking-challenge-scope` | Selected direction and protected invariants | Human-accepted scope: original or lean version, with exclusions and residual risk |
| 5 | `plan-creation` | All accepted artifacts from stages 1-4 | Complete implementation plan grounded in the causal model and approved scope |
| 6 | `plan-review` or, when available, `plan-agreement-review` | Stage 5 plan, current implementation evidence, and human-selected review route | Fast Review returns `ready`, or Agreement Review passes its Agreement Gate |

## Transitions

- When durable tracking is active, after stage 1 initialize a new record or update the existing record with the decomposition. After stage 2, update it with the causal findings. Use the shared decision, pause, phase-boundary, and finish checkpoints in [Companion workflows](companion-workflows.md).
- Stage 2 requires a verified or probable causal model. Carry remaining uncertainties into stage 3. Pause if no cause is at least probable, or if uncertainty prevents a meaningful comparison under `thinking-propose-solutions`' entry rules. Continue safe read-only investigation when it can resolve the gap; pause when missing evidence needs new authority or user context.
- During stage 3, apply the parent Coordinate section's intent-pause handling;
  a blocked intent gate is not a completed proposal stage.
- Once stage 3 has produced an allowed comparison, pause for solution selection
  unless the user has already explicitly selected a fully disclosed direction.
  Confirm the agreement before stage 4.
- After stage 4, pause for the scope decision. Record what is kept, removed, narrowed, and deferred before stage 5.
- After stage 5, follow [Review stage](review-stage.md). Ask the user to select an available route or confirm Fast Review if it is the only available route; do not choose silently.
- A plan that passes the selected review gate ends this workflow. Do not implement it.
