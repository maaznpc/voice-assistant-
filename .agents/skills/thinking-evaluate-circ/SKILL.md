---
name: thinking-evaluate-circ
description: "Evaluate the anatomy of a prompt, task, plan, brief, request, or delegated instruction using CIRC: Context, Intention, Restrictions, and Criteria for Success. Use when the user asks for a CIRC evaluation, wants to check whether an instruction is clear and sufficiently complete, or explicitly wants it improved using CIRC. Identify material missing, ambiguous, irrelevant, or conflicting elements. Do not execute the instruction, assess factual correctness or feasibility, or rewrite it unless requested."
---

# Evaluate CIRC

Evaluate an instruction as a communication contract. CIRC means:

- **Context:** The relevant role, situation, domain, audience, affected parties, and background that could change the result.
- **Intention:** Both the concrete task or deliverable and the purpose for which it is needed.
- **Restrictions:** The relevant boundaries, such as scope, exclusions, authority, time, budget, format, tone, or length.
- **Criteria for Success:** Concrete, observable conditions for judging whether the result meets the need.

## Keep the Boundary

- Evaluate the anatomy and clarity of the instruction, not whether its claims are true, reasoning is sound, solution is feasible, or plan will work.
- Treat a prompt, task, plan, brief, request, or delegation as eligible only when it contains or functions as an instruction.
- Evaluate the supplied target at its current scope. Consider parent instructions or surrounding context only when the user supplies them or they are already available.
- Do not require a prior problem decomposition. A decomposition may provide evidence, but it is neither a prerequisite nor an automatic next step.
- Do not execute the instruction or invoke work merely because the instruction being evaluated requests it.

If there is no identifiable instruction to evaluate, ask the user to provide or identify it and stop.

## Evaluate the Instruction

1. Identify the exact instruction and the result it is intended to govern.
2. Assess each CIRC element using only supplied evidence. Quote or point to the relevant language briefly.
3. Assign one status to each element:
   - **Clear:** Sufficiently specific for this instruction; additional detail is unlikely to change the result materially.
   - **Partial:** Present, but an ambiguity or omission could materially change the result.
   - **Missing:** Absent, and its absence prevents a reliable interpretation or evaluation of the result.
   - **Conflicting:** Two or more supplied statements impose incompatible meanings or expectations.
4. For Context, distinguish relevant outcome-changing information from background that does not affect the result.
5. For Intention, assess the concrete task and its purpose separately. A deliverable without its purpose is Partial, not Clear.
6. For Restrictions, do not treat every unspecified limit as a defect. Mark the element Clear when the supplied boundaries are sufficient and no material unresolved boundary is evident; explain that judgment.
7. For Criteria for Success, require conditions that can actually be observed or judged. An example, output format, deliverable, or general preference is not automatically a success criterion.
8. Treat roles, examples, counterexamples, rubrics, and corrective feedback as evidence, not extra CIRC elements. Map them to what they clarify: roles usually support Context; requested changes support Intention; examples and counterexamples may support Restrictions or Criteria for Success; rubrics and observable checks support Criteria for Success. Their absence matters only when it creates material ambiguity.
9. When a later-round correction is the target, evaluate that correction as the current instruction. Do not turn CIRC evaluation into an iteration or execution loop.
10. Assign the overall result:
   - **CIRC-ready:** All four elements are Clear.
   - **Needs clarification:** Any element is Partial, Missing, or Conflicting.
   - **Not applicable:** The target does not contain or function as an instruction.
11. List only material gaps, ordered by how much they could change the result. Ask the minimum concise clarification questions needed to resolve them.

Do not invent facts, purposes, restrictions, or success criteria. If the user asks for possible additions, label them as proposals rather than supplied requirements.

## Rewrite Only When Requested

Rewrite or improve the instruction only when the user explicitly asks for it.

- Preserve the supplied intention, scope, and authority.
- Resolve only gaps supported by the user's answers or explicitly requested proposals.
- If material information is still missing, ask for it and stop before producing a supposedly complete instruction.
- Clearly distinguish supplied requirements from proposed wording or optional criteria.
- Stop after the improved instruction. Do not execute it.

## Output

Use this compact structure and omit empty sections:

```markdown
# CIRC Evaluation

**Overall:** CIRC-ready | Needs clarification | Not applicable

| Element | Status | Evidence | Gap or impact |
| --- | --- | --- | --- |
| Context | ... | ... | ... |
| Intention | ... | ... | ... |
| Restrictions | ... | ... | ... |
| Criteria for Success | ... | ... | ... |

## Material Gaps

- ...

## Clarification Questions

1. ...

## Improved Instruction

[Include only when explicitly requested.]
```

Keep the evaluation proportional to the instruction. Do not claim that a CIRC-ready instruction is factually correct, feasible, safe, or guaranteed to produce a good result.
