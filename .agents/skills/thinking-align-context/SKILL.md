---
name: thinking-align-context
description: Show the agent's current understanding of the goal, which earlier conversation information is guiding its responses and why, and how earlier topics relate to that goal. Use when the user wants to inspect or correct the agent's focus, assumptions, or interpretation after a conversation covers multiple topics. Do not use for ordinary progress summaries or task resumption briefs.
---

# Thinking Align Context

Make the agent's working interpretation visible so the user can spot and correct
a misunderstood goal, overlooked information, or mistaken topic relationship.
Report the interpretation held before this request; do not treat the request for
alignment itself as replacing the substantive goal.

## Method

- Use the conversation and context currently available. Do not run tools by
  default. If the user explicitly requests checking earlier history or an
  artifact, make only the narrow read-only retrieval needed and distinguish
  recovered information from the interpretation held before retrieval.
- Identify the overall goal and immediate request when they differ. Preserve
  explicit user changes and corrections. If more than one goal is plausible,
  expose the ambiguity instead of selecting one silently.
- Select earlier facts, constraints, decisions, corrections, and assumptions
  that materially shape the response or proposed next action. For each, briefly
  explain its practical effect. Report concise conclusions and supporting
  evidence, not private deliberation or internal attention measurements.
- Distinguish what the user explicitly established from the agent's inference.
  Mention the relevant statement or event when it helps the user check the
  interpretation. Do not invent quotations or precise references.
- Describe earlier topics by their relationship to the understood goal. A topic
  may support it, be a separate discussion, have been explicitly completed,
  deferred, replaced, or dropped, or have an unclear status. A subject change,
  silence, or recency alone does not establish abandonment or completion.
- Include topics whose uncertain relevance or status could materially change
  the agent's focus. Do not enumerate every topic or repeat the full history.
- Make material assumptions, conflicting interpretations, and known context
  gaps visible. Do not claim complete recall or that omitted information is
  necessarily irrelevant. Disclose known reliance on a summary or incomplete
  history when it affects confidence; do not invent a missing-context problem.

## Output

Keep the report short enough to inspect and correct quickly. Use these fields,
omitting empty optional fields and avoiding repetition:

**Goal I understand:** State the substantive goal and, if different, the current
request. Identify ambiguity when present.

**Context guiding my responses:** Give a few concise bullets in the form
`Earlier information → how it affects my response`. Label material assumptions
or inferences; distinguish them from explicit user decisions.

**Earlier topics and their relationship:** Briefly identify relevant topic
relationships and their evidence. Mark status as unclear when the conversation
does not establish it. Include this field only when other topics matter.

**Possible misalignment or gaps:** Name material uncertainty, potentially
outdated assumptions, or unavailable information without inventing a problem.
Omit when none is apparent.

End with a brief invitation to correct the goal, relevance, or topic status.
Do not require confirmation before every future action or turn the invitation
into a new approval gate.

## Boundaries

- This is a read-only interpretation report. Do not edit files, change task
  state, send messages, resume substantive work, or silently reprioritize goals.
- Do not turn the report into a progress recap, completed-work list, handoff,
  implementation plan, or new solution recommendation.
- Do not claim to inspect internal attention weights, score message importance,
  prove context retention, or diagnose forgetting from uncertainty alone.
- When the user corrects the report, incorporate that correction into the
  stated understanding without treating it as permission to execute work.
