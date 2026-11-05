# Source analysis

Inspected 2026-11-05: [judejulius/jarvis](https://github.com/judejulius/jarvis) at `5f896a5be08c6024a7c8285a7969a03c8e4c22c2`. Public MIT-licensed source analysis, not proprietary firmware extraction. Upstream performance claims are not our device results.

| Inspected module | Observed architecture | Apna adaptation |
|---|---|---|
| VoiceInput | One 16 kHz stream; local KWS, streaming ASR, buffered wake audio and echo handling | Genuine BPE sequences for three wake phrases; selection feeds KWS and transcript cleanup |
| JarvisService → ApnaService | IDLE/LISTENING_COMMAND/WORKING, reply interruption and fixed follow-up | Configurable timeout, sleep commands, cleared history, callback generations, lifecycle wake lock and unlock failure handling only for UI actions; conversation can work while locked or without Accessibility |
| PhoneAgent | Observe, model, tool, refreshed screen; provider fallback | Runtime skills, native device actions; one tool executes per model turn so subsequent calls cannot use stale screen IDs |
| ScreenReader | Numbered visible nodes and text | Password redaction and bounded traversal |
| Accessibility service | Node actions, gestures, app launching and optional screenshots | Reject stale nodes/password typing; screenshot sharing defaults off |
| ModelDownloader | Archive download, traversal check, staged install | Retained with regression tests |

## Wake detection is not a label

Renaming a UI string does not change an acoustic detector. These pieces were generated from the actual downloaded keyword model's `bpe.model` with SentencePiece:

```text
HEY APNA       → ▁HE Y ▁A P N A
HEY ASSISTANT  → ▁HE Y ▁AS S IST ANT
HEY COMPUTER   → ▁HE Y ▁COMP U TER
```

They are runtime keyword definitions, not a newly trained acoustic model. Accuracy needs physical speakers and noise tests. `tools/wake_tokens.py` reproduces generation. Adding another phrase requires registering it in WakePhrases.kt, rebuilding and evaluating.

## Limits

The voice architecture remains chained STT/LLM/TTS. Accessibility cannot fully describe every custom-drawn view, and model decisions do not guarantee app semantics. This build includes ARM64 native libraries. No physical phone or live-provider credential was connected during the initial build. See VALIDATION.md for actual checks and EVALUATION.md for field measurements.
