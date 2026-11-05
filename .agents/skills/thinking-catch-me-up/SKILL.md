---
name: thinking-catch-me-up
description: Reorient the user to a forgotten current task with a short, read-only resumption brief explaining the problem, work completed, exact stopping point, and next action. Use when the user asks to catch up, remember what they were doing, understand where work stopped, or learn what needs attention before continuing. Do not use for cross-task portfolio reports, manager checkpoints, formal handoffs, or ordinary progress updates when the user already has the task context.
---

# Thinking Catch Me Up

Help the user resume this task without rereading the conversation. Assume they
may remember nothing about the task. This is a report, not permission to
continue the work.

## Method

- Reconstruct the task in plain language: what was wrong, why it mattered, the
  intended outcome, meaningful results and decisions, where work stopped, and
  the next action.
- Make the brief standalone. Do not rely on a task ID, PR, file, plan option, or
  prior decision being familiar; explain what each important reference means.
- Use the conversation as the default source. Do not run tools just to produce
  the brief. If the user asks for current or live status, make only the narrow
  read-only checks needed and identify anything that remains last known.
- Report outcomes and decisions, not tool activity or a detailed chronology.
  Include only completed work that helps the user understand the present state
  or continue safely.
- Separate verified facts, decisions, hypotheses, and unknowns when the
  distinction affects what the user should believe or do next.
- Write the shortest complete explanation, usually about 150 to 200 words. Use
  more only when necessary for safe resumption; never omit essential context to
  satisfy a word target.
- Before responding, read the brief as if you had forgotten the entire task. If
  it does not answer what the problem was, what has been accomplished, where
  work stopped, and what happens next, revise it.

## Output

Use this order:

**Bottom line:** In one or two sentences, name the task in plain language, say
where it stopped, and state whether the user needs to act. Put the present state
before supporting history.

**Problem:** Explain what was wrong, why it mattered, and the intended outcome.
Keep this to one or two sentences.

**Done so far:** List only meaningful results and settled decisions. Prefer one
to four concise bullets; do not list searches, commands, tool calls, or every
step taken.

**Where we stopped:** State the exact operational status: working, waiting,
blocked, decision needed, or complete. Name the blocker, pending decision, or
unverified state when relevant. Say `Last known` when the state was not checked
recently.

**Next:** Give one immediate next step, its owner, and any decision or approval
required. Say `No action needed` when appropriate. Because this brief does not
authorize execution, describe what would happen if the user chooses to
continue; do not start it.

Add **Key references** only when a few links, files, task IDs, or PRs will help
the user resume. Explain opaque references briefly and include no more than
five.

Avoid repeating the same fact across fields. The bottom line summarizes the
resume point; the remaining fields supply only the context needed to understand
and act on it.

## Boundaries

- Do not edit project or task files, resume execution, send messages, update
  task state, or make other operational side effects.
- Do not present stale or inferred state as current fact.
- Do not reopen settled decisions or recommend new scope without a material
  reason.
- Do not turn the brief into a transcript, management report, formal handoff,
  or full project history.
- If the task is complete, identify the outcome, mark the stopping state as
  complete, and say `No action needed` instead of inventing another step.
