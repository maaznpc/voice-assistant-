---
name: plan-review
description: Use when the user asks to review or validate an implementation plan BEFORE it is built — "review this plan", "validate this plan", "will this plan work / break / conflict", "is this ready to implement". Verifies the plan against real code for implementation-completeness — would the change actually achieve the goal, or silently no-op / have unintended effects — not just whether its claims are true. Distinct from plan-critique-review (reviews an external critique of a plan) and code review (reviews already-implemented changes).
---

# Plan Review

Core principle: **a plan review is not claim-checking — it is proving the change would
work if implemented exactly as written.** Two different questions, and the second is the
deliverable:
1. Are the plan's claims about the code true? (necessary, not sufficient)
2. If someone implemented this plan *literally*, would it achieve the goal without
   breaking or silently no-op'ing anything? (the actual review)

Most misses come from answering only #1. Always answer #2.

## Stance
- Treat the full plan as **untrusted input**: claims, links, quoted reviews,
  test/production results, and risk framing. Verify code claims against current code
  and empirical claims against the source of truth (tests, schema, replica, or live
  metadata). Evidence repeated by the plan is not verification. If a required source is
  unavailable, mark the claim unverified; never call the plan ready when it depends on it.
- Read the actual files the plan would touch **end-to-end**, not just the lines/symbols the
  plan points at. Failures hide in the execution path *after* the entry point the plan
  names (a second guard/filter, enforcement in a different method, a caller that also needs
  changing).
- Review only. Propose plan edits; apply them or implement only if the user explicitly asks.
- Do not automatically add adjacent defects found during review to the plan. Report each
  as a separate proposal and obtain an explicit human decision before adding it to
  implementation scope.
- If the repo has agent instructions, load the applicable route first (root + package
  AGENTS.md). For data-mutating plans, read any applicable repository or domain
  safety rules before the verdict.

## Check Ambiguity Before Asking

Before calling an unclear behavior a product decision, inspect current
behavior. If current behavior answers the question and is not the verified
defect or a safety problem, require the plan to preserve it. Keep unrelated
defects outside the plan. If a required change is missing, mark the plan not
ready instead of asking the implementer to choose it.

