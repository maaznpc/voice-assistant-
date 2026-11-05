---
name: plan-critique-review
description: Validate feedback on a proposed implementation plan against the actual codebase before changing the plan. Use to break feedback into claims, verify each claim with relevant evidence, classify its support, and revise only what the evidence justifies.
---

# Plan Critique Review

Use this when a plan comes back with review feedback and the user wants the critique checked before changing the plan.

## Rules

- Treat critique as claims, not facts.
- Inspect the actual code, tests, docs, schemas, or routes before agreeing.
- Inspect evidence read-only. When the request is assessment only, present
  proposed plan changes without editing files. Apply supported changes when
  the user or authorized parent workflow has requested plan revision.
- Do not implement the plan as part of this skill. Implementation requires a
  separate, explicitly authorized workflow.
- Keep the response token efficient and relaxed.
- If the task is in a repo with agent instructions, read the applicable route first.

## Workflow

1. Restate the review target in one sentence.
2. Break the critique into concrete claims.
3. For each claim, inspect the smallest relevant code path.
4. Classify each claim:
   - `confirmed`
   - `partially confirmed`
   - `not supported`
   - `needs evidence`
   - `needs product decision`
5. For `partially confirmed` feedback, handle each material portion separately:
   - Propose or apply revisions for portions supported by evidence, according
     to the authorized scope.
   - Reject portions contradicted by evidence and record why.
   - Do not guess about unresolved portions. Continue bounded investigation when
     possible; otherwise state what evidence is missing.
   - Classify an unresolved portion as `needs evidence` when available evidence
     cannot establish the answer.
   - Classify it as `needs product decision` only when evidence cannot choose
     between valid product or business options.
6. Cite the code evidence with file references.
7. Propose or apply plan changes, according to the authorized scope, only for
   confirmed claims, supported portions of partially confirmed feedback, or
   explicit product choices. Leave `not supported` claims out of the plan and
   record why. Do not propose or apply a correction for `needs evidence`
   claims until the missing evidence resolves them.
8. Call out anything the critique missed if it changes implementation safety.

## Output Shape

When this skill is used directly, use this order. When another workflow invokes
it, return the classifications and evidence in that workflow's required format.

1. `Assessment`
   - One bullet per claim.
   - Include classification and evidence.
2. `Plan Delta`
   - What changes from the original plan and why.
   - State whether the changes are proposed or were applied under authorization.
   - Say `No plan changes needed` if true.
3. `Revised Plan`
   - Include a complete replacement plan only when plan changes are needed.
   - For assessment-only requests, label it as a proposal; do not save it.
   - Use the repo/user's required plan format if one exists.
4. `Implementation Readiness`
   - State whether the reviewed critique leaves unresolved blockers.
   - Claim overall implementation readiness only when the full plan was also
     reviewed; otherwise state that overall readiness was not assessed.
   - List remaining product decisions and unresolved evidence gaps, if any.

## Defaults

- Prefer code truth over critique wording.
- Prefer a smaller plan patch over rewriting the whole plan.
- Prefer existing tests and local patterns over inventing new test harnesses.
- For frontend review, inspect both render gates and template branches.
- For backend review, inspect routes/controllers, model helpers, schema fields, and existing specs.
