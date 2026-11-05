---
name: thinking-propose-solutions
description: Develop and compare evidence-grounded solution directions after a root cause or equivalent causal model is established and before implementation planning. Use when the user wants solution proposals, alternatives, trade-offs, an easy versus comprehensive versus target-state comparison, or high-level agreement on what to do. Do not use to investigate causes, silently choose a solution, create an implementation plan, or implement changes.
---

# Propose Solutions

Create a short decision brief that lets a human choose the high-level solution
direction before planning begins.

## Start from an established cause

- Require a verified or probable root cause, or an equivalent evidence-backed
  causal model. If it is missing, recommend `thinking-investigate-root-causes` and
  stop. Do not invoke it automatically.
- Restate the observed problem, causal mechanism, recurrence conditions, scope,
  material evidence gaps, and relevant constraints. Preserve the source's
  certainty; do not promote a probable cause to verified.
- If multiple unresolved hypotheses remain and none is at least probable, do
  not compare corrective or target-state solutions. State that the decision is
  not ready, name the smallest read-only validation needed, and stop. When
  immediate harm requires action, include only a reversible containment
  direction and state that it is not remediation.
- When a probable cause has a material validation gap, compare only options
  that remain viable across that uncertainty and make their dependency on the
  unresolved point explicit. If the gap would materially change viability,
  state the smallest investigation needed and stop.

## Stay at the decision level

- Use read-only inspection when needed to understand feasibility, current
  boundaries, dependencies, and affected people or systems.
- Do not modify files, code, configuration, or data. Do not run state-changing
  tests or operations.
- Do not create an implementation plan, select exact implementation steps, or
  provide file-by-file tasks, code, tests, migration steps, or rollout commands.
- Do not implement, update an investigation record, or invoke another workflow
  automatically.

## Establish the decision criteria

Identify:

- the desired outcome;
- non-negotiable constraints and protected invariants;
- priority trade-offs such as speed, safety, cost, maintenance, user impact,
  reversibility, and long-term flexibility;
- the relevant time horizon; and
- affected people, operators, customers, or systems.

Use only criteria supported by the user or supplied context. Do not silently
optimize for architectural cleanliness, speed, cost, or another agent default.
Ask the smallest blocking question only when a missing priority would make the
comparison misleading. Otherwise show how the choice changes under different
priorities.

When an option crosses contexts where a material term, identity, rule, source
of truth, or owner has different local meaning, keep those meanings separate
and treat every required boundary translation as part of that option's affected
scope, risk, and ongoing burden. A shared table, model name, or implementation
mechanism doesn't by itself justify a cross-context solution. Skip this check
when the decision stays within one semantic context.

## Select the decision view

Keep the comparison method shared. Select the view from the actual decision,
not merely the vocabulary or the tool involved. Read the selected reference
completely before establishing intent and comparing options. Use the first
matching row.

| Context | Load |
| --- | --- |
| Mixed product/software and general decisions | Both references; apply relevant criteria without producing duplicate briefs |
| A product or software system | [Product/software view](references/product-software-view.md) |
| Any other decision | [General view](references/general-view.md) |

These views add criteria; they do not change the causal evidence requirement
or authorize another workflow.

### Establish what is intended

Before setting the minimum safe direction, separate:

- what happens now and why;
- the intended outcome or behavior established by the user or supplied evidence;
- any new outcome, rule, or meaning proposed by an option; and
- what remains unknown about intent and who can resolve it.

An explanation of current behavior does not establish what should happen.
A requested outcome does not automatically settle the policy, workflow, or
representation used to achieve it. Do not call a proposed change a restoration
unless the intended baseline is supported.

Apply this intent gate before presenting or recommending dependent solution
proposals. Resolve intent from explicit user instructions or authoritative
supplied evidence first; do not ask again when it is already established.
An explicit request to compare product or policy directions selects the second
row for that comparison even while intent is unresolved. This permits comparing
the intended outcomes themselves; it does not clear the first-row gate for
dependent technical solution proposals.

