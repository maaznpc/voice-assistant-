---
name: thinking-investigate-root-causes
description: Conduct a read-only, evidence-based root-cause investigation from a decomposition and collected evidence. Use after `thinking-decompose-problem` when the user wants to verify causal hypotheses, understand why a problem occurs, or resolve causal uncertainty. Extract and compare material source evidence before causal interpretation, test competing explanations, revise hypotheses when warranted, and stop before fixes or state changes.
---

# Investigate Root Causes

Use the decomposition as a starting map, not a conclusion.

## Start

- Require the full decomposition or an equivalent structured analysis containing
  a neutral symptom, scope, impact, prioritized causal hypotheses or chains, and
  known evidence gaps. If it is missing, recommend
  `thinking-decompose-problem` and stop. Do not invoke it automatically.
- Restate the observed symptom, scope, and impact without assuming a cause or solution.

## Stay Read-Only

- Use inspections, pure reads, and read-only queries only.
- Do not modify code, configuration, files, or data. Do not run tests, migrations, syncs, jobs, backfills, repairs, deployments, installs, or commands that may write artifacts or caches.
- Report any state-changing validation as an unexecuted next step.
- Do not design fixes, make decisions, or create a plan.

## Prepare the Evidence

Before testing causal hypotheses, extract the relevant information from each
material source:

- Identify the source, relevant timeframe and scope, direct observations or
  data, reported claims, interpretations, and material limitations.
- Preserve the source's meaning. Keep observations and source claims separate
  from agent inference.
- Check the extraction against the original source when accessible. If it
  cannot be checked, label it unverified and do not use it as conclusive
  evidence.
- Compare where sources agree, whether that agreement is independent or comes
  from shared underlying evidence, where they contradict each other, and what
  relevant evidence none of them provides.
- Consider whether differences in timeframe, scope, method, or perspective may
  explain a contradiction, but do not present an explanation as established
  without evidence.

Keep extraction separate from causal interpretation. If an ambiguity or
unresolved contradiction prevents reliable hypothesis testing, report the
extracted evidence and the smallest clarification or read-only check needed,
then stop.

Using the prepared evidence, confirm the symptom, expected versus actual
behavior, timing, and a useful comparison. If the symptom is not confirmed,
distinguish contradictory evidence from failure to observe it, report what is
missing, and stop.

## Investigate

- Start with the prepared evidence and the decomposition's priorities and causal chains, but follow relevant new leads.
- For each plausible hypothesis, identify evidence that would support or weaken it.
- Prefer primary evidence. Look for confirming and disconfirming evidence, and test competing explanations with similar scrutiny.
- Check whether the proposed cause precedes or enables the symptom, has a plausible mechanism, explains the scope and exceptions, matches comparisons or counterexamples, and would plausibly affect recurrence if removed.
- Revise, merge, split, deprioritize, or reject hypotheses as evidence changes.

Do not treat correlation, timing alone, suggestive names, or lack of another
explanation as proof.

## Assess Findings

Use these labels:

- **Verified root cause:** The mechanism and observed scope are supported, credible alternatives were materially tested, and confidence is high.
- **Probable root cause:** Strongest supported explanation, but a material validation gap remains.
- **Contributing factor:** Supported, but does not explain the problem alone.
- **Rejected hypothesis:** Contradicted or immaterial.
- **Unresolved hypothesis:** Evidence is insufficient.

Separate the immediate mechanism from conditions that allow recurrence. Allow
multiple or branching causes when supported.

Label claims as observed, inferred, reported, or unknown. Cite exact evidence
where possible, include material counterevidence, separate current from
historical behavior, and state the inspected scope.

Stop when the material hypotheses are tested and either the causal model
explains the problem or no further safe, in-scope read can resolve the gaps. Do
not search merely to eliminate every theoretical possibility.

## Report

Keep the result concise and adapt the headings to the problem. Give each
material fact one owner in the report; refer to it rather than restating it in
later sections.

Include:

1. **Observed problem and scope:** State the neutral symptom, impact, timeframe,
   and inspected boundary.
2. **Evidence synthesis:** Present each material observation or source claim
   once. Separate observations from source interpretations, and show agreements,
   contradictions, shared origins, limitations, and gaps. Do not perform the
   investigation's causal interpretation or state its conclusion here.
3. **Findings:** Use this as the authoritative location for causal judgments.
   For each material finding, give its label, confidence, causal role, mechanism,
   decisive supporting evidence by reference to the synthesis, material
   counterevidence, and remaining uncertainty. Identify which hypotheses were
   retained, changed, added, rejected, or left unresolved. For an unresolved
   uncertainty, name the next safe read-only check if one exists; otherwise
   state that no in-scope read can resolve it and, when relevant, name any
   state-changing validation only as an unexecuted next step.
4. **Causal model, only when useful:** When multiple or branching causes are
   supported, show their relationship in a compact chain or model that refers
   to the findings without repeating their evidence.

The primary verified or probable root-cause finding is also the conclusion; do
not repeat it in a separate conclusion section. If no root cause was established,
state that once in the Findings section. Do not add a separate unknowns section
when the same uncertainties and checks already appear with their findings.

Stop after the investigation report. Continue to remediation or planning only
through a separate user request.
