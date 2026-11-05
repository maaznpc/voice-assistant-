# Companion Workflows

Companions sit beside the six mandatory thinking stages. Show their state in progress updates, but do not add them to the stage count.

## Order

1. Route Bug or Feature.
2. Determine whether a repository workflow applies. If so, complete its read-only preflight before stage 1 or any record or plan write.
3. Offer durable tracking when its skill is available and the work is long, handoff-sensitive, or needs durable status. Pause for the answer before its initialization point. If accepted, show the exact existing or new record path, the exact same-folder `README.md` path when it may be required, and the allowed operations.
4. Run the selected workflow. Return here for tracking checkpoints and Git gates.

## Durable tracking

`thinking-track-investigation` is an optional persistent sidecar. Read its `SKILL.md` completely before the first operation.

- An existing record uses `Orient` before stage 1. Reconcile conflicts before continuing.
- A new Bug record starts after Bug stage 1. A new Feature record starts after Feature stage 2.
- If an unexpected pause, handoff, or likely compaction occurs before that point, initialize with the available supplied context, mark missing decomposition as pending, and import it once available. Never invent the missing context.
- Before a solution or scope decision pause, use `Checkpoint`. After approval, use `Update` to record the decision before resuming.
- Before stage 5, record the phase boundary: completed investigation and scoping, authorized planning, any deferred implementation, its handoff, governing workflow, and resume requirement.
- Before any unexpected pause, handoff, or likely context compaction, use `Checkpoint`.
- After stage 6 passes the selected review gate, use `Update` to record the plan path, selected route, verdict, and applicable review metadata. If the investigation question is resolved, use the ready-for-closure state; never close automatically.
- After each operation, return to the parent workflow. Tracker operations do not satisfy a thinking-stage gate.

The user's approval must name the record path and, when applicable, its exact navigation-only `README.md` path. It may authorize `Orient`, `Initialize`, `Update`, and `Checkpoint` for those paths during this run. A different path, `Create a child`, or `Close` needs separate approval.

## Repository workflow

Read the target repository's applicable instructions. Apply any required checkout or
workspace checks before stage 1 or a record or plan write, according to their scope.
Use the repository's documented branch, base, and artifact-location rules. Do not
invent a branch prefix, default branch, worktree requirement, or pull request workflow.
If no Git workflow is required, mark this companion not applicable.

- Report unmet requirements and the exact required state; pause before affected work
  without changing Git state automatically.
- Keep records and plans in the location required by the repository. Recheck applicable
  requirements after resume or context compaction.
- After stage 6 passes, report any repository-defined continuation gate and, when
  tracking is active, record the governing instruction and resume requirement.
- Implementation and Git mutations remain outside this orchestrator's authorization.
  Follow the repository's instructions and the user's authorization for later work.

The repository workflow does not consume the one-at-a-time optional-complement
suggestion limit.
