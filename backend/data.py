# Imports
import time
import random
from functools import wraps
import requests, json, time
import threading
from lxml import html
from algoliasearch import algoliasearch
from github import Github
from bs4 import BeautifulSoup
import os
from flask import Flask
from flask import request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

def create_query(keywords):
    result = ""
    for keyword in keywords:
        result = result + "+" + keyword
    return result[1:]

def get_devpost(query, container):
    # get first page of query results... max of 4 documents returned
    page = requests.get('https://devpost.com/software/search?query=' + query + '&page=0').json()["software"]
    counter = 0

    if len(page) != 0:
        for project in page:
            if counter == 5: break
            product_data = {}
            product_data['title'] = project['name']
            product_data['url'] = project['url']
            product_data['tagline'] = project['tagline']
            product_data['image_url'] = project['photo']
            product_data['tags'] = project['tags']
            product_data['origin'] = 'Devpost'
            print(product_data)
            container.append(product_data)
            counter += 1
        print ('DEVPOST ELAPSED TIME: ', time.time())
    else: # no data found
        return None

def get_producthunt(query, container):
    client = algoliasearch.Client("0H4SMABBSG", '9670d2d619b9d07859448d7628eea5f3')
    index = client.init_index('Post_production')
    posts = index.search(query, {"page": 0})['hits']
    counter = 0

    if len(posts) != 0:
        for project in posts:
            if counter == 5: break
            product_data = {}
            product_data['title'] = project['name']
            product_data['tagline'] = project['tagline']
            product_data['url'] = 'https://www.producthunt.com/' + project['url']
            product_data['image_url'] = project['thumbnail']['image_url']
            product_data['origin'] = 'ProductHunt'

            topics_array = []
            for topic in project['topics']:
                topics_array.append(topic['name'])
        
            product_data['tags'] = topics_array
            container.append(product_data)
        print('PRODUCT HUNT:', time.time())
    else:
        return None


def get_github (search_query, container):
    g = Github("testhacks", "random123")
    counter = 0
    for repo in g.search_repositories(search_query, sort = "stars", order= "desc").get_page(0):
        if counter == 5: break
        _ = {}
        _ ['title'] = repo.name
        _ ['tagline'] = repo.description
        _ ['url'] = repo.html_url
        _ ['tags'] = [repo.language]
        _ ['image_url'] = None
        _ ['origin'] = 'Github'
        container.append(_)
        counter += 1
    print('GITHUB TIME: ', time.time())


def get_googleplay(search_query, container):
    search_url = "https://play.google.com/store/search?q="+ search_query +"&c=apps&hl=en"
    page = requests.get(search_url)
    data = page.text

    soup = BeautifulSoup(data, "lxml")
    counter = 0
    for link in soup.find_all('div', attrs = {"class" : "details"}):
        if (counter == 5): break
        results = {}
        #for _ in link.find_all ('a'):
        href = link.find ("a").get("href")
        url = "https://play.google.com" + href
        try:
            title = link.find ("a", {"class" : "title"}).get_text(strip = True)
            description = link.find ("div", {"class" : "description"}).get_text(strip = True)
            results ['title'] = title
            results ['tagline'] = description
            results ['url'] = url
            results ['origin'] = 'Google Play'
            results ['tags'] = None
            results ['image_url'] = None
            container.append(results)
            counter += 1
        except:
            pass
    print('GOOGLE PLAY: ', time.time())

def delay(delay=0.):
    def wrap(f):
        @wraps(f)
        def delayed(*args, **kwargs):
            timer = threading.Timer(delay, f, args=args, kwargs=kwargs)
            timer.start()
        return delayed
    return wrap

def getScore(title, desc, container):
    print("Sending payload: ")
    payload = { "title": title, "description": desc, "candidates": container }
    print(payload)
    try:
      resp = requests.post('http://52.233.33.65:5000/score', data = json.dumps(payload))
      return json.dumps(resp.json())
    except Exception:
      for record in container:
        container['ideascore'] = random.random() * 100
        container['namescore'] = random.random() * 100
      return { "idea": container, "name": container}


text_analytics_base_url = "https://eastus.api.cognitive.microsoft.com/text/analytics/v2.0/keyPhrases"

def processDescription(desc):
  headers = {
      # Request headers
      'Content-Type': 'application/json',
      'Ocp-Apim-Subscription-Key': 'adf14f2ab244459aac058cea4c3a5108',
  }
  payload = {
    "documents": [
      {
        "language": "en",
        "id": "1",
        "text": desc
      }
    ]
  }
  r = requests.post(text_analytics_base_url, headers=headers, data=json.dumps(payload))
  return ' '.join((r.json()["documents"][0])['keyPhrases'])
  

@app.route('/score', methods=['POST'])
def score(): 
    container = [];
    content = request.get_json()
    print(request.headers['Content-Type'])
    print("Received data: ", content)
    title = content['name'][0:200]
    title = title.replace("'", "")
    title.encode('ascii', 'replace').decode()
    description = content['description'][0:200]
    description = description.replace("'", "")
    description.encode('ascii', 'replace').decode()
    if len(description) > 75:
      description = processDescription(description)
      print("Adjusted description: ", description)
    tokens = description.split(' ')
    sites = content['sites']
    t1 = threading.Thread(target=get_devpost, args=(create_query(tokens), container))
    t2 = threading.Thread(target=get_github, args=(description, container))
    t3 = threading.Thread(target=get_googleplay, args=(description, container))
    t4 = threading.Thread(target=get_producthunt, args=(description, container))
    threads = { "devpost": t1, "github": t2, "producthunt": t3, "googleplay": t4}
    for key, val in sites.items():
      if val == 't' and key in threads: threads[key].start()
    time.sleep(1.05) 
    msg = getScore(title, description, container)
    return msg

if __name__ == '__main__':
  app.run()
