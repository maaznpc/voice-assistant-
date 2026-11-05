# Omission Bias

Use this lens to find a missing fact, consequence, perspective, or decision dimension that makes the work lean toward a conclusion or gives an incomplete picture of reality.

## Keep It Separate

Omission is about **directional absence beyond the explicit request**. Request completeness asks whether the work included what the user directly requested. Omission asks whether something important was left out even though the literal request was answered.

Keep omission separate from representation. Representation asks who or what makes up the sample and whether it matches the target. Omission asks whether a whole category of evidence, cost, risk, consequence, or perspective is missing.

## Counterfactual Omission Check

Before concluding that no material omission exists, assume the stated evidence
is true and imagine the conclusion or intended outcome fails completely.

- What plausible missing fact, condition, constraint, consequence, or perspective
  could explain that failure?
- What material limitation or second-order effect has the work not questioned?

Treat the answers as omission candidates, not verified root causes. Consider
only candidates with a concrete reason to materially change or qualify the
conclusion.

## Starting Questions

Use these as starting points, not a complete checklist.  Adapt, skip, or add questions based on the work, evidence, and context to fit the work.

- What missing fact could reverse or seriously qualify the conclusion?
- Are the benefits shown without the costs, risks, tradeoffs, or opportunity costs?
- Are short-term results shown without long-term or second-order effects?
- Are implementation benefits included while maintenance and operating work are missing?
- Are benefits for the decision-maker included while effects on operators or other affected people are missing?
- Are averages shown without the range, variance, failures, or denominator needed to understand them?
- Are dependencies, reversibility, downstream effects, or protected history left out?
- Is a whole decision dimension missing that a reasonable person would need?

## When to Report It

Report a material finding only when:

1. the missing dimension matters to the decision;
2. there is a concrete reason it could change the picture; and
3. leaving it out pushes the work toward a conclusion instead of merely making it less detailed.

Name what is missing, why it matters, and what evidence would fill the gap. Do not dump a generic list of every possible risk.

## Examples

- Report 30% revenue growth but leave out 50% debt growth in an investment recommendation.
- Recommend automation for its speed but leave out review work and failure recovery.
- Present implementation speed without the ongoing maintenance cost.
- Judge a cleanup by current correctness but leave out paid, audited, or historical records it could affect.
