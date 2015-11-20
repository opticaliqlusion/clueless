import urllib2, urllib, json, random

address = 'http://127.0.0.1:65500/%s'

def perform_webserver_query(path, values):
    data = json.dumps(values)
    url = address % (path,)
    #data = urllib.urlencode(values)
    req = urllib2.Request(url, data)
    resp = urllib2.urlopen(req).read()
    return json.loads(resp)

# create a new game
game_create_response = perform_webserver_query('join_game', { 'idGame': 0 })

print(game_create_response)
playerid = game_create_response['idPlayer']

# join that game as a different user
response = perform_webserver_query('join_game', { 'idGame': game_create_response['idGame'] })
print(response)

# start the game
start_game_response = perform_webserver_query('start_game', { 'idGame': response['idGame'], 'idPlayer': playerid })
print(start_game_response)

moves = perform_webserver_query('get_valid_moves', { 'idGame': response['idGame'], 'idPlayer': playerid })
print(moves)

move_player_response =  perform_webserver_query('move_player', { 'idGame': response['idGame'], 'idPlayer': playerid, 'idRoom': random.choice(moves)})
print(move_player_response)