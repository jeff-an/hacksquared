export FLASK_APP=data.py
nohup python3 -m flask run --host=0.0.0.0 1>output.txt 2>&1 &
