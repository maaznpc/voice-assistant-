#!/usr/bin/env python3
"""Initialize one investigation tracking record at an exact destination path."""

from __future__ import annotations

import argparse
from datetime import datetime
from pathlib import Path
import sys


DEFAULT_AUTHORIZATION = (
    "Tracking-file changes only. Investigation, external access, planning, "
    "implementation, and other follow-up actions require separate authorization."
)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Initialize an investigation tracking record."
    )
    parser.add_argument("path", help="Exact destination path for the new .md record")
    parser.add_argument("--title", required=True, help="Short investigation title")
    parser.add_argument(
        "--question",
        required=True,
        help="Exact question the tracked investigation must answer",
    )
    parser.add_argument("--parent", default="None", help="Parent investigation path")
    parser.add_argument("--owner", default="Unassigned", help="Investigation owner")
    parser.add_argument(
        "--subject",
        default="Not specified",
        help="Subject or system being tracked",
    )
    parser.add_argument("--related-task", default="None", help="Related issue or task")
    parser.add_argument(
        "--authorization-boundary",
        default=DEFAULT_AUTHORIZATION,
        help="Authorized and prohibited actions for this tracking record",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    destination = Path(args.path).expanduser()

    if destination.suffix.lower() != ".md":
        print("error: destination must end in .md", file=sys.stderr)
        return 2

    if destination.exists():
        print(f"error: refusing to overwrite existing file: {destination}", file=sys.stderr)
        return 2

    skill_dir = Path(__file__).resolve().parent.parent
    template_path = skill_dir / "assets" / "investigation-record.md"
    if not template_path.is_file():
        print(f"error: template not found: {template_path}", file=sys.stderr)
        return 2

    timestamp = datetime.now().astimezone().isoformat(timespec="seconds")
    replacements = {
        "{{TITLE}}": args.title,
        "{{QUESTION}}": args.question,
        "{{STARTED_AT}}": timestamp,
        "{{LAST_UPDATED_AT}}": timestamp,
        "{{OWNER}}": args.owner,
        "{{SUBJECT}}": args.subject,
        "{{RELATED_TASK}}": args.related_task,
        "{{AUTHORIZATION_BOUNDARY}}": args.authorization_boundary,
        "{{PARENT}}": args.parent,
    }

    content = template_path.read_text(encoding="utf-8")
    for token, value in replacements.items():
        content = content.replace(token, value)

    destination.parent.mkdir(parents=True, exist_ok=True)
    try:
        with destination.open("x", encoding="utf-8") as record:
            record.write(content)
    except FileExistsError:
        print(f"error: refusing to overwrite existing file: {destination}", file=sys.stderr)
        return 2
    print(destination.resolve())
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
