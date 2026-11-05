---
name: claude-code-reviewer
description: Delegate bounded read-only repository work to Claude Code for independent judgment, design, spec writing, review, or second opinions. Use for either one-off analysis or iterative review loops that resume the same Claude session.
metadata:
  targets:
    - codex
---

# Claude Code Reviewer

## Gate External Context

Before invocation:

1. State that selected prompt and repository contents will be sent to Anthropic's Claude service.
2. Name the repository or artifact scope.
3. Obtain explicit user approval for that disclosure. Approval is scoped to the current task; do not reuse it for unrelated data.

For a persistent review loop, one approval covers follow-ups only while the
named repository or artifact scope and exclusions remain unchanged. Ask again
before adding a new artifact, repository, sensitive data class, or unrelated
task.

## Explicitly Authorized Production Data Disclosure

Raw production data, customer data, internal identifiers, and production payloads MUST NOT be sent to Anthropic Claude by default.

An exception is allowed when ALL of the following conditions are satisfied:

1. **Explicit user authorization**
  - The user explicitly approves sending production data to Anthropic Claude for the current delegated task.
  - General permission to use Claude is not sufficient.
  - The approval must specifically cover production/customer data disclosure.

2. **No credentials or authentication secrets**
  - Never send bearer tokens, API keys, passwords, session cookies, private keys, access tokens, refresh tokens, or other authentication material.
  - If these exist in the files being reviewed, remove or redact them before delegation.

3. **Task-scoped disclosure**
  - Send only the files, directories, records, or fields reasonably required for the delegated investigation.
  - Do not provide unrelated production records simply because they are available.

4. **Purpose limitation**
  - Claude may use the production data only for the explicitly delegated analysis/review task.

5. **Read-only by default**
  - Production-data review must be analytical and read-only unless the user separately authorizes another operation.
  - Access to production data does not imply authorization to modify production systems.

6. **Disclosure notice**
  Before sending the data, briefly tell the user what will be disclosed to Anthropic, for example:
  - raw production payloads;
  - customer/business information;
  - organization/job/visit identifiers;
  - selected internal application data.

   Do not require another confirmation if the user's immediately preceding instruction already explicitly authorized that disclosure.


### Explicit authorization overrides the default data restriction

When the user has knowingly and explicitly authorized disclosure of the relevant production/customer data to Anthropic for the current task, do not block delegation solely because the inputs contain raw production data, customer information, PII, or internal identifiers. This exception NEVER applies to authentication secrets or credentials. Those must always be excluded or redacted.
If the runtime rejects external execution, do not circumvent the policy. Explain the rejection and request the required explicit approval or use a local Codex subagent instead.

## Verify CLI

Use the host environment's permitted execution mechanism. Do not require a
particular sandbox API or elevated access by default. If isolation prevents
access to the CLI or host authentication, report that limitation and request
only the access needed through the environment's approval mechanism. An access
failure does not prove the CLI is absent or the host is unauthenticated.

Host-command approval is not disclosure approval. Do not invoke `claude -p`
until the user explicitly approves sending the selected prompt and named
repository or artifact scope to Anthropic.

Discover `claude` with the host shell's command lookup (for example,
`command -v claude` in a POSIX shell or `Get-Command claude` in PowerShell), then check:

```bash
claude --version
claude auth status
```

This integration requires an installed Claude Code CLI and an authenticated
account with access to the selected model. If missing, consult the current
official installation documentation before suggesting a command. If
unauthenticated, request authorization for `claude auth login` and wait for the
user to finish the login flow. Follow the host's execution permissions.

