# Plan Explanation Branch

Use this branch when `SKILL.md` routes an implementation-plan explanation here.
Explain what the plan proposes so a reader can understand the relevant part or
the complete solution without reading the full plan. Preserve the plan's
decisions, uncertainties, and scope boundaries.

## Boundaries

- Read the complete current plan before summarizing it.
- Treat the plan as the source for the explanation, not as verified truth.
- Say `the plan states`, `proposes`, or `assumes` when a claim was not
  independently verified.
- Do not inspect code or production data merely to explain the plan.
- Do not review feasibility, correct the plan, expand scope, or implement it
  unless the user separately asks.
- If the user asks whether the plan is safe, correct, complete, or ready, use
  `plan-review` as a separate workflow.
- If the user asks to review an implemented pull request or diff, use the
  applicable PR-review workflow instead.

## Method

1. Identify the problem or user-visible behavior the plan addresses.
2. Extract the intended outcome and the explicit scope boundary.
3. Choose the simplest example that explains the problem: one input or actor,
   one decision, and one visible result. Use a plan example when available.
   Otherwise, label it `Illustrative` and use neutral placeholders without
   adding unstated behavior.
4. Show the example's current and proposed behavior in a small before-and-after
   flow.
5. Use the smallest visual that improves shared understanding. Prefer a text
   flow or table; use Mermaid when multiple steps, branches, or relationships
   are clearer visually. Keep every element faithful to the plan.
6. Reduce implementation details to the smallest responsibility-based solution
   shape that preserves ownership and boundaries.
7. When an important term, identity, or rule changes meaning across domains,
   teams, or systems, name the relevant contexts, keep their meanings and rules
   separate, and preserve any translation at the boundary.
8. When multiple responsibilities interact, trace the primary movement of
   control, data, or state from the initiating actor or input to the visible
   result.
9. Preserve design rationale, alternatives, and accepted trade-offs when the
   plan states them or clearly depends on them.
10. Extract precedence, guards, branches, fallbacks, and exception behavior only
   when the plan contains meaningful conditional decisions. Do not convert
   linear implementation steps into decision rules.
11. Convert success criteria into observable behavior, not file or class names.
12. Preserve important risks, assumptions, unknowns, and decisions still needed.
13. Remove code-level detail that does not change shared understanding.

When main routing also loads `software-visualization.md`, use that reference to
select the visual format while keeping this plan contract authoritative:

- Let visuals support the applicable plan sections; do not replace those
  sections unless the user explicitly requests another output format.
- Prefer a diff or shallow file tree for current-versus-proposed software
  structure and Mermaid for multi-component interaction.
- Label code, structure, or behavior absent from the plan `Illustrative`; do not
  present it as an approved implementation decision.
- Replace the normal plan brief with a focused HTML artifact only when the user
  explicitly requests that format.

Do not force every section when the source has no meaningful content for it.
Never invent a missing goal, non-goal, decision, rationale, trade-off, or
acceptance criterion. Mark it as `not stated in the plan` only when the omission
itself prevents shared understanding.

## Default output

Use `Standard` by default and keep it between 450 and 700 words, readable in
about three minutes. Keep `Detailed` between 700 and 1,000 words. For `Standard`
and `Detailed`, use the applicable headings below in order unless the user
requests another format. Omit unsupported conditional sections instead of
emitting empty headings or filler. Do not pad an explanation to reach a length
range. Avoid repeating the same fact in multiple sections. Include one simple
positive example at every depth, plus one important negative case when it
clarifies a safety boundary. Put the simple example before implementation
details.

### Thirty-second summary

Explain the problem, the proposed solution, and the main safety behavior in two
to four sentences.

### Problem

State the observed or intended problem without embedding the proposed solution.

### Goal

State the outcome the plan intends to achieve and who or what benefits.

### Before and after

Show the selected example here by default. Keep it to one input or actor, one
decision, and one visible result. Use a compact text flow for linear behavior:

```text
Current:  input -> current decision -> unwanted result
Proposed: input -> new decision -> intended result
```

Use a table or Mermaid diagram when it is clearer. A visual supports the
explanation; it does not replace the applicable sections.

### Solution shape

Show the smallest set of responsibilities needed to understand the proposed
solution and how they relate. Explain ownership and boundaries rather than
listing files, classes, tests, phases, or implementation tasks. Prefer domain
concepts and use a compact responsibility map, call tree, component tree,
shallow file tree, context map, or prose only when it improves understanding.
When meanings or rules differ across contexts, show which context owns each
concept and keep those local meanings separate. A simple solution may have only
one or two responsibilities; do not invent components to meet a numerical
minimum.

### How the solution works

Include this section only when understanding the plan requires following
control, data, or state through multiple responsibilities. Explain one primary
end-to-end path from the initiating actor or input to the visible result.
Include one materially different failure, fallback, or negative path when it
changes understanding. When the path crosses a context boundary, name the
information or event that crosses and explain any material translation in
meaning. Do not repeat the user-visible comparison from `Before and after`;
explain the internal interaction that produces the proposed behavior.

