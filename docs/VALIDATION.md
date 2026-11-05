# Validation — 2026-11-05

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
