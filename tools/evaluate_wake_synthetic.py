#!/usr/bin/env python3
from pathlib import Path
import subprocess, time, json
import numpy as np
import soundfile as sf
import sherpa_onnx
import argparse
parser=argparse.ArgumentParser(description='Synthetic-only wake regression probe. Requires sherpa-onnx 1.13.8, numpy, soundfile and ffmpeg with libflite. Not a physical-speaker accuracy evaluation.')
parser.add_argument('--model-dir',type=Path,required=True)
parser.add_argument('--output-dir',type=Path,required=True)
args=parser.parse_args()
root=args.model_dir
out=args.output_dir
out.mkdir(parents=True,exist_ok=True)
cases=[]
for voice in ['slt','rms','awb','kal','kal16']:
 for label,text,positive in [('wake','Hey Friday',True),('day','Today is Friday',False),('near','Hey Freddy',False),('command','Open WhatsApp and send a message',False)]:
  f=out/f'{voice}-{label}.wav'
  subprocess.run(['ffmpeg','-hide_banner','-loglevel','error','-f','lavfi','-i',f"flite=text='{text}':voice={voice}",'-ar','16000',str(f),'-y'],check=True)
  a,rate=sf.read(f,dtype='float32')
  cases.append((f'{voice}-{label}',a,positive))
  if positive:
   cases.append((f'{voice}-{label}-quiet',a*.2,True))
config=dict(tokens=str(root/'tokens.txt'),encoder=str(root/'encoder-epoch-12-avg-2-chunk-16-left-64.int8.onnx'),decoder=str(root/'decoder-epoch-12-avg-2-chunk-16-left-64.int8.onnx'),joiner=str(root/'joiner-epoch-12-avg-2-chunk-16-left-64.int8.onnx'),num_threads=1,keywords_score=2,keywords_threshold=.1)
result={}
for score,threshold in [(2,.2),(2,.1),(3,.1)]:
 k=out/'eval-keywords.txt';k.write_text(f'▁HE Y ▁F RI DAY :{score} #{threshold} @FRIDAY\n')
 kws=sherpa_onnx.KeywordSpotter(**config,keywords_file=str(k))
 rows=[]
 for name,a,positive in cases:
  a=np.concatenate([np.zeros(4000,dtype='float32'),a,np.zeros(16000,dtype='float32')]);stream=kws.create_stream();detected=False;start=time.monotonic()
  for i in range(0,len(a),1600):
   stream.accept_waveform(16000,a[i:i+1600])
   while kws.is_ready(stream):
    kws.decode_stream(stream)
    if kws.get_result(stream): detected=True;kws.reset_stream(stream)
  rows.append(dict(case=name,positive=positive,detected=detected,seconds=round(time.monotonic()-start,3)))
 result[f'{score}/{threshold}']=rows
 summary=dict(positive_hits=sum(x['positive'] and x['detected'] for x in rows),positives=sum(x['positive'] for x in rows),negative_hits=sum(not x['positive'] and x['detected'] for x in rows),negatives=sum(not x['positive'] for x in rows),misses=[x['case'] for x in rows if x['positive'] and not x['detected']],mean_seconds=round(sum(x['seconds'] for x in rows)/len(rows),3))
 print(score,threshold,json.dumps(summary),flush=True)
(out/'results.json').write_text(json.dumps(result,indent=2))
