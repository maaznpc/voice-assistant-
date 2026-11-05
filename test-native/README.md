Optional files for the real-engine tests (`RealEngineTest`). Not committed.

    curl -LO https://github.com/k2-fsa/sherpa-onnx/releases/download/v1.13.8/sherpa-onnx-v1.13.8-linux-x64-jni.tar.bz2 && tar xjf sherpa-onnx-v1.13.8-linux-x64-jni.tar.bz2
    mkdir models && cd models
    curl -L -o kokoro.tar.bz2 https://github.com/k2-fsa/sherpa-onnx/releases/download/tts-models/kokoro-int8-en-v0_19.tar.bz2
    curl -L -o kws.tar.bz2 https://github.com/k2-fsa/sherpa-onnx/releases/download/kws-models/sherpa-onnx-kws-zipformer-gigaspeech-3.3M-2024-01-01.tar.bz2
    curl -L -o asr.tar.bz2 https://github.com/k2-fsa/sherpa-onnx/releases/download/asr-models/sherpa-onnx-streaming-zipformer-en-kroko-2025-08-06.tar.bz2

## Current automatic mode

This is a legacy, optional offline-English engine harness. Friday 0.3.1 uses automatic cloud/system command transcription and does not run that offline-English command path by default. This harness is excluded from the normal verification suite and is not evidence of automatic Urdu/Hindi recognition. Use the device evaluation checklist for the current audio flow.
