# Friday 0.4.0: wake, API routing and response delay

The user reports that “Hey Friday” is not detected, a Deepgram free/trial credential produces HTTP 404, and replies feel slow. Exact phone status, URL, error body and timing measurements were not supplied. No phone is connected to this workspace and no live API key is available.

## Evidence and diagnosis

| Observation | Finding | Limit |
|---|---|---|
| The chat adapter appends `/chat/completions` to any configured Base URL. Deepgram documents `/v1/listen` for transcription, with `Authorization: Token …`. | A Deepgram speech URL entered as a custom dialogue endpoint is incompatible with this adapter and explains a 404 by a concrete mechanism. | This is a probable cause of the user's error; their exact request/error body is unavailable. Deepgram's separate Voice Agent WebSocket API can orchestrate LLMs, but this app does not implement that protocol. |
| The microphone loop previously logged failures without clearing its running flag or reporting them to the service. Nonpositive reads could loop indefinitely. | Verified microphone-health reporting/recovery defects could leave the UI looking ready without usable input. | They do not establish the exact cause on either physical phone. |
| Standby previously used call-mode audio processing whenever the “interrupt by talking” preference was set, although automatic recognition cannot use that talk-over path. | A contributing risk: call suppression/routing can degrade quiet/far-field wake input. Standby now uses recognition input. | Hardware behavior still needs phone testing. |
| Native sherpa-onnx 1.13.8, the exact shipped int8 KWS files and current token definition were exercised against five local flite voices. | Keyword configuration can detect the phrase, but acoustic robustness is unresolved. Baseline: 6/10 positive clips detected, 3/15 negative clips accepted. Misses were kal/kal16 voices; confusions were “Hey Freddy.” Lowering threshold from .2 to .1 did not improve these cases. | These are synthesized clips, including quieter copies, not independent speakers, background rooms, measured real false-accept/reject rates or Android performance. |
| Active recognition waited 1,500 ms of ending silence and first spoke “I am listening.” Each dialogue session constructed an unrelated HTTP client. | Verified avoidable delays: endpoint wait, acknowledgement speech and lost connection reuse. | Overall response delay has not been measured on a phone/provider. |
| Groq's official model-deprecation page retires the old Llama 3.3 default on free/developer accounts. | Verified outdated preset. New Groq profiles use `openai/gpt-oss-20b`; existing user-selected model IDs remain intact. | Account access and real task quality require the user's provider connection test. |

The recurrence condition was missing operational evidence: mocked transport/unit checks and a valid token vocabulary did not establish real microphone activity, wake accuracy or live provider compatibility. Home diagnostics and distinct speech/dialogue error labels make that boundary visible.

## Implemented direction

1. Separate speech credentials from the dialogue model. Settings has a Deepgram speech field; existing Deepgram-host custom profiles supply its key without being deleted or becoming the dialogue model. Deepgram uses `/v1/listen`, Token authentication and Whisper automatic language detection. Whisper was selected because the documented language list includes Urdu, Hindi and English; Nova's multilingual language set does not establish equivalent Urdu code-switching coverage. Deepgram itself notes that Whisper is slower than Nova. Credits/model access still apply.
2. Expose wake microphone health, quiet/silent/stalled frames and last detection on Home. Interrupt pending microphone reads on handover, clear running state on exit and report input failures. Native keyword score/threshold are retained: the small synthetic probe does not justify a universal sensitivity change.
3. Replace spoken activation acknowledgement with a short cue, use 900 ms ending silence (600 ms less configured endpoint wait, plus neural VAD’s short internal smoothing), retain up to 300 ms pre-roll, and use a bundled local Silero neural VAD with bounded endpoint detection (conservative energy fallback if native VAD cannot load). Reuse dialogue HTTP connections; require a tool response for Groq's tool-capable route to avoid an unnecessary prose-to-tool retry. Custom/local servers retain their longer timeout.
4. Normalize pasted complete chat/transcription URLs while preserving proxy prefixes. Errors distinguish speech from dialogue APIs and explain 404/model/key/quota failures. Home reports capture, STT and task durations after voice turns; task time includes phone actions and excludes TTS playback.

## Other viable directions and tradeoffs

- One Groq key for Whisper Turbo and GPT-OSS dialogue reduces setup effort and is a useful free-tier candidate; account limits and actual latency must be measured. Deepgram can remain the explicitly selected speech provider with a separate dialogue key.
- Streaming speech/voice-agent APIs can overlap recognition, model generation and playback. They require a distinct streaming/tool-event implementation and verification of Urdu language coverage and device interruption behavior. A fast English-only demo would not satisfy this project's automatic Urdu requirement.
- A speaker-enrolled or specifically trained “Hey Friday” detector may improve accent robustness. It needs real positive samples and negative/background data; a renamed keyword string or relaxed threshold is not evidence of a trained custom wake model.

## Reproduce and evaluate

`tools/evaluate_wake_synthetic.py --model-dir /path/to/kws-model --output-dir /temporary/output` produces synthetic clips and JSON results. The inspected results are in `docs/evidence/wake-synthetic-0.4.0.json`. It requires sherpa-onnx 1.13.8, NumPy, soundfile and ffmpeg/libflite; it sends no audio to a provider.

On the phone: turn Friday on, confirm the wake model is loaded, watch the Home input-health line while speaking, and compare wake activation with the mic button once to isolate keyword detection from active recognition. Test Deepgram speech and a separate dialogue provider; record the stage-specific error and timings. Then measure real wake misses, near-sounding false activations, room noise, screen-off behavior and response delay on both Pixel 8 and Vivo Y20. Do not report either issue as physically resolved until those checks pass.

Sources: [Deepgram transcription API](https://developers.deepgram.com/reference/speech-to-text/listen-pre-recorded), [Deepgram Whisper and its language/latency caveats](https://developers.deepgram.com/docs/deepgram-whisper-cloud), [Deepgram code-switching](https://developers.deepgram.com/docs/multilingual-code-switching), [Groq current models](https://console.groq.com/docs/models), [Groq deprecations](https://console.groq.com/docs/deprecations).

## Neural VAD probe

A 629 KiB Silero VAD model was tested through native sherpa-onnx 1.13.8 with the same 16 kHz/window/threshold configuration. Six synthesized command clips (three voices, normal and 20% amplitude copies) all contained detected speech; silence and seeded steady noise at RMS 0.008 produced no detected speech in this limited set. Results are in `docs/evidence/vad-synthetic-0.4.0.json`. This supports using speech classification instead of lowering an energy threshold; it is not a real-room accuracy or Android CPU measurement. The VAD is loaded only for activated command capture and released before the network transcription request.
