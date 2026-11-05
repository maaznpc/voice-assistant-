# Changes

## 0.2.1-friday

- Renamed the launcher, screens, notifications and assistant identity to Friday; updated the icon to F.
- Added the model-tokenized Hey Friday default and named sleep commands. Existing Apna wake selections resolve to Friday; Assistant and Computer selections remain available.
- Preserve date phrases such as "on Friday" when cleaning recognized commands.
- Retained the application ID and preferences for updates signed with the same key. The rename does not change the Play Protect installation restriction.

## 0.2.0-apna

- Attributed MIT Jarvis source import and new com.maaz.apna identity.
- Model-tokenized Hey Apna / Hey Assistant / Hey Computer wake choices.
- Local sleep commands, 5–60 second follow-up timeout and conversation clearing.
- Native Android device actions and bundled Markdown skills.
- Password redaction, stale-node rejection, screenshot opt-in and one-action-per-observation loop.
- Device evaluation guide, build workflow and feature regression tests.
