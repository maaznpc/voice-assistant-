# Complementary Skills

These sit beside the mandatory workflow. Suggest only when the trigger is material and the skill is available. `thinking-show-me` is a bundled optional complement. Skip unavailable complements; do not install them automatically. Never run one automatically.

| Skill | Suggest when | Best moment | Guard |
| --- | --- | --- | --- |
| `thinking-evaluate-circ` | The request or instruction has material Context, Intention, Restrictions, or Success Criteria ambiguity | Intake, before stage 1 | Instruction clarity only; not truth, feasibility, or execution |
| `thinking-expand-problem-space` | A fresh question, perspective, analogy, or inversion could change the framing | Before decomposition or solution proposals | One technique; exploratory only |
| `thinking-evaluate-bias` | A causal report, option set, solution, or plan may favor one conclusion through framing, unequal scrutiny, or omission | Before the next human decision | Not a general accuracy or completeness audit |
| `thinking-explain` | A dense stage artifact may block shared understanding | At a decision pause | Explanation only; no disguised review, diagnosis, or decision |
| `plan-critique-review` | Concrete plan feedback must be checked before changing the plan | After plan creation or review feedback | Treat feedback as claims; implementation stays separate |
| `thinking-show-me` | A visual could materially improve understanding of the current stage | At a stage boundary or decision pause | Mention as an explicit option only; run only when the user invokes `thinking-show-me` or `$thinking-show-me` |

## Suggestion

Use one short prompt:

```text
Optional: `<skill>` could help here because <reason>. It fits before <next stage> and the main workflow resumes there. Want to use it?
```

If accepted, satisfy the complement's own entry gates, finish it, record any relevant artifact, then resume the same mandatory stage. Its output counts only when that stage's normal evidence gate accepts it.

- For `thinking-show-me`, a generic `yes` is not invocation. Wait for `thinking-show-me` or `$thinking-show-me`.
