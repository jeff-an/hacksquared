from flask import Flask
from flask import request, jsonify
from Scorer import computeScore

app = Flask(__name__)

@app.route('/score', methods=['POST'])
def score():
  content = request.get_json(force = True)
  print("Received data: ", content)

  usertitle = content['title']
  comparingTitle = len(usertitle) > 4
  userdescription = content['description']
  seenTagLines = {}
  for record in content['candidates']:
    if record['tagline'] in seenTagLines:
      # Seen this exact record already, sort to the end
      record['ideascore'] = 0
      record['namescore'] = 0
      continue
    if comparingTitle:
      # Compute name similarity and description similarity
      namescore = computeScore(usertitle, record['title']) if record['title'] else 0
      ideascore = computeScore(userdescription, record['tagline']) if record['tagline'] else 0
      record['namescore'] = namescore
      record['ideascore'] = ideascore
    else:
      # Just compute description similarity
      ideascore = computeScore(userdescription, record['tagline']) if record['tagline'] else 0
      record['ideascore'] = ideascore
    seenTagLines[record['tagline']] = 1
  results = {}
  content['candidates'].sort(key=lambda x: x['ideascore'], reverse=True)
  results['idea'] = content['candidates'][:10]
  if comparingTitle:
    content['candidates'].sort(key=lambda x: x['namescore'], reverse=True)
    results['name'] = content['candidates'][:10]
  return jsonify(results)

if __name__ == '__main__':
  app.run()