### Design rationale and trade-offs

Include this section only when the plan states or clearly depends on meaningful
design reasoning, alternatives, or accepted costs. Explain why the plan selects
this shape, what it gains, what complexity or limitation it accepts, and which
alternatives it explicitly rejects or defers. Preserve the plan's certainty. Do
not independently declare the design optimal or invent rationale absent from
the source.

### Decision rules and fallbacks

Include this section only when the plan contains meaningful precedence, guards,
branches, fallbacks, exception handling, or uncertainty behavior. Present the
rules in execution or precedence order and use a compact table when it improves
clarity. Do not convert linear implementation steps, responsibilities,
non-goals, or acceptance criteria into artificial decision rules. Keep every
rule faithful to the plan, identify the context that owns it when material, and
do not flatten materially different cases.

### Non-goals

List what the plan explicitly excludes, defers, or leaves unchanged. Include
adjacent problems that a reader could reasonably mistake as solved.

### Acceptance criteria

Describe observable proof that the plan worked. Prefer concrete input-to-output
examples, safety invariants, negative cases, and repeatability over internal
implementation steps. When contexts use different meanings or rules, cover the
relevant behavior inside each context and the translation at their boundary.
Render the heading exactly as `Acceptance criteria`, not `What success looks
like` or another synonym.

### Risks, assumptions, and open decisions

Include only items that can change expectations, rollout safety, or whether the
solution is complete. Distinguish:

- `Risk`: something that may go wrong even if implemented as planned.
- `Assumption`: something the plan relies on but does not establish.
- `Open decision`: a choice still requiring an owner.

## Section selection and ownership

Include conditional sections only when the source contains the relationship
they are responsible for explaining:

| Source contains | Include |
|---|---|
| Responsibilities, ownership, or boundaries | Solution shape |
| Domain-specific meanings, rule ownership, or translations | Solution shape; add How the solution works when the boundary crossing is dynamic |
| Multi-component control, data, or state movement | How the solution works |
| Explicit rationale, alternatives, costs, or trade-offs | Design rationale and trade-offs |
| Precedence, guards, branches, exceptions, or fallbacks | Decision rules and fallbacks |

Keep each fact in the section that owns its explanatory purpose:

| Section | Question it owns |
|---|---|
| Before and after | What observable behavior changes? |
| Solution shape | Who owns what? |
| How the solution works | How does control or data move? |
| Design rationale and trade-offs | Why this shape? |
| Decision rules and fallbacks | What happens under different conditions? |
| Acceptance criteria | What proves it worked? |

## Depth controls

- `Brief`: Return the thirty-second summary, goal, before and after when useful,
  solution shape when meaningful, non-goals, and acceptance criteria in 250 to
  400 words.
  Include a conditional decision rule, risk, assumption, or open decision only
  when it materially changes the summary. Do not place risks, assumptions, or
  open decisions under acceptance criteria.
- `Standard`: Use all applicable sections in 450 to 700 words; do not emit every
  possible heading by default.
- `Detailed`: Use all applicable sections in 700 to 1,000 words. Add detail only
  where it improves understanding of the solution shape, relationships,
  sequence, boundaries, examples, rationale, trade-offs, or uncertainty.
  Detailed does not authorize source expansion, correctness review,
  investigation, or implementation.
- Treat `TLDR` as `Brief` and `full brief` as `Standard`. Treat the exact phrases
  `meeting brief` and `shared understanding` as `Standard`, even though
  `meeting brief` contains the word `brief`.
- For `explain the core components` or `explain the solution shape`, focus on
  responsibilities, ownership, boundaries, and their relationships. Add data or
  control movement only when it is needed to answer the focused question.
- A focused question selects scope, not depth. Answer only that section at the
  requested depth instead of reproducing the whole explanation. If the user
  does not specify a depth, infer it from the focused request and context.

## Quality check

Before responding, confirm:

- The explanation distinguishes current behavior from proposed behavior.
- The goal describes an outcome, not a code change.
- Any included solution shape exposes meaningful responsibilities and
  relationships without padding a simple plan with artificial components.
- Important terms are not silently given one global meaning when the plan
  assigns different meanings across contexts; local rule ownership and material
  boundary translations are explicit.
- When included, `How the solution works` traces interaction rather than
  repeating the before-and-after behavior.
- Any included design rationale and trade-offs come from the plan rather than
  agent judgment.
- Decision rules appear only when the plan contains conditional behavior and
  preserve its precedence, guards, fallbacks, and uncertainty behavior.
- Non-goals prevent the solution from sounding broader than the plan.
- Acceptance criteria are observable and include important negative cases.
- Unverified plan claims are not presented as independently confirmed facts.
- Every depth includes one simple example that explains the problem.
- Any visual improves understanding and stays faithful to the plan.
- A reader can explain the solution back without reading the full plan.
