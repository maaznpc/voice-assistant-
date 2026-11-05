---
name: thinking-explain
description: Help the user understand a difficult topic, concept, artifact, plan, proposal, system, code behavior, or situation through a concise plain-language explanation. Use when the user's primary intent is understanding, such as "explain this," "help me understand," "break this down," "what does this mean," a TLDR, a walkthrough, or a shared-understanding brief. Adapt the depth and structure to the topic. Do not use as a substitute for requested review, validation, fact-checking, diagnosis, decision-making, or implementation.
---

# Thinking Explain

Help the user understand the current topic without needing to absorb all of the
source material. Skip the preamble and explain the central idea, important
relationships, and practical meaning in brief prose. When a visual materially
improves understanding, use the smallest useful diagram, table, flow, or
code-shape sketch. Use a focused HTML artifact only when the software
visualization route applies.

## Boundaries

- Treat supplied material as the source for the explanation, not automatically
  as verified truth.
- Distinguish established facts, source claims, assumptions, and uncertainty
  when the distinction affects understanding.
- Inspect only the material needed to explain the topic. Do not expand an
  explanation into a correctness review, investigation, diagnosis, decision, or
  implementation unless the user separately asks for that work.
- Preserve the source's meaning and scope. Do not resolve ambiguity by inventing
  missing behavior or intent.
- Label an invented example `Illustrative`; keep it neutral and do not use it as
  evidence that the source is correct.
- Do not make external or operational state changes merely to explain
  something. Create a local user-facing visual artifact only when
  [Software Visualization](references/software-visualization.md) selects its
  focused HTML path.

## Method

1. Identify what the user is trying to understand and select the appropriate
   depth from the request and context. Use the general depth controls for the
   general route and the plan branch's depth controls for plan explanations.
2. Lead with the central idea and why it matters before introducing details.
3. Break the topic into the fewest components needed to explain how the pieces
   relate, interact, or differ.
4. Make important sequence, cause-and-effect, precedence, and boundaries
   explicit instead of leaving the reader to infer them.
5. When an important term, identity, or rule changes meaning across domains,
   roles, teams, or systems, name the relevant contexts, define the local
   meaning in each one, keep their rules separate, and show any material
   translation at the boundary. Do not invent contexts unsupported by the
   source.
6. When it materially improves understanding, use the simplest useful example.
   Prefer one actor or input, one important decision or mechanism, and one
   visible result.
7. Use a before-and-after comparison, analogy, counterexample, or negative case
   only when it removes a likely misunderstanding.
8. Use the smallest visual that materially improves understanding. Prefer a
   text flow or compact table; use Mermaid for multiple steps, branches, or
   relationships that are difficult to explain linearly.
9. Define necessary jargon in plain language and remove details that do not
   change the reader's mental model.
10. Preserve meaningful risks, assumptions, unknowns, and conflicting claims.
11. Stop when the user has the requested understanding; do not turn the answer
    into an exhaustive report by default.

## Route by source and request

Evaluate this table from top to bottom. Apply every matching `Modifier`, then
stop at the first matching `Route`. A modifier shapes the eventual route but
does not select the source workflow by itself.

Treat a source as a plan only when the user explicitly identifies it as one, or
when it is clearly an implementation, migration, execution, or rollout plan
that proposes a future change. Ordered steps, recommendations, or remediation
actions alone do not make an artifact a plan.

| Order | Type | Match | Load or workflow | Required behavior |
| --- | --- | --- | --- | --- |
| 1 | Modifier | The user requests a specific format or depth | None | Honor it instead of the default output; continue routing so the selected route applies its depth contract. |
| 2 | Modifier | The topic concerns software behavior, code structure, runtime flow, UI components, file ownership, architecture, or a proposed code change, and either the user requests a visual or a visual materially improves understanding | Read [Software Visualization](references/software-visualization.md) completely | Select the smallest software-specific visual, keep it explanatory, and continue routing by source. |
| 3 | Modifier | Explanation is combined with review, validation, fact-checking, diagnosis, decision-making, or implementation | Use the applicable separate workflow for that additional intent | Keep explanation distinct from the additional work; continue routing by source. |
| 4 | Route | A focused question about one part of an identified plan | Read [Plan Explanation Branch](references/plan-explanation.md) completely | Follow its focused-question rule; answer directly without the full plan template. |
| 5 | Route | A broad plan request such as `explain this plan`, `plan TLDR`, `meeting brief`, or `shared understanding` | Read [Plan Explanation Branch](references/plan-explanation.md) completely | Apply its plan-specific method and output contract. |
| 6 | Route | Any other topic, including concepts, documents, systems, code behavior, proposals, incidents, decisions, mixed artifacts without a plan focus, or artifacts that merely contain steps | No additional reference | Use the general method and topic-appropriate structure; do not borrow plan-only headings. |

Add new modifiers before the route rows. Add new source routes from most
specific to most general, always above the final `Any other topic` fallback.

## Default output

Lead with a short direct explanation. For the general `Any other topic` route,
use these depth controls:

- `Brief`: Explain the central idea, why it matters, and the key takeaway. Use
  one to three short paragraphs. Add an example only when it is necessary for
  understanding.
- `Standard`: Explain the central idea, essential relationships or mechanics,
  one simple example when useful, and the practical meaning. This is the
  default for an ordinary explanation request.
- `Detailed`: Explain the important components, sequence, relationships,
  boundaries, examples, and relevant uncertainty. Detailed does not authorize
  an exhaustive report, correctness review, investigation, or diagnosis.

When the user does not specify a depth, use `Brief` for a narrow or simple
question, `Standard` for an ordinary explanation, and `Detailed` only when the
topic's complexity materially requires it. Do not pad an explanation to fit a
depth. A requested word count or format overrides these controls. Do not show
the depth label unless it helps the response.

For plan explanations, use the depth controls in
`references/plan-explanation.md` instead.

Add only the sections that materially help, such as:

- `How it works` for components, sequence, or relationships;
- `Simple example` for a concrete mental model;
- `What matters` for the practical consequence or key takeaway; and
- `Uncertainty` for claims, assumptions, or missing context that could change
  the explanation.

Do not force these headings. For a focused question, answer directly. For a
broad or complex topic, use a structured explanation. The user's requested
format and depth override these defaults.

## Quality check

Before responding, confirm:

- The central idea appears before supporting detail.
- The explanation answers the user's actual point of confusion.
- Important relationships, sequence, and boundaries are explicit.
- Important terms are not silently given one global meaning when the source
  assigns different meanings across contexts; material boundary translations
  are explicit.
- Jargon is defined or replaced without losing necessary precision.
- Examples and visuals are faithful to the source and materially useful.
- Unverified claims are not presented as independently confirmed facts.
- Explanation is not being used as a disguised review, diagnosis, or decision.
- The reader can explain the topic back at the requested level.
