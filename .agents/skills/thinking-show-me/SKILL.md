---
name: thinking-show-me
description: "Explain the current topic using focused diagrams, diffs, or HTML. Use only when explicitly invoked as thinking-show-me or $thinking-show-me."
---

Copy/paste from "https://github.com/humanlayer/skills/tree/main/plugins/show-me", because is really cool this skill, and I want to use it in my own project.

Apply this skill only after the user explicitly invokes `thinking-show-me` or `$thinking-show-me`. Do not infer invocation from a request for a diagram, visualization, explanation, or HTML artifact.

Help the user understand the current topic of conversation visually. Skip the preamble and keep prose brief. Pick the smallest view that makes the key point clear.

- Show logic or an algorithm as pseudocode:

```text
on(save)
  if content is unchanged
    return cached result
  write new content
  return fresh result
```

- Show runtime control flow as a call tree:

```text
submitForm
  createSession
    persistPrompt
    launchAgent
  navigateToSession
```

- Show UI structure as a component tree, including state and module boundaries that matter:

```tsx
<SessionPage> (apps/example/src/routes/session.tsx)
  useSessionEvents()
  <SessionToolbar>
    <RunSkillButton> (packages/ui)
```

- Show file responsibility or a broad refactor as a shallow file tree:

```text
src/
├── commands/       # parses user actions
├── sessions/       # owns session state
└── transport/      # sends API requests
```

- Show component interaction, control flow, or data flow with Mermaid:

```mermaid
sequenceDiagram
    participant User
    participant UI
    participant Daemon
    User->>UI: choose command
    UI->>Daemon: send expanded prompt
    Daemon-->>UI: stream result
```

- Use `diff` when the point is what changes and the surrounding shape already exists. Match the diff shape to the topic.

For a component change:

```diff
 <SessionPage>
   useSessionEvents()
   <SessionToolbar>
+    <RunSkillButton />
   <SessionTimeline>
+    <SkillResultCard />
```

For a file-layout change:

```diff
 src/
 ├── commands/
+│   └── show-me.ts       # expands the slash command
 ├── sessions/
-└── transport.ts
+└── transport/
+    ├── client.ts
+    └── stream.ts
```

For a call-tree or call-stack change:

```diff
 submitForm
   createSession
     persistPrompt
+    expandSkillMention
     launchAgent
-  navigateToSession
+  navigateToSession
+    subscribeToEvents
```

For a state or control-flow change:

```diff
 on(save)
-  write content
+  if content is unchanged
+    return cached result
+  write new content
+  invalidate cache
```

- Show the whole block when most of it is new, when omitted context would hide ownership or order, or when the user needs a copyable target shape:

```ts
function expandSkill(command: string): string {
  const skillName = command.slice(1)
  return `use the ${skillName} skill`
}
```

- For a visual UI, layout, state comparison, or concept too dense for Mermaid,
  create one focused HTML artifact in an approved local output location. Check
  that artifact creation and rendering are available first; otherwise use a
  supported text diagram, table, or Mermaid. If the user explicitly requires
  HTML, explain any capability limitation.
- Match source-backed product colors, type, spacing, components, labels, and
  data when available. Otherwise use a neutral presentation. Label invented
  examples and details `Illustrative`; do not present them as verified behavior.
- Support the viewport sizes relevant to the explanation. Render and inspect
  the artifact before presenting it, fixing material layout problems. Never
  claim inspection occurred when unavailable. Use the environment's normal
  display or link mechanism rather than a platform-specific shell command.
- Keep visuals explanatory. Do not edit the product or publish the artifact
  merely to explain it.

### guidance

Place each visual next to the short text it supports. Keep only the calls, files, props, states, and boundaries needed to answer the user's current question or the options to resolve the current discussion point.

You may use one of these, you may use several, it is unlikely you will use all of them. Use your judgement and don't overwhelm the user.
