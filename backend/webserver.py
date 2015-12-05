from __future__ import print_function
import time
import BaseHTTPServer
import cgi
import urllib
import urlparse
import sys

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
        elif self.path == '/get_room_names':
            response = logic_engine.get_room_names()
        elif self.path == '/get_all_cards':
            response = logic_engine.get_all_cards()
        elif self.path.startswith('/get_board_state'):
            # game id
            response = logic_engine.get_board_state(int(query['idGame'][0]), int(query['idPlayer'][0]))
        elif self.path.startswith('/get_valid_moves'):
            response = logic_engine.get_valid_moves(int(query['idGame'][0]), int(query['idPlayer'][0]))

        # for testing only, unless you want the game to be really easy, you cheating f%&@
        elif self.path.startswith('/get_solution'):
            response = logic_engine.get_solution(int(query['idGame'][0]))

        else:
            print("query not recognized:%s" % (path,))

        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()

        self.wfile.write(self.getSuccessData(response))

    def do_POST(self):
        length = int(self.headers["Content-Length"])

        data = self.rfile.read(length)
        jsondata = json.loads(data)

        print(self.path, file=sys.stderr)
        print(data, file=sys.stderr)

        #if self.path == '/create_game':
        #    response = logic_engine.create_game()
        #import pdb; pdb.set_trace()

        if self.path == '/join_game':
            response = logic_engine.add_player_to_game(jsondata['idGame'], None, jsondata['idCharacter'])

        elif self.path == '/start_game':
            response = logic_engine.start_game(jsondata['idGame'], jsondata['idPlayer'])

        elif self.path == '/move_player':
            response = logic_engine.move_player(jsondata['idGame'], jsondata['idPlayer'], jsondata['idRoom'])

        elif self.path == '/make_suggestion':
            response = logic_engine.make_suggestion(jsondata['idGame'], jsondata['idPlayer'], jsondata['cards'])

        elif self.path == '/make_accusation':
            response = logic_engine.make_accusation(jsondata['idGame'], jsondata['idPlayer'], jsondata['cards'])

        elif self.path == '/disprove_suggestion':
            response = logic_engine.disprove_suggestion(jsondata['idGame'], jsondata['idPlayer'], jsondata['idCard'] if 'idCard' in jsondata.keys() else None)

        elif self.path == '/end_player_turn':
            response = logic_engine.end_player_turn(jsondata['idGame'], jsondata['idPlayer'])

        else:
            print("query not recognized")
            import pdb; pdb.set_trace()

        response = self.getSuccessData(response)
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