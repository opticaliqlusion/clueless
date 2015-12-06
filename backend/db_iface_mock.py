import os, sys, random

def enum(*sequential, **named):
    enums = dict(zip(sequential, range(len(sequential))), **named)
    return type('Enum', (), enums)

RoomTypes = enum('ROOM', 'HALL')
GameStates = enum('PENDING', 'PLAYING', 'FINISHED')
TurnState = enum('SELECTING_MOVE', 'MAKING_SUGGESTION', 'SOLICITING_DISPROVALS', 'WAITING_FOR_END')
CardTypes = enum('WEAPON', 'ROOM', 'CHARACTER')

log_message_dict = {
        'start_game':'playerId=%s started the game',
        'move_player':'playerId=%s moved from %s to %s',
        'make_suggestion':'playerId=%s made suggestion %s',
        'render_disproval':'playerId=%s disproved suggestion by revealing %s',
        'make_accusation':'playerId=%s made accusation %s, resulting in %s',
        'end_player_turn':'playerId=%s ended their turn',
        'generic':'%s',
    }

class GameStateViolation(Exception):
    pass

class NoSuchObjectException(Exception):
    pass

class PersistableBase(object):
    static_list = []

    def __init__(self):
        try:
            self.id = max([i.id for i in self.static_list]) + 1
        except:
            self.id = 1

        self.static_list.append(self)

class Character(PersistableBase):
    static_list = []

    @staticmethod
    def get_by_id(id):
        return [i for i in Player.static_list if i.id == id][0]

    def __repr__(self):
        return '<Character(id=%d)>' % (self.id,)

    def __init__(self, name):
        self.name = name
        super(Character, self).__init__()

class Player(PersistableBase):
    static_list = []

    @staticmethod
    def get_by_id(id):
        return [i for i in Player.static_list if i.id == id][0]

    def __repr__(self):
        return '<Player(id=%d)>' % (self.id,)

    def __init__(self, idCharacter):
        self.idCharacter = idCharacter
        self.cards = []
        self.room = None
        super(Player, self).__init__()

class Game(PersistableBase):
    static_list = []

    @staticmethod
    def get_by_id(id):
        return [i for i in Game.static_list if i.id == id][0]

    def __init__(self):
        self.meta_state = GameStates.PENDING
        self.players = []
        self.character_map = []
        self.player_current_turn_index = 0
        self.player_current_disprover_index = 0
        self.turn_state = None
        self.log = []
        self.current_suggestion = []
        self.solution = {'weapon':None, 'character':None, 'room':None}
        self.suggesting_player = None
        self.winner = None
        self.losers = []

        super(Game, self).__init__()

    def __repr__(self):
        return '<Game(id=%d)>' % (self.id,)

    def serialize(self, idPlayer=None):
        playerCards = None
        if idPlayer:
            player = [i for i in self.players if i.id == idPlayer][0]
            playerCards = [{'id':i.id, 'type':i.type} for i in player.cards]

        return {
            'idPlayer':idPlayer,
            # TODO Hack if room or char map is null
            'playerGameIdMap': {i.id: (None if not i.room else i.room.id) for i in self.players},
            # TODO I don't think we need this?
            # 'characterMap': {self.players[i].id: self.character_map[i] for i in range(len(self.character_map))},
            'idCurrentTurn': self.players[self.player_current_turn_index].id,
            'idCurrentDisprover': self.players[self.player_current_disprover_index].id,
            'gameState': self.meta_state,
            'turnState': self.turn_state,
            'cardIds': playerCards,
            'currentSuggestion': [i.id for i in self.current_suggestion],
            'logs': self.log,
            'lastLogId': 0,
            'idGame': self.id,
            'idPlayer': idPlayer,
            'winner':self.winner.id if self.winner else None,
            'losers':[i.id for i in self.losers],
            }

class Room(PersistableBase):
    static_list = []

    @staticmethod
    def get_by_id(id):
        return [i for i in Room.static_list if i.id == id][0]

    @staticmethod
    def get_by_name(name):
        return [i for i in Room.static_list if i.name == name][0]

    def __init__(self, name, type):
        self.name = name
        self.type = type
        self.adjacent_rooms = []
        self.card = None
        super(Room, self).__init__()

    def __repr__(self):
        return '<Room(id=%d, name=%s)>' % (self.id, self.name)

