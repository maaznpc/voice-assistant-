# Validation — 2026-11-05




## Friday 0.3.2 — Thinking Skills

- Imported all 20 original Codex skill folders from `SaulBurgos/thinking-skills` commit `f99b1b2b22d639de5103dbeefd59c9ecdf46b1ef` into `.agents/skills/`. Compared all 55 files in those folders byte-for-byte against the source; supporting files are intact. Upstream README and MIT license are also retained.
- Friday loads a separate 2,530-character voice adaptation through `Settings.skillInstructions()`, below the 6,000-character per-skill loader limit. The existing loaded-skills test verifies that the thinking entry and its completion/short-reply guidance reach the model instructions.
- `./gradlew assembleDebug testDebugUnitTest lintDebug --no-daemon`: **BUILD SUCCESSFUL**; **70 tests passed**, zero failures/errors. Lint: **zero errors, 47 warnings**.
- APK ZIP inspection confirmed both `assets/skills/thinking/SKILL.md` and `assets/legal/THINKING_SKILLS_LICENSE.txt`. The license matches the source and is included in the in-app licenses dialog. `apksigner` verified the APK; `aapt` confirmed Friday version `0.3.2-friday`/5 and package `com.maaz.apna`.
- Local APK: `artifacts/Friday-0.3.2-debug.apk`, 28038955 bytes; SHA-256 `a02aed85b90fa8c8ad2ae94926727a287b75e3c84b6755d5aff4035b0f414d45`. CI artifacts may have another hash/signing key.
- These checks verify installation, packaging and prompt loading. No live model or physical phone was available to measure any improvement in reasoning or task performance. The original Codex workflows/scripts were installed, not executed; no Claude review was invoked.

## Friday 0.3.1 — Automatic language

- Removed manual language selection; old stored language preferences are ignored. Each request/conversation drives response language and script. Urdu/Roman Urdu/Hinglish are handled without asking the user to classify them.
- `./gradlew assembleDebug testDebugUnitTest lintDebug --no-daemon`: **BUILD SUCCESSFUL**. **70 tests passed**, zero failures/errors. Lint: **zero errors, 47 warnings**.
- Additional checks use MockWebServer to validate Gemini inline-audio transcription requests and responses, Groq multipart Whisper transcription without a forced language, configured provider authentication, WAV header/PCM integrity, old preference migration, Android Urdu/Hindi/English hints and script-sensitive TTS routing. All provider responses in these tests are fake.
- The active microphone capture uses a fixed energy threshold, a 300 ms pre-roll, 1.5 s ending silence and a 20 s speech cap. It keeps audio in memory and uploads only after activation to the configured primary Gemini/Groq/OpenAI provider. Older Android devices can use this path independently of system automatic-language switching. Other providers use Android speech recognition, with service/device limitations.
- `apksigner` verified the local APK; `aapt` confirmed label Friday, package `com.maaz.apna`, version `0.3.1-friday`/4, minimum API 26 and ARM64. The updated Settings render was visually inspected and contains no language buttons.
- Local APK: `artifacts/Friday-0.3.1-debug.apk`, 28100024 bytes; SHA-256 `f6140de8c30b9b15f7f47087c5897567bd0ced9907eec3c3001763245b73dc98`. GitHub CI artifacts may differ in hash and signing key.
- No physical microphone, live API key or phone was available. Real recording/voice activity detection, cancellation during capture, multilingual recognition accuracy, latency, quota, TTS pronunciation and phone installation are unverified. The legacy native offline-English harness is excluded from the normal suite and does not validate this automatic path.

## Friday 0.3.0 — Urdu and Roman Urdu

- `./gradlew assembleDebug testDebugUnitTest lintDebug --no-daemon`: **BUILD SUCCESSFUL**. **64 tests passed**, zero failures/errors. Lint: **zero errors, 47 warnings** (including two advisory SwitchIntDef warnings for recognition error codes).
- New Robolectric checks cover Urdu recognition delivery, cancellation/late callback rejection, timeout, Urdu/Hindi local sleep controls, distinct Roman-display/Urdu-speech agent results, installed Urdu TTS and an explicit missing-Urdu-voice path. The English cloud-voice fallback test now explicitly supplies an installed English voice.
- The recognizer and TTS tests use Android shadows and a fake model; they do **not** measure real recognition accuracy, pronunciation, network recognition or API provider behavior.
- `apksigner` verified the APK. `aapt` confirmed label Friday, package `com.maaz.apna`, version `0.3.0-friday`/3, minimum API 26 and ARM64. The updated language settings render was visually checked and the README image refreshed.
- Local APK: `artifacts/Friday-0.3.0-debug.apk` — 28085141 bytes. SHA-256: `3bbda2526124697ac2bb675519c324e2d1c63945bd24e7dc4a9fa5ead2350b7a`. CI artifacts use **Friday-debug** and may have a different hash/signing key.
- Urdu/Roman Urdu and Hindi/Hinglish command recognition uses the installed Android speech service after wake-up and may send activated audio to that provider. English code-switching requests on Android 14+ depend on service support. Urdu/Hindi replies require an installed matching TTS voice or a multilingual cloud TTS provider. Wake detection remains local. Talk-over interruption remains limited to offline English; other modes support hands-free follow-ups after replies.
- No physical phone or live model key was available. The existing Play Protect block remains a separate unresolved installation issue; changing language support does not remove it.

