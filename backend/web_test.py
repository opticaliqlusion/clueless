import urllib2, urllib, json, random

address = 'http://127.0.0.1:65500/%s'

def perform_webserver_query(path, postvalues=None, getvalues=None):

    url = address % (path,)

    if postvalues:
        data = json.dumps(postvalues)
        req = urllib2.Request(url, data)
    elif getvalues:
        req = '%s?%s' % (url, urllib.urlencode(getvalues))
    else:
        req = urllib2.Request(url)
    resp = urllib2.urlopen(req).read()
    return json.loads(resp)

rooms = perform_webserver_query('get_room_names')
print(rooms)

# create a new game
game_create_response = perform_webserver_query('join_game', postvalues={ 'idGame': 0, 'idCharacter' : 0 })
print(game_create_response)
playerid = game_create_response['data']['idPlayer']

# test pending games
pending_games = perform_webserver_query('get_pending_games')
print(pending_games)

# test get characters
characters_reply = perform_webserver_query('get_characters')
print(characters_reply)

# test get all cards
cards_reply = perform_webserver_query('get_all_cards')
cards = cards_reply['data']
print(cards_reply)

# join that game as a different user
response = perform_webserver_query('join_game', postvalues={ 'idGame': game_create_response['data']['idGame'], 'idCharacter' : 1 })
print(response)
playerid2 = response['data']['idPlayer']

# start the game
start_game_response = perform_webserver_query('start_game', postvalues={ 'idGame': response['data']['idGame'], 'idPlayer': playerid })
print(start_game_response)

# because we're cheating f$#@s, we're gonna cheat
cheating_response = perform_webserver_query('get_solution', getvalues={'idGame': response['data']['idGame']})
import pdb; pdb.set_trace()

p1_state = perform_webserver_query('get_board_state', getvalues={ 'idGame': game_create_response['data']['idGame'], 'idPlayer' : playerid })
p1_cards = p1_state['data']['cardIds']

p2_state = perform_webserver_query('get_board_state', getvalues={ 'idGame': game_create_response['data']['idGame'], 'idPlayer' : playerid2 })
p2_cards = p2_state['data']['cardIds']

ids = [ playerid, playerid2 ]

player_cards = { playerid:p1_cards, playerid2:p2_cards }

# move 10 times and end the turn
for i in range(10):

    id, other_id = ids
    
    moves = perform_webserver_query('get_valid_moves', getvalues={ 'idGame': response['data']['idGame'], 'idPlayer': id })['data']
    print(moves)

    move_player_response =  perform_webserver_query('move_player', postvalues={ 'idGame': response['data']['idGame'], 'idPlayer': id, 'idRoom': random.choice(moves)})
    print(move_player_response)

    # find a guess
    my_room = [i['id'] for i in rooms['data'] if i['id'] == move_player_response['data']['playerGameIdMap'][str(id)] ][0]
    my_room_obj = [i for i in rooms['data'] if i['id'] == my_room][0]
    
    if my_room_obj['type'] == 0:
        weapon_cards = [i['id'] for i in cards if i['type'] == 0]
        character_cards = [i['id'] for i in cards if i['type'] == 2]
        
        # make a suggestion as the player
        suggestion = {'weapon':random.choice(weapon_cards),'character':random.choice(character_cards)}
        make_suggestion_response =  perform_webserver_query('make_suggestion', { 'idGame': response['data']['idGame'], 'idPlayer': id, 'cards' : suggestion})
        print(make_suggestion_response)
   
        # disprove, if we can, as the other player
        try:
            disproving_card = random.choice([i['id'] for i in player_cards[other_id] if i['id'] in make_suggestion_response['data']['currentSuggestion']])
            render_disproval_response =  perform_webserver_query('disprove_suggestion', { 'idGame': response['data']['idGame'], 'idPlayer': other_id, 'idCard' : disproving_card})
        except Exception, e:
            import pdb; pdb.set_trace()
            pass
    
    end_turn_response =  perform_webserver_query('end_player_turn', postvalues={ 'idGame': response['data']['idGame'], 'idPlayer': id})
    print(end_turn_response)
    
    ids.reverse()