class Card(PersistableBase):
    static_list = []

    @staticmethod
    def get_by_id(id):
        return [i for i in Card.static_list if i.id == id][0]

    @staticmethod
    def get_by_name(name):
        return [i for i in Room.static_list if i.name == name][0]

    def __init__(self, name, type):
        self.name = name
        self.type = type
        super(Card, self).__init__()

    def __repr__(self):
        return '<Card(id=%d, name=%s)>' % (self.id, self.name)

card_names_and_types = [
    ('Rope', CardTypes.WEAPON),
    ('Lead Pipe', CardTypes.WEAPON),
    ('Knife', CardTypes.WEAPON),
    ('Wrench', CardTypes.WEAPON),
    ('Candlestick', CardTypes.WEAPON),
    ('Revolver', CardTypes.WEAPON),
    ('Colonel Mustard', CardTypes.CHARACTER),
    ('Miss Scarlet', CardTypes.CHARACTER),
    ('Professor Plum', CardTypes.CHARACTER),
    ('Mr. Green', CardTypes.CHARACTER),
    ('Mrs. White', CardTypes.CHARACTER),
    ('Mrs. Peacock', CardTypes.CHARACTER),
]

# create character and weapon cards
for name, type in card_names_and_types:
    card = Card(name, type)

rooms_adjacent_with_halls = [
    ('Study', 'Hall'),
    ('Hall', 'Lounge'),
    ('Lounge', 'Dining Room'),
    ('Dining Room', 'Kitchen'),
    ('Kitchen', 'Ballroom'),
    ('Ballroom', 'Conservatory'),
    ('Conservatory', 'Library'),
    ('Library', 'Study'),
    ('Hall', 'Billiard Room'),
    ('Billiard Room', 'Ballroom'),
    ('Library', 'Billiard Room'),
    ('Billiard Room', 'Dining Room'),
]

rooms_adjacent = [
    ('Study', 'Kitchen'),
    ('Conservatory', 'Lounge'),
]

# create all the rooms
for i in rooms_adjacent_with_halls:

    try:
        room_a = Room.get_by_name(i[0])
    except IndexError:
        room_a = Room(i[0], RoomTypes.ROOM)

    try:
        room_b = Room.get_by_name(i[1])
    except IndexError:
        room_b = Room(i[1], RoomTypes.ROOM)

    hall = Room('Hall-%s-%s' % (room_a.name, room_b.name), RoomTypes.HALL)

    room_a.adjacent_rooms.append(hall)
    room_b.adjacent_rooms.append(hall)
    hall.adjacent_rooms.append(room_a)
    hall.adjacent_rooms.append(room_b)

# create secret passages
for i in rooms_adjacent:
    room_a = Room.get_by_name(i[0])
    room_b = Room.get_by_name(i[1])

    room_a.adjacent_rooms.append(room_b)
    room_b.adjacent_rooms.append(room_a)

# create room cards
for room in [i for i in Room.static_list if i.type == RoomTypes.ROOM]:
    room.card = Card(room.name, CardTypes.ROOM)

# create the characters
char_name_list = ['Char 1', 'Char 2', 'Char 3', 'Char 4', 'Char 5', 'Char 6']
for i in range(len(char_name_list)):
    character = Character(char_name_list[i])

# static stuff
def get_characters():
    return [ {'id':i.id, 'name':i.name} for i in Character.static_list ]

def get_room_names():
    return [ {'id':i.id, 'name':i.name, 'type':i.type} for i in Room.static_list ]

def get_all_cards():
    return [ {'id':i.id, 'name':i.name, 'type':i.type} for i in Card.static_list ]

def get_pending_games():
    return { i.id : [ j.idCharacter for j in i.players ] for i in Game.static_list if i.meta_state == GameStates.PENDING }

def add_player_to_game(idGame, idPlayer, idCharacter):
    game = None

    # find or create the game
    if not idGame:
        game = Game()
    else:
        game = Game.get_by_id(idGame)

    # make sure there's room
    if len(game.players) >= 6:
        return

    if not idPlayer:
        player = Player(idCharacter)
    else:
        player = Player.get_by_id(idPlayer)

    game.players.append(player)

    return {'idGame': game.id, 'idPlayer': player.id, 'idCharacter': player.idCharacter}

