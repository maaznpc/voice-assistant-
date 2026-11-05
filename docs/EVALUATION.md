# Project 33 evaluation

Record phone model, Android version, RAM, app build, wake phrase, speech/voice engines, provider/model and network. Keep tuning samples separate from test speakers/rooms. Do not reuse upstream results as measurements of Friday.

## End-to-end acceptance

1. Configure permissions, wake/STT downloads and a model; test its connection.
2. Leave the screen off for ten minutes, wake by voice, then unlock before controlling an app.
3. Complete three spoken turns without buttons, including a context-dependent follow-up.
4. Say “go to sleep”; verify idle state, cleared history and no task API requests. Wake again.
5. Test silence timeouts at 5, 15 and 60 seconds after replies.
6. Disable network mid-task; verify failure feedback, no false success and local wake operation.
7. Interrupt a reply, stop a task and turn off the service; verify late callbacks do not resume an old command.
8. Lock during a task and revoke microphone permission; verify blocked actions/failure feedback.

| Metric | Method |
|---|---|
| False-reject rate | Missed genuine wakes / genuine attempts; at least 5 speakers × 20 attempts, quiet/noisy rooms and several distances. |
| False activations | Unwanted activations / negative-audio hours; include TV, conversation and similar phrases. Report count and duration. |
| Response latency | User speech end to first audible reply; median and p95 over at least 30 turns, with model/network details. |
| Standby resources | Compare off, wake-only and active over equal durations. Use `adb shell dumpsys cpuinfo com.maaz.apna` and `adb shell dumpsys batterystats com.maaz.apna`. The wake lock trades battery for screen-off reliability. |
| Task success | Predetermine visible outcomes for app, timer, search, volume, message and navigation tasks. A draft is not a sent message. |
| Conversation quality | Several users rate clarity, naturalness, turn-taking and interruption, 1–5, with comments. |

CSV columns: `trial_id,device,android,phrase,room,distance_m,expected_wake,detected,false_activation,latency_ms,task,outcome,notes`.
Get consent for evaluation recordings, keep them out of Git, and delete on request. The app does not save raw microphone recordings by default.

## Multilingual device checks

Select Urdu, Roman Urdu, Hindi or Hinglish in Settings and restart Friday. Keep the local Hey Friday detector enabled. Wait for the prompt, then speak each request in Urdu or mixed English/Urdu. Repeat with the screen locked for ordinary conversation, then unlocked for app actions.

- Try “WhatsApp کھولو”, “battery kitni hai?”, “mujhe machine learning samjhao” and a follow-up without another wake phrase. Check the actual transcript and reply, rather than relying on success claims.
- Roman Urdu replies must display Latin letters and speak the same answer from the Urdu-script field. Check native Urdu pronunciation with several speakers.
- Try “سو جاؤ” and “خدا حافظ”; these must return to wake-only standby without an AI request.
- Check unavailable language/network errors, silence timeout, stop during recognition, and missing Urdu TTS data. Missing voice must leave a readable reply and show setup guidance.
- Android 14+ language switching is a request to the speech service, not a guarantee. Test mixed English names and numbers separately on Pixel 8 and Vivo Y20.
- Confirm no active recognition runs in standby. System recognition may send activated audio to its provider; ordinary English offline mode remains available.
