---
name: thinking-land-to-earth
description: Turn a blurry, abstract, tangled, or half-formed idea into a concrete, user-confirmed Grounded Idea Card through a bounded one-question-at-a-time interview. Use only when the user explicitly invokes `$thinking-land-to-earth` to make an idea concrete before solution design, planning, writing, or execution.
---

# Thinking: Land to Earth

Move an idea down the ladder of abstraction until its result, context, success, boundaries, and representative scenario are concrete. Preserve the user's intent and uncertainty; do not silently turn clarification into solution design or planning.

## Keep the boundary

Work across software, business, writing, family, personal, and other domains. Interpret a concrete result as whatever fits the domain: a file or component, a decision-ready concept, an observable behavior, a process, an experience, or another specific outcome.

Remain read-only. Do not edit files, make external changes, choose priorities for the user, solve the idea, compare strategies, create an implementation plan, or execute the grounded idea.

If the input is already concrete and requests a different kind of work, state that it is already grounded, name the appropriate next workflow, and stop. Keep adjacent work separate:

- Use `thinking-decompose-problem` for factors and causal hypotheses.
- Use `thinking-explain` for understanding an existing topic or artifact.
- Use `thinking-expand-problem-space` for new perspectives or unconventional angles.
- Use `thinking-evaluate-circ` to evaluate an identifiable instruction.
- Use `thinking-propose-solutions` to compare solution directions after a causal model exists.
- Use `plan-creation` for an execution plan.

Never invoke another skill automatically.

## Ground the idea

Progress through three stages, but choose each question adaptively rather than following a rigid checklist:

1. **De-abstraction:** Replace vague categories, qualities, and intentions with specific language and an observable result.
2. **Scope and boundary:** Establish who or what the idea concerns, where it applies, and what it deliberately excludes.
3. **Concrete scenario:** Demonstrate the idea through one representative starting situation, interaction or action, and result.

Maintain a tentative Grounded Idea Card internally from the initial prompt onward. Fill what the user already supplied, mark inferences as assumptions, and identify only gaps that could materially change the card.

Resolve directly relevant, accessible facts through bounded read-only discovery instead of asking the user. Do not broaden clarification into general research. Facts may be verified; meaning, preferences, priorities, boundaries, and tradeoffs belong to the user.

## Apply the landing tests

Reevaluate these tests after the initial prompt and every answer:

1. **Concrete result:** What will exist, happen, or change is specific.
2. **Person and context:** Who or what is affected and where the idea applies are known.
3. **Observable success:** The result can be recognized without relying on vague adjectives.
4. **Boundaries:** Material non-goals or exclusions are explicit.
5. **Representative scenario:** One example shows the starting situation, action or experience, and concrete result.

Mark a test `Not applicable` only when it genuinely does not matter to the idea. Do not invent detail merely to make a test pass.

If every applicable test passes and no material ambiguity is being treated as settled, output the Grounded Idea Card immediately. Ask zero questions when the initial prompt is already sufficient. Do not use a numeric confidence score; structural coverage and unresolved material ambiguity determine readiness.

## Ask bounded questions

Ask exactly one high-leverage question at a time. Ask only when plausible answers would materially change the card. Skip information that is already clear, safely verifiable, non-material, or covered by a reasonable accepted default.

Use this format for questions one through three:

```markdown
[Grounding question <n> · default limit 3]

Question: <one concrete clarification>

Recommended answer: <a context-supported answer>

Why: <one short reason>
```

When evidence is thin, use `Suggested default` instead of `Recommended answer`. Always provide one of them. Keep the reason brief. Accept `yes`, `no`, `not sure`, or a correction.

When the user answers `not sure`, ask once whether to adopt the proposed answer provisionally. Reuse the same progress header with `· provisional confirmation` appended; do not count it as a new grounding question. If accepted, continue and label it as an assumption. If rejected or still uncertain, preserve the gap rather than repeating the confirmation or forcing false precision.

Do not ask compound questions. Do not use one question for every possible dimension. Resolve the single gap with the highest effect on the card, then reevaluate all landing tests before asking another.

## Enforce the session budget

Ask no more than three grounding questions by default.

After the answer to question three:

- Output a Grounded Idea Card immediately if the landing tests pass.
- Otherwise output a Draft Grounded Idea Card, name the remaining material gap, and ask whether the user wants to continue for up to two optional questions. Prefix this checkpoint with `[Checkpoint · 3-question default reached]` and include a recommended choice and short reason.

At this checkpoint, ask only whether to continue. Defer final card confirmation
until the interview ends. If the user declines further questions, present the
draft for confirmation without trying to resolve its remaining gaps.

Ask question four or five only after explicit approval. Prefix each with:

```text
[Optional grounding question <n> · hard limit 5]
```

After question five, end the grounding interview and output either the grounded
or draft card. Then request final card confirmation. Confirmation does not
authorize additional grounding questions. Never begin another interview cycle
automatically.

## Produce the card

Use the grounded title only when every applicable landing test passes:

```markdown
# Grounded Idea Card

## Core idea
<one concrete sentence without unexplained vague language>

## Concrete result or deliverable
<what will exist, happen, or change>

## Who and context
<who or what is affected and where it applies>

## Observable success
<how someone can recognize that it worked>

## Boundaries and non-goals
- <explicit exclusion>

## Representative scenario
<starting situation> → <interaction or action> → <concrete result>

## Assumptions and open questions
- <non-blocking uncertainty; omit this section when empty>
```

When a material gap remains, title it `# Draft Grounded Idea Card`, preserve the gap under `Assumptions and open questions`, and never present it as settled.

When the interview ends, ask whether the card accurately captures the user's
idea. Defer this confirmation at the three-question continuation checkpoint as
described above. Prefix the confirmation with `[Grounded · interview complete]`
or `[Draft · interview paused]`. Incorporate supplied corrections and regenerate
the card. If a correction creates a new material gap, keep it in a draft card;
confirmation alone does not authorize more grounding questions. Resume only
when the user requests it and within the unused approved question budget.

## Guardrails

- Keep the tone direct, calm, and conversational. Challenge vague language without becoming confrontational.
- Preserve the user's meaningful wording while replacing jargon and abstraction with concrete definitions.
- Never confuse polished phrasing with shared understanding.
- Never recommend a solution when the question concerns the user's intended meaning.
- Stop grounding questions once the landing tests pass. Ask only for final card confirmation.
- Never exceed the approved grounding-question budget. Budget-extension requests
  and card confirmation do not count as grounding questions and must not
  introduce new interview questions.