def start_game(idGame, idPlayer):
    # first, change the meta_state of the game
    game = Game.get_by_id(idGame)
    game.meta_state = GameStates.PLAYING

    # establish who is going to go first
    game.player_current_turn_index = 0

    # shuffle and distribute the cards
    card_list_rooms = [i for i in Card.static_list if i.type == CardTypes.ROOM]
    card_list_weapons = [i for i in Card.static_list if i.type == CardTypes.WEAPON]
    card_list_characters = [i for i in Card.static_list if i.type == CardTypes.CHARACTER]

    assert (len(card_list_rooms) == 9)
    assert (len(card_list_weapons) == 6)
    assert (len(card_list_characters) == 6)

    # create the solution. dont tell anyone
    game.solution['room'] = card_list_rooms.pop()
    game.solution['weapon'] = card_list_weapons.pop()
    game.solution['character'] = card_list_characters.pop()

    i = 0
    while card_list_rooms and card_list_weapons and card_list_characters:

        if card_list_rooms:
            game.players[i].cards.append(card_list_rooms.pop())

        if card_list_weapons:
            game.players[i].cards.append(card_list_weapons.pop())

        if card_list_characters:
            game.players[i].cards.append(card_list_characters.pop())

        i = (i + 1) % len(game.players)
    # TODO Is this a requirement? Or are positions predetermined?
    # generate players' starting positions, randomly
    card_list_rooms = [Room.get_by_name(i.name) for i in Card.static_list if i.type == CardTypes.ROOM]
    random.shuffle(card_list_rooms)

    for player in game.players:
        player.room = card_list_rooms.pop()

    game.character_map = range(0, len([i for i in Card.static_list if i.type == CardTypes.CHARACTER]))
    random.shuffle(game.character_map)

    # finally, tell the game its time to start playing
    game.turn_state = TurnState.SELECTING_MOVE
    state = game.serialize(idPlayer=idPlayer)

    # push the log
    game.log.append(log_message_dict['start_game'] % (idPlayer,))
    return state


def get_valid_moves(idGame, idPlayer):
    game = Game.get_by_id(idGame)
    player = Player.get_by_id(idPlayer)

    if not player in game.players:
        raise NoSuchObjectException('No player found for idGame=%d and idPlayer=%d' % (idGame, idPlayer))

    return [i.id for i in player.room.adjacent_rooms]

def get_board_state(idGame, idPlayer):
    game = Game.get_by_id(idGame)
    player = Player.get_by_id(idPlayer)

    if not player in game.players:
        raise NoSuchObjectException('No player found for idGame=%d and idPlayer=%d' % (idGame, idPlayer))

    return game.serialize(idPlayer=idPlayer)

def move_player(idGame, idPlayer, idRoom):
    game = Game.get_by_id(idGame)
    player = Player.get_by_id(idPlayer)

    if not player in game.players:
        raise NoSuchObjectException('No player found for idGame=%d and idPlayer=%d' % (idGame, idPlayer))

    if not game.turn_state == TurnState.SELECTING_MOVE:
        raise GameStateViolation('idGame=%d not in state SELECTING_MOVE' % (idGame,))

    if not game.players[game.player_current_turn_index].id == idPlayer:
        raise GameStateViolation(
            'it is idPlayer=%d turn, NOT idPlayer=%d' % (game.players[game.player_current_turn_index].id, idPlayer))

    if not idRoom in [i.id for i in player.room.adjacent_rooms]:
        raise GameStateViolation('idRoom=%d and idRoom=%d not adjacent' % (current_room.id, idRoom))

    oldRoom = player.room
    player.room = Room.get_by_id(idRoom)

    # TODO change this to TurnState.MAKING_SUGGESTION, this is just for testing
    game.turn_state = TurnState.MAKING_SUGGESTION
    state = game.serialize(idPlayer=idPlayer)
    game.log.append(log_message_dict['move_player'] % (idPlayer, oldRoom.id, player.room.id))
    return state

