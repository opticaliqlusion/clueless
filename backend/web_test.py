import urllib2, urllib, json, random

address = 'http://127.0.0.1:65500/%s'

def perform_webserver_query(path, values=None):
    
    url = address % (path,)
    
    if values:
        data = json.dumps(values)
        req = urllib2.Request(url, data)
    else:
        req = urllib2.Request(url)
    resp = urllib2.urlopen(req).read()
    return json.loads(resp)

rooms = perform_webserver_query('get_room_names', None)
print(rooms)
import pdb; pdb.set_trace()
# create a new game
game_create_response = perform_webserver_query('join_game', { 'idGame': 0, 'idCharacter' : 0 })

print(game_create_response)
playerid = game_create_response['data']['idPlayer']

# join that game as a different user
response = perform_webserver_query('join_game', { 'idGame': game_create_response['data']['idGame'], 'idCharacter' : 1 })
print(response)

# start the game
start_game_response = perform_webserver_query('start_game', { 'idGame': response['data']['idGame'], 'idPlayer': playerid })
print(start_game_response)

moves = perform_webserver_query('get_valid_moves', { 'idGame': response['data']['idGame'], 'idPlayer': playerid })
print(moves)

move_player_response =  perform_webserver_query('move_player', { 'idGame': response['data']['idGame'], 'idPlayer': playerid, 'idRoom': random.choice(moves)})
print(move_player_response)