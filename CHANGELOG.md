# Changes

## 0.3.1-friday

- Removed language selection. Infer language/script from every request and conversation; ignore old per-language preferences.
- Added automatic multilingual activated-audio transcription through the configured primary Gemini/Groq/OpenAI API; Android speech recognition remains available for other providers.
- Added bounded, cancellable in-memory command capture; no recordings saved and no audio upload in standby.
- Removed the unused English-only command-model download from onboarding/settings.

## 0.3.0-friday

- Added Urdu, Roman Urdu, Hindi and Hinglish speech/reply settings with Android recognition after activation and optional Android 14+ English switching.
- Added native-script speech fields for Roman replies, script-aware system TTS and an explicit missing-voice message; skip English-only Kokoro for Urdu/Hindi.
- Added system-recognizer follow-ups, bounded recognition timeout and stale callback rejection, plus native Urdu/Hindi sleep phrases.
- Wake detection remains local. Urdu/Hindi recognition and pronunciation depend on the phone service and installed TTS voices and have not been tested on physical phones.

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