def make_suggestion(idGame, idPlayer, cards):
    game, player = Game.get_by_id(idGame), Player.get_by_id(idPlayer)

    if not game.turn_state == TurnState.MAKING_SUGGESTION:
        raise GameStateViolation('idGame=%d not in state MAKING_SUGGESTION' % (idGame,))

    if not game.players[game.player_current_turn_index].id == idPlayer:
        raise GameStateViolation(
            'it is idPlayer=%d turn, NOT idPlayer=%d' % (game.players[game.player_current_turn_index].id, idPlayer))

    # make sure we have a card of each type
    try:
        weapon_card = Card.get_by_id(cards['weapon'])
        character_card = Card.get_by_id(cards['character'])
        room_card = player.room.card
    except Exception, e:
        raise GameStateViolation('Invalid suggestion of cards')

    game.turn_state = TurnState.SOLICITING_DISPROVALS

    # @TODO problems here if there is only one player?
    game.player_current_disprover_index = (game.player_current_turn_index + 1) % len(game.players)

    card_list = [weapon_card, character_card,  room_card]
    game.current_suggestion = card_list

    state = game.serialize(idPlayer=idPlayer)
    game.log.append(log_message_dict['make_suggestion'] % (idPlayer, str(card_list)))
    return state

def submit_disproval(idGame, idPlayer, idCard):
    game, player = Game.get_by_id(idGame), Player.get_by_id(idPlayer)

    if not game.turn_state == TurnState.SOLICITING_DISPROVALS:
        raise GameStateViolation('idGame=%d not in state SOLICITING_DISPROVALS' % (idGame,))

    if not game.players[game.player_current_disprover_index].id == idPlayer:
        raise GameStateViolation(
            'it is idPlayer=%d disproval, NOT idPlayer=%d' % (game.players[game.player_current_disprover_index].id, idPlayer))

    # if you do not make a disproval, you must not own any qualifying cards
    if not idCard and any([i in game.currentSuggestion for i in player.cards]):
        raise GameStateViolation('idPlayer=%d can and must disprove' % (idPlayer,))

    # if you make a disproval, you must have that card
    if idCard not in [i.id for i in player.cards]:
        raise GameStateViolation('idPlayer=%d does not own idCard=%d' % (idCard,))

    if idCard: # disproval rendered
        game.turn_state = TurnState.WAITING_FOR_END
        game.player_current_disprover_index = None
        game.current_suggestion = []
        state = game.serialize(idPlayer=idPlayer)
        game.log.append(log_message_dict['render_disproval'] % (idPlayer, idCard))

    else: # cant disprove
        game.player_current_disprover_index = (game.player_current_disprover_index + 1) % len(game.players)

        # sometimes, nobody can disprove
        if game.player_current_disprover_index == game.player_current_turn_index:
            game.turn_state = TurnState.WAITING_FOR_END
            game.log.append('Disproval step ended without any disprovals')
        else:
            game.log.append('idPlayer=%d cannot disprove' % (idPlayer))

        state = game.serialize(idPlayer=idPlayer)

    return state

def end_player_turn(idGame, idPlayer):
    game = Game.get_by_id(idGame)
    player = Player.get_by_id(idPlayer)

    if not player in game.players:
        raise NoSuchObjectException('No player found for idGame=%d and idPlayer=%d' % (idGame, idPlayer))

    # TODO This needs tobe altered or removed
    #if not game.turn_state == TurnState.WAITING_FOR_END:
    #    raise GameStateViolation('idGame=%d not in state WAITING_FOR_END' % (idGame,))

    if not game.players[game.player_current_turn_index].id == idPlayer:
        raise GameStateViolation(
            'it is idPlayer=%d turn, NOT idPlayer=%d' % (game.players[game.player_current_turn_index].id, idPlayer))

    game.turn_state = TurnState.SELECTING_MOVE
    game.player_current_turn_index = (game.player_current_turn_index + 1) % len(game.players)

    state = game.serialize(idPlayer=idPlayer)
    game.log.append(log_message_dict['end_player_turn'] % (idPlayer,))

    return state

def get_solution(idGame):
    game = Game.get_by_id(idGame)
    return { i:game.solution[i].id for i in game.solution }

def make_accusation(idGame, idPlayer, accusation):
    game, player = Game.get_by_id(idGame), Player.get_by_id(idPlayer)

    if not player in game.players:
        raise NoSuchObjectException('No player found for idGame=%d and idPlayer=%d' % (idGame, idPlayer))

    # either you win, or you lose
    #   good luck.

    if accusation['room'] == game.solution['room'].id 
        and accusation['weapon'] == game.solution['weapon'].id
        and accusation['character'] == game.solution['character'].id:
        game.winner = idPlayer
        game.turn_state = None
        game.meta_state = GameStates.FINISHED
    else:
        game.players.remove(player)
        game.losers.append(player)
        
        # redistribute the loser's cards
        
        
        # if there is only one person left, they win!
        if game.players
    

    return state