| Situation | Action |
| --- | --- |
| Unresolved product or policy intent changes which solutions are viable or makes a recommendation misleading | State the unresolved behavior and its consequence, ask the smallest question needed, and pause the dependent comparison. A disclaimer does not clear this gate. |
| The user explicitly asks to compare product or policy directions | Compare those directions as the decision itself, with concrete consequences and decision ownership. Do not present them as technical implementations of an agreed outcome. |
| Intent is established, but an otherwise viable option introduces an additional product or policy change | Disclose that option's specific change using the selected view's decision label, its consequence, and who must accept it before planning. If the uncertainty instead determines option viability, use the first row. |
| Options preserve established intent | Compare normally without generic product-decision disclaimers. |

An instruction to start this skill or an orchestration workflow, or a ticket
listing alternatives, does not by itself request a product-direction comparison
or settle product intent. When the second row applies, existing recommendation
rules still require supported priorities; technical simplicity cannot choose
product intent on the user's behalf.

Independent comparisons may proceed. Preserve the existing reversible-
containment exception and its limits. Use product terminology only for product
or software decisions; use the general view's labels in other contexts.

## Establish the minimum safe direction

Before proposing broader strategies, state the **minimum safe direction** that
would correct or contain the established causal mechanism while preserving all
non-negotiable invariants. Keep this at the decision level: describe the
behavioral boundary and affected scope, not exact files, line counts, or
implementation steps.

Use it as a baseline, not as a predetermined winner. For every materially
broader strategy, identify the accepted requirement, recurrence condition, or
verified constraint that requires the extra scope. Persistent coordination,
reusable frameworks, generalized interfaces, new operational machinery, and
future fleet capabilities are not justified merely because they could be useful
later.

Apply a proportionality check:

- compare the observed problem, affected population, recurrence conditions,
  and required safety guarantees with the strategy's change surface and
  ongoing operational burden;
- for each broader strategy, explain whether the simpler viable direction
  meets the accepted requirements. If it does, acknowledge its sufficiency;
  justify broader scope only through supported requirements or priorities; and
- defer capabilities that serve only a possible future need.

This is a minimum-change challenge, not a code-golf rule. Never shrink a
direction by omitting required safety, security, data integrity, financial
correctness, protected-history, or recovery guarantees.

## Generate viable strategies

Generate materially different strategies before assigning tiers. Consider,
when relevant:

- no change, defer, monitor, or gather more evidence;
- containment that reduces immediate harm;
- correction of the established causal mechanism;
- removal of conditions that allow recurrence;
- a broader target-state change; and
- staged or hybrid combinations.

Include only options that are responsible and viable within known constraints.
Do not create a weak option to make another look better. An easy option must
still preserve safety, security, data integrity, financial correctness,
protected history, and other applicable invariants.

Aim for two or three useful options. Do not manufacture three when fewer are
materially distinct. Explain briefly when a requested tier has no viable option.

## Classify after generating

Use these labels only when they fit the strategy:

1. **Easy / Containment / Minimal:** Make the smallest safe change that reduces
   immediate harm or restores acceptable behavior. State what cause or
   recurrence risk remains.
2. **Complex / Corrective / Comprehensive:** Address the established mechanism
   or recurrence conditions across the necessary scope. Complexity alone does
   not make this option better.
3. **Optimal / Transformative / Target State:** Pursue the strongest fit for
   explicitly stated priorities and time horizon. Never call an option optimal
   without naming the criteria under which it is optimal.

Options may use another strategy label when these tiers would distort the real
choice. Generate the strategy first; never derive a strategy merely from its
tier.

## Compare consistently

Apply the same scrutiny and evidence standard to every option. For each one,
explain:

- the strategy and high-level change;
- what remains unchanged;
- whether it preserves agreed expectations or introduces a decision identified
  by the selected view, with a concrete before-and-after consequence;
