---
name: thinking-decompose-problem
description: Break down a large, ambiguous, or confusing problem using limited read-only discovery, distinct factors, meaningful groups, visible relationships, bounded causal hypotheses, and up to three priorities. Use at the start of a problem before causal verification, decisions, planning, or action. This skill may explore possible causes through iterative why questions and End User, Process, and Organization perspectives, but it does not verify causes, solve, plan, decide, or modify anything.
---

# Decompose Problem

Decompose the problem using these four operations:

## 1. List factors (factors that could be involved)

List the factors that could be involved. Do not judge or prioritize them yet. Consider anything relevant, For example:

- People
- Processes
- Resources
- Time
- Decisions
- Information
- Constraints
- Incentives
- Dependencies
- External conditions
- Etc

Use these only as prompts. Include the factors that fit the actual problem.
Keep discovery separate from evaluation so early assumptions do not exclude
potentially important factors before the problem structure is visible. Do not
repeat the raw list in the final response.

## 2. Categories (categories of related factors)

Organize related factors into meaningful categories.

- Merge factors that express the same idea.
- Keep categories distinct.
- Give each category a useful, descriptive name.
- Avoid generic categories such as "Other," "Miscellaneous," or "Various."
- Include every distinct factor once in the final Factor Map.

Categorization turns the broad factor set into a complete, non-overlapping map.
This makes relationships easier to see and prevents factors from being omitted
or counted more than once during prioritization.

## 3. Connect (relationships between factors and categories)

Show how the factors and groups may relate to each other. Look for:

- Factors that may cause or influence others
- Preconditions and dependencies
- Feedback loops
- Shared causes
- Factors that affect several parts of the problem
- Relationships that could reveal a root factor or leverage point

Treat unverified relationships as possibilities/hypotheses, not established facts.

## 4. Prioritize

Choose no more than three factors that deserve the most attention. Prioritize based on their potential impact on the overall problem, not merely because they are easy to address. For each priority, explain:

- Why it matters
- Which parts of the problem it affects
- What could change if it were understood or resolved

Priorities are provisional. If later evidence shows that a priority is not
helpful, contradicts it, or reveals a more influential factor, revisit the
decomposition and select a replacement. Explain why the previous factor was
deprioritized and why the new one deserves attention.

# After the 4 operations

## Quality Check

Before responding, confirm that:

- The parts are distinct and do not repeat the same idea.
- Together, they cover the problem as currently understood.
- Important gaps or missing information are visible.
- Relationships between the parts are explicit.
- Priorities follow from the decomposition rather than convenience.
- Possible connections are not presented as proven causes.
- Causal chains stay within the required limits and preserve evidence gaps.
- All three perspectives were checked, and supported additions were incorporated before finalizing priorities.

## Deepen Symptoms and Causes

Separate the problem into three levels:

1. **Symptom:** What observable behavior indicates something is wrong?
2. **Possible direct cause:** What might be directly producing that behavior?
3. **Possible underlying cause:** What might allow the problem to exist or keep recurring?

Separate these levels so observed behavior is not confused with a causal
hypothesis, and so the mechanism producing the symptom remains distinct from
the condition allowing it to recur.

For prioritized causal hypotheses, ask "Why might this happen?" within these limits:

- Explore no more than three causal chains.
- Ask no more than five Why questions per chain.
- Stop earlier when the next answer lacks evidence, leaves the relevant scope, or reaches a condition that plausibly explains recurrence.
- Branch when multiple causes are plausible, but keep all branches within the three-chain limit.
- Assign an evidence status to each level in a chain, not to the chain as a whole: observed, supported inference, hypothesis, or unknown.
- Label unverified answers as hypotheses rather than root causes.

These limits keep the decomposition focused and prevent increasingly speculative
explanations from overwhelming the useful causal possibilities. Labeling each
level separately prevents evidence supporting one part of a chain from making
the entire chain appear proven.

Check the causal picture through all three perspectives:

- **End User:** How is the problem experienced, and what need is unmet?
- **Process:** Where does the workflow break, stall, repeat, or depend on manual work?
- **Organization:** What structure, incentive, decision, ownership gap, or missing resource allows the problem to continue?

For each perspective, identify what it reveals and whether it contributes a new
factor, relationship, evidence gap, or no addition. Do not invent a contribution
when a perspective reveals nothing useful, and do not treat perspective findings
as proven causes.

After checking all three perspectives, incorporate any supported additions into
the Factor Map, connections, causal chains, and gaps. This keeps the decomposition
internally consistent and ensures the priorities reflect the fullest current
understanding of the problem. Reconsider the priorities when a newly identified
factor or relationship may be more influential than those previously selected.
If a perspective reveals nothing new, do not change the decomposition merely to
satisfy the template.

Then summarize the problem using the observed symptom, scope, and impact without
embedding an assumed cause or solution. Keeping the problem statement neutral
prevents an unverified causal hypothesis or preferred solution from becoming
part of the problem definition, leaving later investigation open to competing
explanations.

## Output Template

Use this structure:

```markdown
# Problem

[statement of the problem]

1. **Symptom:** What observable behavior indicates something is wrong?
2. **Possible direct cause:** What might be directly producing that behavior?
3. **Possible underlying cause:** What might allow the problem to exist or keep recurring?


# Factor Map

## [Meaningful category]

- **[Factor]:** [Why it may be involved]
- **[Factor]:** [Why it may be involved]

## [Meaningful category]

- **[Factor]:** [Why it may be involved]

# Connections between factors and categories

- [Factor] may influence [factor] because...
- [Factor] may affect several groups by...
- A possible shared cause is...

## Causal Chains - maximum three chains, maximum five Why levels per chain

### 1. [Short chain name]

- **Symptom:** [Observable behavior]
  - Evidence status: observed / supported inference / hypothesis / unknown
- **Why 1 - Possible direct cause:** [What might directly produce the symptom?]
  - Evidence status: observed / supported inference / hypothesis / unknown
- **Why 2 - Possible underlying condition:** [What might allow the direct cause to exist or recur?]
  - Evidence status: observed / supported inference / hypothesis / unknown
- **Why 3-5, only when supported and useful:** [Repeat one Why level at a time without exceeding five levels.]
  - Evidence status: observed / supported inference / hypothesis / unknown
- **Stop condition:** [State why the chain stops here: recurrence is plausibly explained, further evidence is missing, or the next Why would leave scope.]

## Perspectives

- **End User**
  - What this lens reveals:
  - Contribution: new factor / relationship / evidence gap / no addition
  - Evidence status: observed / supported inference / hypothesis / unknown
- **Process**
  - What this lens reveals:
  - Contribution: new factor / relationship / evidence gap / no addition
  - Evidence status: observed / supported inference / hypothesis / unknown
- **Organization**
  - What this lens reveals:
  - Contribution: new factor / relationship / evidence gap / no addition
  - Evidence status: observed / supported inference / hypothesis / unknown

# Top Priorities that deserve the most attention.

1. **[Factor]**
   - Why it matters:
   - What it affects:
   - Potential impact:

2. **[Factor]**
   - Why it matters:
   - What it affects:
   - Potential impact:

3. **[Factor]**
   - Why it matters:
   - What it affects:
   - Potential impact:

# Gaps and Assumptions

- ...
```

Stop after presenting the decomposition.
