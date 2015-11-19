import time
import BaseHTTPServer
import cgi
import urllib
import urlparse

import logic_engine
import json
import web_response

# Example python custom handler code courtesy stackoverflow
# http://stackoverflow.com/questions/4233218/python-basehttprequesthandler-post-variables
# https://wiki.python.org/moin/BaseHttpServer

HOST_NAME = '0.0.0.0'
PORT_NUMBER = 65500

class MyHandler(BaseHTTPServer.BaseHTTPRequestHandler):
    def getSuccessData(self, dataObject):
        jsonData = web_response.WebResponse(dataObject)
        return json.dumps(jsonData.__dict__)
    def do_HEAD(self):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()

    def do_GET(self):
        """Respond to a GET request."""

        url   =  urlparse.urlparse(self.path)
        path  = url.path
        query = urlparse.parse_qs(url.query)

        print(query)

        #@todo
        if path == '/get_pending_games':
            # no variables needed
            gamedata = logic_engine.get_pending_games()
            response = dict()
            response['games'] = gamedata
        elif self.path == '/get_characters':
            response = logic_engine.get_characters()

        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()

        test = self.getSuccessData(response)
        self.wfile.write(test)

    def do_POST(self):
        length = int(self.headers["Content-Length"])

        query = urlparse.parse_qs(self.rfile.read(length))
        print(self.path)
        print(query)

        #if self.path == '/create_game':
        #    response = logic_engine.create_game()

        if self.path == '/join_game':
            response = logic_engine.add_player_to_game(query['idGame'])

        elif self.path == '/get_valid_moves':
            # game id, player id
            response = logic_engine.get_valid_moves(query['idGame'], query['idPlayer'])

        elif self.path == '/get_board_state':
            # game id
            response = logic_engine.get_board_state(query['idGame'], query['idPlayer'])

        if self.path == '/start_game':
            response = logic_engine.start_game(query['idGame'], query['idPlayer'])

        elif self.path == '/move_player':
            response = logic_engine.move_player(query['idGame'], query['idPlayer'], query['idRoom'])

        elif self.path == '/make_suggestion':
            response = logic_engine.make_suggestion(query['idGame'], query['idPlayer'], query['cards'])

        elif self.path == '/make_accusation':
            response = logic_engine.make_accusation(query['idGame'], query['idPlayer'], query['cards'])

        elif self.path == '/disprove_suggestion':
            response = logic_engine.disprove_suggestion(query['idGame'], query['idPlayer'], query['idCard'])

        response = json.dumps(response)
        self.send_response(200)
        self.send_header("Content-Length", str(len(response)))
        self.end_headers()

        self.wfile.write(response)

if __name__ == '__main__':
    server_class = BaseHTTPServer.HTTPServer
    httpd = server_class((HOST_NAME, PORT_NUMBER), MyHandler)
    print(time.asctime(), "Server Starts - %s:%s" % (HOST_NAME, PORT_NUMBER))
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()
    print(time.asctime(), "Server Stops - %s:%s" % (HOST_NAME, PORT_NUMBER))