- which part of the causal model it addresses;
- expected benefits and time to value;
- effort, affected scope, and effects on people or systems;
- proportionality to the established problem and accepted requirements;
- major risks, opportunity costs, and ongoing burden;
- reversibility and recovery difficulty;
- residual risk, debt, or recurrence conditions;
- prerequisites, assumptions, and unresolved validation; and
- when a human should choose it.

Separate established facts from forecasts and judgment. Use relative ratings
only when their basis is clear; avoid false numerical precision.

## Recommend without deciding

- Give conditional guidance such as `Choose A when...` for every viable option.
- Recommend one option only when the stated priorities and evidence make it the
  strongest fit. Name the deciding criteria, confidence, assumptions, and the
  strongest case against it.
- If priorities do not identify a clear winner, say so. Do not break the tie
  using an unstated preference.
- Keep the final choice with the human.

## Output

When the intent gate blocks a dependent comparison, return a short pause brief:
- **Status:** Waiting for product intent, or the general view's appropriate label.
- **Established context:** Current behavior and what intended behavior is known.
- **Decision needed:** The unresolved behavior, why it changes solution viability,
  and one question for the human who can decide it.
- **Resume:** Re-enter this skill's intent gate with the answer before comparing
  dependent solutions.

For the blocked comparison, do not fill the proposal template or recommend a
dependent technical solution. Independent comparisons allowed by the intent
gate may use the full brief alongside the clearly separated pause brief;
they do not resolve the blocked decision. For allowed comparisons, use the
following structure and keep it easy to scan:

```markdown
# Decision

- **Problem and causal basis:** ...
- **Decision needed:** ...
- **Established intent and proposed changes:** ...
- **Minimum safe direction:** ...
- **Criteria and priorities:** ...
- **Non-negotiable constraints:** ...
- **Material unknowns:** ...

# Options

| Option | Strategy / tier | Root-cause coverage | Effort / time | Affected scope / risk | Reversibility | Residual burden | Best when |
| --- | --- | --- | --- | --- | --- | --- | --- |
| A | ... | ... | ... | ... | ... | ... | ... |

## Option A — [Name]

- **High-level change:** ...
- **Decision implications:** [Context-appropriate decision label, consequence,
  and decision owner; or a brief statement that agreed behavior is preserved.]
- **What stays unchanged:** ...
- **Benefits:** ...
- **Costs and risks:** ...
- **People or systems affected:** ...
- **Why this scope is necessary:** ...
- **Future-only capabilities deferred:** ...
- **Prerequisites and unknowns:** ...

# Decision Guidance

- **Choose A when:** ...
- **Choose B when:** ...
- **Conditional recommendation:** ...
- **Decision required from the human:** ...
```

Keep material decision implications visible in the user-facing brief, including
when a parent orchestration flow summarizes it. Do not hide them only in a
supporting document or an effort/risk rating. Use the decision labels required
by the selected view.

Omit empty sections and repeat no table content unless detail is needed to make
the decision. Add a small visual only when relationships or sequencing cannot
be understood as easily from the comparison.

## Agreement boundary

When the human selects or revises a direction, confirm:

- the selected direction and why it was chosen;
- the priorities and constraints that govern it;
- the disclosed changes to agreed expectations or behavior that the choice
  accepts, and any separate decisions still unresolved;
- the minimum safe direction and the accepted reasons for any broader scope;
- required validation gates and unresolved assumptions; and
- explicitly deferred or rejected alternatives.

A selection accepts only implications already made explicit. If a material
implication was omitted, disclose it and obtain confirmation before carrying it
into planning. Do not ask again for a decision already explicitly accepted.

Treat this as high-level agreement, not implementation approval. Stop after the
decision brief or agreement confirmation. Continue to `plan-creation` only
through a separate user request. The selected direction becomes a planning
constraint unless later evidence invalidates it; planning must not silently
replace it with a different solution.
