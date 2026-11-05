---
name: thinking-challenge-scope
description: Challenge the scope of a selected solution, proposal, design, or implementation plan and find its smallest safe version. Use when the user asks what can be removed, narrowed, deferred, simplified, or cut substantially, including a 50 percent scope-reduction challenge. Stay read-only and do not replace the selected direction, create an implementation plan, or implement changes.
---

# Challenge Scope

Pressure-test a chosen approach. Find the smallest coherent version that still
delivers its core outcome safely.

This skill stands alone. Don't require, call, or assume output from another
`thinking-*` skill or orchestration flow.

## Establish the challenge boundary

- Start with a selected solution, proposal, design, plan, or other defined
  approach. It can come directly from the user or the current context.
- Identify the outcome, accepted direction, non-negotiable constraints,
  protected invariants, and time horizon. If something important is missing,
  ask one small blocking question only when guessing could materially distort
  the result.
- Keep supplied facts and technical claims at their stated certainty. Inspect
  read-only evidence only when it affects whether some scope is necessary.
- Don't reopen the root cause or choose the original solution. Challenge the
  scope of the chosen approach. If a cut changes its fundamental direction,
  call it a separate alternative that needs a new human decision.

## Define the protected core

First, state what can't be lost:

- the user-visible or operational outcome;
- safety, security, privacy, and data-integrity guarantees;
- financial correctness and protected-history rules;
- required compatibility, recovery, and observability guarantees;
- explicitly approved product or operational constraints; and
- evidence-backed recurrence conditions the solution must address.

Implementation choices, possible future needs, architecture preferences, and
assumptions aren't protected just because they're already written down. Ask
where any material constraint came from.

## Inventory the meaningful scope

List the meaningful parts at the right level for the artifact. Look at things
like capabilities, phases, domains, connectors, persistence, shared
abstractions, jobs, schedulers, rollout machinery, operations, and ongoing
maintenance. Don't invent file or line estimates for a high-level solution.

Classify each material component as one of:

- **Core outcome:** directly delivers the approved result.
- **Mandatory safeguard:** keeps that result safe and reliable.
- **Necessary enabler:** doesn't deliver the outcome itself, but a verified
  constraint or dependency requires it.
- **Future-only:** helps a possible later need, not the current outcome.
- **Unapproved generalization:** widens the solution without an accepted
  requirement or verified constraint.

## Map semantic boundaries when they affect scope

Use a compact context map when the selected approach crosses areas where a
material term, identity, rule, source of truth, or owner has different local
meaning. Skip this step for an ordinary single-context change, and don't invent
contexts just to apply DDD terminology.

For each relevant context, show only:

- the context name and the local concept, rule, or invariant it owns;
- the identifier, event, data, or result that crosses the boundary;
- any translation in meaning or representation at that boundary; and
- why the crossing is required by the core outcome, a mandatory safeguard, or
  a verified dependency.

Challenge every boundary crossing. Shared storage, a common model name,
implementation convenience, or possible future reuse doesn't prove that a
change should apply across contexts. Treat crossings without a current,
evidence-backed need as candidates to narrow, remove, or defer. If removing a
crossing changes the selected direction, identify it as a separate alternative
that needs a human decision.

## Run the substantial-reduction challenge

Try to cut the chosen approach by roughly 50 percent across meaningful scope
dimensions: affected domains, durable components, operational steps, ongoing
maintenance, or implementation surface.

The target is a forcing exercise, not a quota or pass/fail rule:

1. Remove future-only capability and unapproved generalization.
2. Narrow broad or reusable machinery to the verified population and recurrence
   conditions.
3. Defer valuable work that the current outcome doesn't need.
4. Reuse an existing boundary or mechanism when it's just as safe and clear.
5. Replace permanent infrastructure with a bounded approach only when that
   approach is safe under the real operational constraints.
6. Combine or remove phases that only spread complexity around.

For every proposed cut, test whether it:

- preserves the protected core;
- leaves a complete, usable end-to-end result;
- respects verified dependencies and sequencing;
- creates unacceptable manual work, recurrence risk, or recovery difficulty;
  and
- stays within the selected direction.

Never hit the target by cutting mandatory safeguards, tests needed to prove the
outcome, protected-history behavior, failure handling, or necessary cleanup.
Don't fake precision. When scope can't be measured responsibly, describe the
reduction by dimension. If a 50 percent cut isn't safe or supported, state the
largest responsible cut and explain what blocks the rest.

## Construct the lean alternative

Build one coherent lean version, not a loose list of cuts. State:

- what it keeps, removes, narrows, and defers;
- how it still achieves the approved outcome;
- the remaining implementation and operational burden;
- residual risks, manual steps, and follow-up work; and
- anything that looked removable but has to stay, and why.

Compare the lean version with the selected approach using the same protected
core. Smaller isn't automatically better. Recommend the lean version only when
the lower burden outweighs its remaining risk under the user's priorities.

## Output

Use this structure. Leave out empty fields.

```markdown
# Scope Challenge

- **Selected approach:** ...
- **Core outcome:** ...
- **Protected invariants:** ...
- **Accepted constraints:** ...
- **Scope dimensions evaluated:** ...

## Context Map

<!-- Include only when semantic boundaries materially affect scope. -->

<compact context map, required crossings, and unnecessary crossings>

## Scope Inventory

| Component | Classification | Keep / narrow / remove / defer | Reason |
| --- | --- | --- | --- |
| ... | ... | ... | ... |

## Lean Alternative

- **What remains:** ...
- **What changes:** ...
- **Outcome coverage:** ...
- **Reduction achieved:** ...
- **Residual risk and burden:** ...
- **What cannot be removed:** ...

## Decision

- **Recommendation:** ...
- **Strongest case against the lean version:** ...
- **Requires a new solution decision:** yes / no — ...
- **Human decision needed:** ...
```

## Boundaries

- Stay read-only unless the user separately asks for an artifact change.
- Don't investigate root causes, generate unrelated options, create
  implementation tasks, or build the lean version.
- Don't silently edit the chosen approach or treat this challenge as approval
  to change it.
- Don't call another skill or continue into planning automatically.
- Leave the final decision with the human.
