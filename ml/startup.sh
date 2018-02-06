export FLASK_APP=SimScorer.py
cd ~/SimScorer
nohup python3 -m flask run --host=0.0.0.0 > output.txt 2>&1 </dev/null &
