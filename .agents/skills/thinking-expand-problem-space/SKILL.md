---
name: thinking-expand-problem-space
description: "Explore a problem, situation, goal, or opportunity with one technique at a time: Socratic questions, an unexpected perspective, a cross-domain analogy, or inversion. Use when the user asks for questions instead of answers, lateral or overlooked angles, an unusual role, analogies from unrelated fields, reverse brainstorming, or a fresh frame before solving. Recommend one technique when needed and keep the output exploratory; do not solve, decide, plan, verify causes, or evaluate bias."
---

# Expand Problem Space

Open a new angle before solving. Use one technique per pass.

## Start

- Require a clear problem, situation, goal, or opportunity. If missing, ask for
  it and stop.
- Treat supplied claims as context, not verified facts.
- If the user names a technique, use only that one.
- If the user says to choose and proceed, pick one, give a one-sentence reason,
  and run it.
- Otherwise, when one technique clearly fits, recommend it, explain why in one
  sentence, ask for confirmation, and stop.
- If no technique clearly fits, ask one question that will help choose and stop.

| Technique | Related requests | Use when |
| --- | --- | --- |
| **Socratic questions** | Flipped interaction, questions instead of answers | Assumptions, reasoning, or important unknowns need reflection. |
| **Unexpected perspective** | Persona or role prompting | A distant viewpoint may break conventional thinking. |
| **Cross-domain analogy** | Synectics, analogical transfer | Another field may have a useful relational pattern. |
| **Inversion** | Reverse brainstorming, anti-problem | The desired outcome is known and failure conditions may reveal blind spots. |

A pre-mortem—examining an existing plan as though it failed—is outside this
skill. If requested, explain that boundary instead of silently substituting
inversion.

## Run the Technique

### Socratic Questions

- Ask one question at a time and wait for feedback.
- Adapt the next question to the response. Do not reveal future questions.
- Explore relevant assumptions, definitions, evidence, unknowns, stakeholders,
  constraints, alternatives, implications, and counterexamples.
- Do not lead toward a preferred answer or hide advice inside a question.
- Ask no more than ten questions unless the user chooses another limit.
- Stop early when the user asks, the goal is reached, or another question adds
  no material insight.
- During the sequence, reply with only the next question and essential context.
  Summarize when the sequence ends or the user asks.

### Unexpected Perspective

- Use one role, archetype, stakeholder, or non-human viewpoint.
- Pick a lens distant from the usual domain but relevant to the problem.
- Say what the lens notices, reframe the target, and surface its questions or
  observations.
- Treat the lens as a heuristic, not verified expertise. Label invented details
  and do not assign unsupported beliefs to a real person.

### Cross-Domain Analogy

- Use one source domain.
- Identify the target structure, map a similar mechanism from the source, and
  surface candidate inferences or questions.
- Say where the analogy may break.
- Transfer relationships, not surface resemblance. Treat the mapping as a
  hypothesis, not proof that it will work.

### Inversion

- Use one inverted framing.
- State the desired outcome, ask what would guarantee failure or make it worse,
  and generate possible failure conditions.
- Compare them only with supplied context. Do not claim they are happening
  without evidence.
- Do not turn the result into a solution, mitigation list, pre-mortem, or plan.

## Boundaries and Finish

- Do not mix techniques or invoke another `thinking-*` skill automatically.
- Do not create a full factor map, causal model, or ranking.
- Do not verify causes, evaluate bias, fact-check, research externally, solve,
  decide, plan, or act unless the user separately requests that work.
- Label perspectives, analogies, inversions, and inferred links as exploratory.
- Summarize only what the selected technique revealed and what remains unclear.
- Suggest at most one next technique only when it adds materially different
  insight. Explain its value in one sentence, ask whether to continue, and stop.
  Never run it automatically.
- If no next technique is useful, stop without suggesting one.

Before presenting exploration results, confirm that exactly one technique was used and the response
did not drift into a solution, decision, plan, verification, or automatic
handoff.
