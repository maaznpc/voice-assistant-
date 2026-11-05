# Credits and provenance

Friday derives from **Jarvis**, copyright 2026 Mohammad Joud Julius, MIT license.
Source: https://github.com/judejulius/jarvis at `5f896a5be08c6024a7c8285a7969a03c8e4c22c2`.
The original LICENSE is unchanged. The initial local import commit preserves the source before customization.

Retained foundations include the Kotlin UI, accessibility adapter, audio pipeline, provider clients, model downloader and upstream tests. Friday adds its application identity, wake vocabulary, local sleep/timeout, native actions, runtime skills, privacy defaults, screen-action sequencing and evaluation docs. It is an attributed derivative.

| Dependency | Purpose | Source/license |
|---|---|---|
| sherpa-onnx 1.13.8 | KWS, ASR, TTS runtime | https://github.com/k2-fsa/sherpa-onnx — Apache-2.0 |
| Zipformer gigaspeech 3.3M KWS | Wake model | https://github.com/k2-fsa/sherpa-onnx/releases/tag/kws-models — consult model documentation |
| Kroko streaming Zipformer English | Offline ASR | https://github.com/k2-fsa/sherpa-onnx/releases/tag/asr-models — consult model documentation |
| Kokoro | Optional local TTS | https://huggingface.co/hexgrad/Kokoro-82M — Apache-2.0 |
| OkHttp | Networking | https://github.com/square/okhttp — Apache-2.0 |
| Anthropic Java SDK | Provider adapter | https://github.com/anthropics/anthropic-sdk-java — MIT |
| AndroidX / Material Components | UI/platform | Apache-2.0 |
| Apache Commons Compress | Model unpacking | Apache-2.0 |
| Gradle wrapper | Build tool | Apache-2.0 |

Development guidance: Google's [Android permissions security skill](https://github.com/android/skills/blob/main/security/android-permissions-security/SKILL.md), read from GitHub. It is development guidance, not a phone runtime capability. The phone and conversation runtime skills were written for Friday.

Sanna was considered during discovery, but GitHub metadata reported no license; its source was not imported. No proprietary Nest/Alexa firmware was decompiled or used.

Thinking skills: https://github.com/SaulBurgos/thinking-skills at `f99b1b2b22d639de5103dbeefd59c9ecdf46b1ef`, MIT, copyright (c) 2026 Saul Burgos Davila. All 20 original Codex skill folders (including references, agents, assets and scripts) are preserved under `.agents/skills/`. Friday loads a separate, concise voice adaptation of selected principles from `app/src/main/assets/skills/thinking/SKILL.md`; it does not execute the original developer workflows. The MIT notice is included in `app/src/main/assets/legal/THINKING_SKILLS_LICENSE.txt` and the in-app licenses dialog.
