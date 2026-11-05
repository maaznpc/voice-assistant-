#!/usr/bin/env python3
"""Generate keywords from the exact KWS bpe.model. Requires pip install sentencepiece."""
import argparse

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument("bpe_model")
parser.add_argument("phrase", help='For example "hey apna"')
args = parser.parse_args()
import sentencepiece as spm

model = spm.SentencePieceProcessor(model_file=args.bpe_model)
phrase = args.phrase.strip().upper()
if not phrase or not all(c.isascii() and (c.isalpha() or c == " ") for c in phrase):
    parser.error("Use an English alphabetic phrase for this model.")
tokens = model.encode(phrase, out_type=str)
if any(t in {"<unk>", "<s>", "</s>"} for t in tokens):
    parser.error("The model could not encode this phrase.")
print(" ".join(tokens) + " :2.0 #0.2 @" + phrase.replace(" ", "_"))