## Friday 0.2.1

- App label **Friday**, version code **2**, package `com.maaz.apna`, minimum Android 8.0/API 26, ARM64.
- `./gradlew assembleDebug testDebugUnitTest lintDebug --no-daemon`: **BUILD SUCCESSFUL**; **57 tests passed**, zero failures/errors; lint **zero errors and 45 warnings**.
- Updated existing checks cover the Friday default, migration from the old wake selection, alternate wake phrases, named sleep and preservation of dates such as "on Friday" in transcripts.
- `HEY FRIDAY` was encoded with the actual KWS `bpe.model` as `▁HE Y ▁F RI DAY`; each piece exists in `tokens.txt`. Acoustic wake accuracy still requires phone testing.
- `apksigner` verified the generated APK's v2 signature. `aapt` confirmed its Friday label, version, package and minimum SDK. The home screen and F launcher icon were visually inspected from Robolectric renders; README screenshots were refreshed.
- Local APK: `artifacts/Friday-0.2.1-debug.apk` — 28021624 bytes; SHA-256 `c7ec39c7651ed9149091e8b5e57c2893d8e0d59a6367862c184a7a628cef8288`. CI artifacts may differ and are named **Friday-debug**.
- This rename does not resolve the previously reported Play Protect block. No phone installation or live voice/provider test was possible in this workspace.

## Earlier validation: Apna 0.2.0

Version: `0.2.0-apna`; package: `com.maaz.apna`; minimum API 26; target/compile API 35; packaged architecture ARM64.

- `./gradlew assembleDebug testDebugUnitTest lintDebug`: **BUILD SUCCESSFUL** with JDK 17, Gradle 8.11.1, AGP 8.7.3 and Android build-tools 35.0.0.
- **57 included unit/Robolectric tests passed**, zero failures/errors. They cover local sleep/history clearing, wake selection, ordinary conversation without Accessibility/unlock, batched tool rejection, device-action validation, lifecycle/crash handling, mocked cloud speech transport, archive handling and UI flows.
- Android lint: **zero errors; 45 warnings**. Remaining warnings include localization/accessibility cleanup, dependency updates, ARM64-only packaging, battery optimization, local-server cleartext support and other platform advisories. Lint passing is not a claim of zero warnings.
- UI test renders were visually inspected; the README screenshots are those renders, not captures from a physical phone.
- All three wake phrases were encoded with the downloaded KWS model's actual SentencePiece model and checked against its token vocabulary. This checks configuration, not acoustic accuracy.
- Debug APK signature verified with Android `apksigner`; APK package/SDK metadata inspected with `aapt`.

## Artifact

`artifacts/Apna-Assistant-0.2.0-debug.apk` — 29,051,813 bytes. This local artifact is intentionally gitignored; the GitHub Actions workflow can produce downloadable APK artifacts from source.

SHA-256: `f69b7590c50e44966d49cb76fb06b2d449324ea6cd7bb6c781b4216d6562bc74`

Separately, the APK downloaded from successful GitHub Actions run `41966859354` was verified with `apksigner` (valid v2 debug signature) and `aapt` (package/version/minimum SDK/ARM64). Its SHA-256 is `d466cef8ac1857bd483e076021e5e0c9f96f4278773ce182d277c061270582c2`; it differs from the local APK. The user reported the sensitive-data/fraud Play Protect installation block on Pixel 8 and Vivo Y20. See [Windows installation diagnosis](INSTALL_WINDOWS.md). Physical installation remains unverified; signature verification does not establish Play Protect acceptance.

## Not verified

`adb devices` reported no connected device. Real microphone/speaker operation, wake false accepts/rejects, screen-off/OEM behavior, power use, interruptions in real noise, live API authorization/cost/latency, and real third-party app task completion were **not** tested. No provider API key was available. The optional native real-engine suite is excluded from the normal unit-test run. This remains a development build until the physical-device checklist in EVALUATION.md is completed.
