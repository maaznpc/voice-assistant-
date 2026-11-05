# Test-install Friday from Windows

The app is now named **Friday**. New builds use the **Friday-debug** artifact on the [Android build page](https://github.com/maaznpc/voice-assistant-/actions/workflows/android.yml). The package remains `com.maaz.apna`. The original blocked build and its verified hash below refer to the earlier Apna release; renaming does not resolve the Play Protect block.

The reported message is **“App blocked to protect your device — This app can request access to sensitive data. This can increase the risk of identity theft or financial fraud.”**

[Google's developer guidance](https://developers.google.com/android/play-protect/warning-dev-guidance) identifies this as protection against internet-sideloaded apps declaring sensitive capabilities. Apna declares an Accessibility service for user-authorized screen reading, tapping and typing. It does not request SMS-reading or notification-listener access. This message does not establish an Android-version incompatibility or, by itself, a malware finding. It also does not establish that the app is safe.

For this personal development project, use Android's documented [USB development workflow](https://developer.android.com/studio/run/device) to attempt installation and obtain the actual package-installer result. Keep Play Protect and USB app verification enabled. ADB installation is not guaranteed to succeed and does not constitute Google approval.

## Windows steps

1. Start with **Pixel 8**. Connect only one phone. This APK requires Android 8.0+ and an OS supporting `arm64-v8a`; a phone model name alone does not verify its installed OS architecture.
2. Download the Windows ZIP from Google's [SDK Platform Tools](https://developer.android.com/tools/releases/platform-tools) page and extract it.
3. Download **Friday-debug** from the latest successful [Android build](https://github.com/maaznpc/voice-assistant-/actions/workflows/android.yml). Extract the artifact ZIP and copy `app-debug.apk` into the extracted `platform-tools` folder next to `adb.exe`. GitHub may require sign-in to download artifacts.
4. On Pixel: **Settings → About phone → Build number**, tap seven times and enter your PIN if requested. Then **Settings → System → Developer options → USB debugging**. Vivo menu names may differ; search Settings for Developer options.
5. Connect with a USB data cable. Unlock the phone and approve the USB debugging prompt for your own computer.
6. In File Explorer, open the `platform-tools` folder, type `powershell` in the address bar and press Enter. Run:

```powershell
.\adb.exe devices
```

The connected phone must show `device`, not `unauthorized` or `offline`. Then run:

```powershell
.\adb.exe install -r .\app-debug.apk
```

If the result is `Success`, open Friday on the phone and continue the [setup instructions](../README.md#install-and-configure). Microphone and phone-control access still require your approval on the phone; the install command does not grant them. Turn USB debugging off after testing.

## If installation fails

- **No device:** check the data cable and the phone's debugging prompt. Windows may need the [Google USB driver for Pixel](https://developer.android.com/studio/run/win-usb) or the manufacturer's driver for Vivo.
- **`unauthorized`:** unlock the phone and accept the debugging prompt for this computer.
- **`INSTALL_FAILED_VERIFICATION_FAILURE` or another Play Protect block:** record the exact result. Do not disable app verification. Review permissions, disclosure and behavior against [Google's guidance](https://developers.google.com/android/play-protect/warning-dev-guidance); a developer appeal is available if the classification is believed to be incorrect. Release signing alone does not remove this protection.
- **`INSTALL_FAILED_NO_MATCHING_ABIS`:** run `.\adb.exe shell getprop ro.product.cpu.abilist`. If `arm64-v8a` is absent, this artifact cannot run on that OS; it needs an appropriate native-library build.
- **`INSTALL_FAILED_UPDATE_INCOMPATIBLE`:** an installed copy has another signing key. Preserve its settings/data; do not uninstall automatically. Independent CI debug builds can use different debug keys.
- **Any other error:** share the complete `Failure [...]` text. Do not assume it has the same cause as the browser-install warning.

If you have only a phone, this guide cannot complete installation. A build without Accessibility would lose generic screen reading/tapping/typing and needs an explicit product decision; it is not equivalent to the full phone-control assistant.

## Verified artifact

For **run 41966859354 only**, the downloaded `app-debug.apk` has SHA-256:

```text
d466cef8ac1857bd483e076021e5e0c9f96f4278773ce182d277c061270582c2
```

Check it on Windows with `Get-FileHash .\app-debug.apk -Algorithm SHA256`. This is the APK hash, not the ZIP hash. Other builds have different hashes.

Android `apksigner` verified this artifact's v2 debug signature; `aapt` confirmed package `com.maaz.apna`, version `0.2.0-apna`, minimum API 26 and ARM64 binaries. These checks verify packaging/signature integrity, not Play Protect acceptance or successful operation on either phone. No physical phone is connected to the build workspace.
