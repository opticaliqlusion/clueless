#!flask/bin/python
from flask import Flask
import uuid

app = Flask(__name__)

@app.route('/Session', methods=['GET'])
def get_Session():
    return str(uuid.uuid4());

if __name__ == '__main__': #Port will transform to 5001, 5002 for test, prod
    app.run(host='0.0.0.0', debug=True, port=5000)