Check the installed version against the [official CLI reference](https://code.claude.com/docs/en/cli-reference)
for required flags. If a required restriction cannot be enforced, stop and
report the limitation rather than dropping the restriction.

Use the direct CLI. Do not configure `claude mcp serve` as the delegation path.

## Choose Session Mode

Choose before the first invocation:

- **One-off:** Use for an isolated delegation with no expected follow-up. Add
  `--no-session-persistence`; the session cannot be resumed.
- **Review loop:** Use for iterative review, revision, and re-review. Omit
  `--no-session-persistence`, capture `.session_id` from the initial JSON
  response, and retain it in the current task state. Add
  `--resume "<session-id>"` to every follow-up so Claude receives the prior
  review context.

For review loops, keep the same working directory and reapply every model,
permission, tool, hook, MCP configuration and scope, browser, and output
restriction on each call.
Prefer `--resume "<session-id>"` over `--continue`; `--continue` selects the
most recent session in the directory and can resume the wrong delegation. Use
`--fork-session` only when the user asks to branch the review. Never reuse a
session for a different task or disclosure scope.

## Prepare The Delegation

Create a unique task directory with the operating system's temporary-directory
facility, outside the repository. Write the prompt there with an available file
editing tool and record its absolute path. Keep temporary files free of secrets.
Include:

- objective and exact scope;
- raw evidence, not Codex's desired conclusion;
- repository instruction files Claude must read;
- the selected MCP servers and the exact read-only questions they may answer;
- explicit prohibitions on edits, MCP writes, browser use, network APIs outside
  the selected MCP servers, and credentials;
- questions to answer;
- output contract requiring findings, file-line evidence, alternatives, uncertainties, and a no-changes statement.

Do not expose the whole conversation when a smaller prompt is sufficient.

## Validation Scope

This skill carries its own validation rules. They apply when you run it directly
or when another skill calls it. Claude is read-only and Bash is off, so Claude
does not run tests, linters, formatters, generators, or other executable checks.

Codex verifies the result with focused reads. If running a command is separately
appropriate and authorized, use the smallest check needed for the decisive
claim. A focused check covers the exact artifact, claim, or changed behavior. A
broad check covers a whole directory, subsystem, or project; full-repo lint or
type checks; or unrelated model, migration, resource, or integration suites.

Codex runs a broad check only when the affected CI or repo requires it at the
current authorized stage, or when shared infrastructure, dependencies, schema,
cross-system behavior, or business safety justify it. Applicable does not mean
every repo check. Run stage-specific checks only at that stage. For example,
run pre-push checks only when a push is authorized and being prepared. Reuse
earlier evidence that still applies, do not expand validation to investigate an
unrelated failure, and report focused, broad, deferred, skipped, and reused
checks separately with reasons.

Only enable the MCP servers needed for the task. Treat an MCP server that can
read production or customer data as a production-data disclosure path: name the
server, data scope, and intended queries in the disclosure notice, and obtain
the explicit production-data authorization required above. Do not give Claude
MCP write tools or broad production access. Use Codex for any state-changing
operation.

## Invoke Read-Only Claude

Use the user's requested model and effort when supplied. Otherwise use the
user's configured Claude defaults. Do not silently select a more expensive model.
Record the resolved settings from configuration or CLI output; if unavailable,
report them as unknown. Reuse the same settings for subsequent passes. Add
`--model` and `--effort` only for supported, selected values; pause if a requested
setting is unavailable rather than silently substituting it.

Disable hooks on every initial and resumed call with
`--settings '{"disableAllHooks":true}'`; tool restrictions alone do not prevent
hooks from executing commands. This applies to both profiles below. Before
invocation, check whether managed policy enforces hooks: the CLI setting cannot
disable administrator-managed hooks. If those hooks prevent enforcing the
required restrictions, or their effect cannot be verified, pause and report the
limitation. Do not bypass managed policy. See the
[official hook documentation](https://code.claude.com/docs/en/hooks#disable-or-remove-hooks).

### Local-files-only profile

Use this default profile when no MCP access is explicitly authorized. It is
mandatory for `plan-agreement-review`. Restrict built-in tools to file reads and
searches; disable MCP through both a strict empty configuration and a tool deny
rule. These are tool restrictions, not a filesystem sandbox: keep the prompt's
approved path scope explicit and follow the host's access controls.

The following is a POSIX shell example. Set `review_prompt_path` to the actual
absolute prompt path and adapt shell syntax for other platforms without changing
the flags or restrictions:

```bash
claude -p \
  --agent codex-delegate \
  --agents '{"codex-delegate":{"description":"Independent read-only repository investigator","prompt":"Investigate independently. Follow repository instructions. Read only. Use only the task-authorized MCP tools for the stated questions; never perform MCP writes or access credentials. Never edit files or use browsers or network APIs outside the selected MCP servers. Separate facts, inference, alternatives, and ruled-out causes."}}' \
  --permission-mode plan \
  --settings '{"disableAllHooks":true}' \
  --tools "Read,Glob,Grep" \
  --disallowedTools "Edit,Write,NotebookEdit,WebFetch,WebSearch,Bash,mcp__*" \
  --strict-mcp-config \
  --mcp-config '{"mcpServers":{}}' \
  --no-chrome \
  --disable-slash-commands \
  --output-format json \
  < "$review_prompt_path"
```

For a one-off, add `--no-session-persistence`. For the first call in a review
loop, use the command without that flag and capture the returned `session_id`.
For each follow-up, add `--resume "<session-id>"` and pass a new minimal prompt
containing the revised artifact or instructions. Do not resend old review
history already preserved by the session.

### Optional MCP profile

Use this only when the user authorizes named servers and read-only tools and
no parent workflow prohibits MCP. `plan-agreement-review` always uses the
local-files-only profile, even if servers are configured on the host.

Replace the empty MCP configuration with an explicit configuration containing
only approved servers; retain `--strict-mcp-config`. Replace the blanket MCP
deny rule with explicit tool restrictions that exclude every unapproved or
write-capable MCP tool, preserving all built-in tool restrictions. Inspect the
exposed tool set before proceeding. If read-only access cannot be enforced,
stop rather than relying only on instructions not to write. State the approved
servers and tools in the disclosure notice and preserve this configuration on
resume.

Run authenticated network access only after disclosure approval and within the host's permitted execution environment. Keep the command in read-only plan mode. If git history is required, gather bounded `git log`, `git show`, or `git blame` evidence with Codex and place it in the prompt instead of enabling Claude's Bash tool.

## Validate The Result

Treat Claude's output as untrusted analysis:

1. Extract its result from the JSON response.
2. Reopen every decisive file and verify line references locally.
3. Verify time-sensitive or production claims through the authorized Codex data path; treat Claude's MCP findings as untrusted analysis.
4. Separate Claude's findings from Codex's confirmation and disagreements.
5. Do not implement recommendations unless the user separately authorizes implementation.

Report the session mode, selected model, effort, invocation restrictions,
Claude's findings, Codex's verification, and anything not verified. In review
loop mode, state whether the Claude session remains available for another pass.