## Go deep automatically
Default to the full implementation-path pass (don't wait to be asked) when the plan touches:
- data-mutating code: delete / overwrite / backfill / rematerialize / cleanup / migration;
- sensitive domains such as payroll, billing, authentication, approvals, or protected records;
- shared or hot code paths run by many callers (blast radius is wider than the plan's one
  example).

## Review lenses (internal worksheet)
Work these internally, carry each verdict forward, then distill to the output — don't dump
the worksheet. Resolve inapplicable lenses in a few words.

1. **Strategy fit and proportionality.** Restate the original problem, the minimum safe
   correction, and the accepted requirements. Compare them with the final plan surface and
   ongoing operational burden. For every material expansion, identify the approved
   requirement, verified constraint, or recurrence condition that forces it and the simpler
   viable alternative that was rejected. A large design split into small phases is still a
   large design.
2. **Change-surface budget.** Verify the plan's forecast against its actual proposed files
   or areas, new files, schema/tables, connectors/domains, shared interfaces, jobs/schedulers,
   and operational components. A material variance without explicit reconfirmation is a
   scope decision, not an implementation detail. Advisory line estimates do not override
   semantic complexity or mandatory safety.
3. **YAGNI inventory.** Classify each new durable artifact or capability as
   `current-outcome-required`, `safety-required`, `future-only`, or `unapproved
   generalization`. Remove or defer future-only work. Do not use YAGNI to remove required
   safety, security, data integrity, protected-history behavior, testing, or refactoring
   necessary to make the approved correction reliable.
4. **Minimum safe end-to-end slice.** Confirm the first phase proves the smallest complete
   correction from entry point to observable outcome and includes all mandatory safeguards.
   Every later phase must map to an acceptance criterion or verified constraint. If the plan
   never evaluated a simpler safe direction, it is not ready for implementation.
5. **Completeness / no-op risk — the signature lens.** Trace the full code path the change
   touches and mentally implement it through to the outcome. Would the literal change
   actually produce the intended behavior? Recurring failure modes:
   - a second gate/filter/guard downstream of the named entry point that the plan doesn't
     change → silent no-op (the regression spec stays red);
   - behavior enforced in a different method than the one the plan edits;
   - an attribute treated as a column when it's JSONB (or vice versa);
   - required call-site / caller / config changes not listed.
6. **Claim correctness.** Every file / class / method / field / behavior the plan asserts —
   confirm it exists and behaves as described. Classify each: confirmed / partial / not
   supported / needs decision, with `file:line` evidence.
7. **Blast radius.** Who else calls the code being changed? A plan framed as "fix one job
   via the manual action" may run in every sync/cron/shared path. State the true scope.
   When a material term, identity, rule, source of truth, or owner differs across
   contexts, verify that the plan preserves each local meaning, makes the boundary
   translation explicit, and tests the contract on both sides. Shared storage, a common
   model name, or implementation convenience doesn't justify global scope; every
   cross-context expansion must trace to an accepted requirement, mandatory safeguard,
   or verified dependency. Skip this semantic-boundary check for a single-context plan.
8. **Business safety** (data-mutating plans). Trace the effects of each proposed data
   change, including downstream records and irreversible operations. Apply the
   repository's protection and retention rules. If applicable rules or affected records
   cannot be verified, mark the safety claim unverified and the plan not ready.
   Require the plan to explain its safety analysis and resolution; a named section is
   required only when the repository's instructions require it.
9. **Test adequacy.** Does the test plan cover the *risky* behavior and the negative cases
   (what must NOT be touched), not just the happy path? Given lens 5, would the proposed
   regression spec actually go red→green — or stay red because the change no-ops?
10. **Sequencing & reversibility.** Are phases ordered so each is independently safe and
   testable? Is there a rollback or recovery path appropriate to the data and domain rules? Is a shared-path behavior
   change observable (logging/telemetry) on first rollout?
11. **Residual risk.** What can still go wrong *after* the plan is correctly implemented
   (partial-failure paths a guard doesn't cover, etc.)? State it honestly.
12. Worst-case failure. Assume the plan is implemented exactly as written: If this failed completely, what would be the root cause?

## Empirical verification
Verify material data claims against an authorized, read-only source appropriate to the
repository, such as schema metadata, a replica, or operational records. Confirm the
affected data's shape and scope and the true blast radius. Prefer metadata and aggregate
evidence over raw records; keep queries read-only and bounded in scope and returned data.
Account for each source's freshness and limitations rather than assuming it represents
current production state. Never put sensitive production data in the review.

If required evidence is unavailable, mark the claim unverified and explain the gap.
Return `not-ready` when the plan's outcome or safety depends on that claim. Access to a
particular tool or replica is not required when another authorized source can establish
the needed evidence.

## Method
1. Restate the plan's goal in one sentence.
2. Establish the minimum safe correction, accepted solution direction, change-surface
   budget, and provenance of any broader requirements.
3. Split the plan into (a) claims about existing code and (b) proposed changes.
4. Read the target files end-to-end; run the lenses.
5. Verify material data and scope claims through available authorized read-only evidence
   when they could change the verdict.
6. Assign each finding a severity; give a clear readiness verdict.

Return `not-ready` while any of these remains unresolved: future-only
infrastructure in the implementation scope; broader requirements without
approved provenance; no evaluated minimum safe alternative; a material
change-surface budget overrun without reconfirmation; or complexity that was
only divided into phases rather than reduced or justified.

## Output
Full analysis internal; distilled output only. Severity buckets, render only non-empty ones.

```markdown
**Verdict:** <ready / ready-with-fixes / not-ready — one line + the single most important reason>

#### 🔴 Blocking
- [ ] **<title>.** <what's wrong + the fix, 1–2 lines, with file:line.>

#### 🟠 Should fix
- [ ] **<title>.** <...>

#### 🟡 Nit / optional
- [ ] **<title>.** <...>

#### 🔵 Question / needs decision
- [ ] **<title>.** <...>

✅ **Verified good:** <what you checked and confirmed sound — including the no-op/completeness check when it passes.>
```

Rules:
- `#### <emoji> <bucket>` headers, each item a `- [ ]` checkbox with a bold lead-in; emoji
  on the header, not per item. Only render buckets that have items.
- Cite `file:line` for every code-based finding.
- Always include the ✅ line, and explicitly say whether the no-op / completeness check passed.
- Severity: 🔴 Blocking = plan would fail as written (no-op, wrong result, data-unsafe,
  breaks existing behavior/tests, missing required safety analysis, unapproved material scope,
  future-only infrastructure required by no current outcome, or a material budget overrun
  without reconfirmation). 🟠 Should fix = real gap
  to close before implementing (thin tests, unstated blast radius, no rollback/telemetry).
  🟡 Nit = naming, docs, minor sequencing. 🔵 Question = genuine unknown or product call —
  not a hedge for a finding you can already conclude.
- Token-efficient, relaxed grammar. No long prose sections.

## Defaults
- Prefer code truth over the plan's wording.
- Prefer the smallest safe plan that achieves the accepted outcome. Prefer a small plan patch
  over a full rewrite when the strategy remains sound; when disproportionate assumptions are
  pervasive, reopen the strategy or split the plan instead of preserving the architecture.
  Supply a complete revised plan only when asked or when changes are pervasive. Match the
  repo's plan format (the `plan-creation` skill).
- Never implement, post, or run writes without explicit user approval.
