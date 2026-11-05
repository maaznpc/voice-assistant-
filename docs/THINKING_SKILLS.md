# Thinking Skills installation

Source: https://github.com/SaulBurgos/thinking-skills
Pinned commit: `f99b1b2b22d639de5103dbeefd59c9ecdf46b1ef`.
License: MIT, copyright (c) 2026 Saul Burgos Davila.

All **20** skill directories are installed unchanged in `.agents/skills/`, including their `agents/`, `references/`, `assets/` and `scripts/` files where present. The upstream README and license are retained alongside them. These are repository-local Codex development skills; this installation does not register them in unrelated repositories or the cloud skills catalog. Invoke the relevant skill in a Codex session that discovers this repo, for example `$thinking-decompose-problem` or `$thinking-explain`.

Installing the collection does not execute its scripts or invoke its workflows. Their own scope, approval and external-review requirements apply when invoked. No material has been sent to Claude Code/Anthropic by this installation.

## Friday runtime

The Android app loads `assets/skills/thinking/SKILL.md` through `Settings.skillInstructions()` into the phone agent's system prompt. This file is a separately identified voice adaptation of selected principles: decompose complex requests, distinguish observations from hypotheses, compare viable choices against user priorities, explain briefly, execute authorized phone tasks and verify results.

The app does not load all 20 developer workflows into every conversation. Their full templates, references, Python scripts and Claude CLI integrations are intended for a developer environment; they are not Android phone tools. The adaptation does not add APIs, model weights or permissions, and does not guarantee reasoning or task performance. The existing automatic language and confirmation rules still apply.

The source license is packaged at `assets/legal/THINKING_SKILLS_LICENSE.txt` and displayed in Settings → About Friday → Open source licenses.
