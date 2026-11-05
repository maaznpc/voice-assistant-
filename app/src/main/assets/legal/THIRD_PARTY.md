# Credits and provenance

Apna Assistant derives from **Jarvis**, copyright 2026 Mohammad Joud Julius, MIT license.
Source: https://github.com/judejulius/jarvis at `5f896a5be08c6024a7c8285a7969a03c8e4c22c2`.
The original LICENSE is unchanged. The initial local import commit preserves the source before customization.

Retained foundations include the Kotlin UI, accessibility adapter, audio pipeline, provider clients, model downloader and upstream tests. Apna adds its application identity, wake vocabulary, local sleep/timeout, native actions, runtime skills, privacy defaults, screen-action sequencing and evaluation docs. It is an attributed derivative.

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

Development guidance: Google's [Android permissions security skill](https://github.com/android/skills/blob/main/security/android-permissions-security/SKILL.md), read from GitHub. It is development guidance, not a phone runtime capability. The two bundled runtime skills were written for Apna.

Sanna was considered during discovery, but GitHub metadata reported no license; its source was not imported. No proprietary Nest/Alexa firmware was decompiled or used.
