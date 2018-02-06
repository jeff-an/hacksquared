from math import log
from collections import Counter
from nltk.stem import PorterStemmer
from nltk.tokenize import word_tokenize

from gensim.models.keyedvectors import KeyedVectors
from DocSim import DocSim
import os

ps = PorterStemmer()
print(os.getcwd())
googlenews_model_path = './data/GoogleNews-vectors-negative300.bin'
stopwords_path = "./data/stopwords_en.txt"

model = KeyedVectors.load_word2vec_format(googlenews_model_path, binary=True)
with open(stopwords_path, 'r') as fh:
    stopwords = fh.read().split(",")
ds = DocSim(model,stopwords=stopwords)

''' Receive the user description as doc1 '''
def computeScore(doc1, doc2):
  words1 = word_tokenize(doc1)
  print("DOC2: ", doc2)
  words2 = word_tokenize(doc2)
  stems1 = [ps.stem(word) for word in words1]
  stems2 = [ps.stem(word) for word in words2]
  stems2freq = Counter(stems2)
  rawSim = ds.calculate_similarity(doc1, doc2)
  rawScore = 0
  if rawSim: rawScore = rawSim[0]['score']
  print("raw: ", rawScore)

  # Reduce error by logarithmic term frequency
  freq = 0
  for stem in stems1:
    if len(stem) > 4 and stem in stems2freq:
      freq += stems2freq[stem]
  freq = min(0.8, freq / max(1, len(stems1) - 1)) #account for sample variation
  logError = (1 - rawScore) * -log(freq + 0.1, 10)
  finalscore = min(1, 1 - logError) # Account for floating point error
  return finalscore
