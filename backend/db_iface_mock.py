import os, sys, random

def enum(*sequential, **named):
    enums = dict(zip(sequential, range(len(sequential))), **named)
    return type('Enum', (), enums)

RoomTypes = enum('ROOM', 'HALL')
GameStates = enum('PENDING', 'PLAYING', 'FINISHED')
TurnState = enum('SELECTING_MOVE', 'MAKING_SUGGESTION', 'SOLICITING_DISPROVALS', 'WAITING_FOR_END')
CardTypes = enum('WEAPON', 'ROOM', 'CHARACTER')
char_name_list = ['Miss Scarlet','Col. Mustard', 'Mrs. White', 'Mr. Green', 'Mrs. Peacock', 'Prof. Plum']
PlayersNames = ['']
PlayersNames.extend(char_name_list)

log_message_dict = {
        'start_game':'%s started the game',
        'move_player':'%s moved from %s to %s',
        'make_suggestion':'%s made suggestion %s',
        'render_disproval':'%s disproved suggestion by revealing %s',   
        'make_accusation':'%s made accusation %s, resulting in %s',
        'end_player_turn':'%s ended their turn',
        'join_game':'%s joined the game',
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
        self.isPlaying = True
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
        self.solution = []
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
            playerCards = [i.id for i in player.cards]

        return {
            'idPlayer':idPlayer,
            # TODO Hack if room or char map is null
            'playerGameIdMap': {i.id: (None if not i.room else i.room.id) for i in self.players},
            'characterMap':  {i.id: i.idCharacter for i in self.players},
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
    ('Miss Scarlet', CardTypes.CHARACTER),
    ('Colonel Mustard', CardTypes.CHARACTER),
    ('Mrs. White', CardTypes.CHARACTER),
    ('Mr. Green', CardTypes.CHARACTER),
    ('Mrs. Peacock', CardTypes.CHARACTER),
    ('Professor Plum', CardTypes.CHARACTER),
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
    game.log.append(log_message_dict['join_game'] % (PlayersNames[idCharacter],))

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
    game.solution.append(card_list_rooms.pop())
    game.solution.append(card_list_weapons.pop())
    game.solution.append(card_list_characters.pop())

    i = 0
    allPlayerCards = []
    allPlayerCards.extend(card_list_rooms)
    allPlayerCards.extend(card_list_weapons)
    allPlayerCards.extend(card_list_characters)
    random.shuffle(allPlayerCards)

    while allPlayerCards:
        if allPlayerCards:
            game.players[i].cards.append(allPlayerCards.pop())
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
    game.log.append(log_message_dict['start_game'] % (PlayersNames[Player.get_by_id(idPlayer).idCharacter],))
    return state


def get_valid_moves(idGame, idPlayer):
    game = Game.get_by_id(idGame)
    player = Player.get_by_id(idPlayer)

    if not player in game.players:
        raise NoSuchObjectException('No player found for idGame=%d and %s' % (idGame, PlayersNames[Player.get_by_id(idPlayer).idCharacter]))

    moves =[i.id for i in player.room.adjacent_rooms];
    for room in moves:
        if Room.get_by_id(room).type == RoomTypes.HALL and any([i.room == Room.get_by_id(room) for i in game.players]):
            moves.remove(room)

    return moves

def get_board_state(idGame, idPlayer):
    game = Game.get_by_id(idGame)
    player = Player.get_by_id(idPlayer)

    if not player in game.players:
        raise NoSuchObjectException('No player found for idGame=%d and %s' % (idGame, PlayersNames[Player.get_by_id(idPlayer).idCharacter]))

    return game.serialize(idPlayer=idPlayer)

def move_player(idGame, idPlayer, idRoom):
    game = Game.get_by_id(idGame)
    player = Player.get_by_id(idPlayer)

    if not player in game.players:
        raise NoSuchObjectException('No player found for idGame=%d and %s' % (idGame, PlayersNames[Player.get_by_id(idPlayer).idCharacter]))

    if not game.turn_state == TurnState.SELECTING_MOVE:
        raise GameStateViolation('idGame=%d not in state SELECTING_MOVE' % (idGame,))

    if not game.players[game.player_current_turn_index].id == idPlayer:
        raise GameStateViolation(
            'it is %s\'s turn, NOT %s\'s' % (PlayersNames[Player.get_by_id(game.players[game.player_current_turn_index].id).idCharacter], PlayersNames[Player.get_by_id(idPlayer).idCharacter]))

    if not idRoom in [i.id for i in player.room.adjacent_rooms]:
        raise GameStateViolation('idRoom=%d and idRoom=%d not adjacent' % (current_room.id, idRoom))

    oldRoom = player.room
    newRoom = Room.get_by_id(idRoom)


    if newRoom.type == RoomTypes.HALL and any([i.room == newRoom for i in game.players]):
        raise GameStateViolation('idRoom=%d of type HALL occupied' % (newRoom.id,))

    player.room = newRoom

    # TODO change this to TurnState.MAKING_SUGGESTION, this is just for testing
    game.turn_state = TurnState.MAKING_SUGGESTION
    state = game.serialize(idPlayer=idPlayer)
    game.log.append(log_message_dict['move_player'] % (PlayersNames[Player.get_by_id(idPlayer).idCharacter], oldRoom.name, player.room.name))
    return state

def make_suggestion(idGame, idPlayer, cards):
    game, player = Game.get_by_id(idGame), Player.get_by_id(idPlayer)

    if not game.turn_state == TurnState.MAKING_SUGGESTION:
        raise GameStateViolation('idGame=%d not in state MAKING_SUGGESTION' % (idGame,))

    if not game.players[game.player_current_turn_index].id == idPlayer:
        raise GameStateViolation(
            'it is %s\'s turn, NOT %s\'s' % (PlayersNames[Player.get_by_id(game.players[game.player_current_turn_index].id).idCharacter], PlayersNames[Player.get_by_id(idPlayer).idCharacter]))

    # make sure we have a card of each type
    try:
        # TODO Phil's mad Python skills
        card1 = Card.get_by_id(cards[0])

        if card1.type == CardTypes.WEAPON:
            weapon_card = card1
            character_card = Card.get_by_id(cards[1])
        else:
            weapon_card = Card.get_by_id(cards[1])
            character_card = card1

        room_card = player.room.card
    except Exception, e:
        raise GameStateViolation('Invalid suggestion of cards')

    game.turn_state = TurnState.SOLICITING_DISPROVALS

    # @TODO problems here if there is only one player?
    game.player_current_disprover_index = (game.player_current_turn_index + 1) % len(game.players)

    card_list = [weapon_card, character_card,  room_card]
    game.current_suggestion = card_list
    card_string = "" + character_card.name + " in the " + room_card.name + " with the " + weapon_card.name + "."
    
    state = game.serialize(idPlayer=idPlayer)
    game.log.append(log_message_dict['make_suggestion'] % (PlayersNames[Player.get_by_id(idPlayer).idCharacter], card_string))
    return state

def add_log(idGame, idPlayer, logContent):
    game, player = Game.get_by_id(idGame), Player.get_by_id(idPlayer)
    game.log.append("" + PlayersNames[Player.get_by_id(idPlayer).idCharacter] + ": "+logContent)

def submit_disproval(idGame, idPlayer, idCard):
    game, player = Game.get_by_id(idGame), Player.get_by_id(idPlayer)

    if not game.turn_state == TurnState.SOLICITING_DISPROVALS:
        raise GameStateViolation('idGame=%d not in state SOLICITING_DISPROVALS' % (idGame,))

    if not game.players[game.player_current_disprover_index].id == idPlayer:
        raise GameStateViolation(
            'it is %s\'s disproval, NOT %s\'s' % (Player.get_by_id(game.players[game.player_current_disprover_index].id), PlayersNames[Player.get_by_id(idPlayer).idCharacter]))

    # if you do not make a disproval, you must not own any qualifying cards
    if not idCard and any([i in game.current_suggestion for i in player.cards]):
        raise GameStateViolation('%s can and must disprove' % (PlayersNames[Player.get_by_id(idPlayer).idCharacter],))

    if idCard: # disproval rendered
        # if you make a disproval, you must have that card
        if idCard not in [i.id for i in player.cards]:
            raise GameStateViolation('%s does not own idCard=%d' % (PlayersNames[Player.get_by_id(idPlayer).idCharacter],idCard,))

        game.turn_state = TurnState.WAITING_FOR_END
        game.player_current_disprover_index = 0
        game.current_suggestion = []
        state = game.serialize(idPlayer=idPlayer)
        game.log.append(log_message_dict['render_disproval'] % (PlayersNames[Player.get_by_id(idPlayer).idCharacter], Card.get_by_id(idCard).name))

    else: # cant disprove
        game.player_current_disprover_index = (game.player_current_disprover_index + 1) % len(game.players)

        # sometimes, nobody can disprove
        if game.player_current_disprover_index == game.player_current_turn_index:
            game.turn_state = TurnState.WAITING_FOR_END
            game.log.append('%s cannot disprove' % (PlayersNames[Player.get_by_id(idPlayer).idCharacter]))
            game.log.append('Disproval step ended without any disprovals')
        else:
            game.log.append('%s cannot disprove' % (PlayersNames[Player.get_by_id(idPlayer).idCharacter]))

        state = game.serialize(idPlayer=idPlayer)

    return state

def end_player_turn(idGame, idPlayer):
    game = Game.get_by_id(idGame)
    player = Player.get_by_id(idPlayer)

    if not player in game.players:
        raise NoSuchObjectException('No player found for idGame=%d and %s' % (idGame, PlayersNames[Player.get_by_id(idPlayer).idCharacter]))

    # TODO This needs to be altered or removed
    #if not game.turn_state == TurnState.WAITING_FOR_END:
    #    raise GameStateViolation('idGame=%d not in state WAITING_FOR_END' % (idGame,))

    if not game.players[game.player_current_turn_index].id == idPlayer:
        raise GameStateViolation(
            'it is %s\'s turn, NOT %s\'s' % (PlayersNames[Player.get_by_id(game.players[game.player_current_turn_index].id).idCharacter], PlayersNames[Player.get_by_id(idPlayer).idCharacter]))

    game.player_current_turn_index = (game.player_current_turn_index + 1) % len(game.players)
    
    # if the player has lost, skip their turn
    while(game.players[game.player_current_turn_index].isPlaying == False):
        game.player_current_turn_index = (game.player_current_turn_index + 1) % len(game.players)
    game.turn_state = TurnState.SELECTING_MOVE
    
    state = game.serialize(idPlayer=idPlayer)
    game.log.append(log_message_dict['end_player_turn'] % (PlayersNames[Player.get_by_id(idPlayer).idCharacter],))

    return state

def get_solution(idGame):
    game = Game.get_by_id(idGame)
    print('Game solution=%s' % (str(game.solution)))
    return [i.id for i in game.solution]

def make_accusation(idGame, idPlayer, accusation):
    game, player = Game.get_by_id(idGame), Player.get_by_id(idPlayer)

    if not player in game.players:
        raise NoSuchObjectException('No player found for idGame=%d and %s' % (idGame, PlayersNames[Player.get_by_id(idPlayer).idCharacter]))

    if not game.players[game.player_current_turn_index].id == idPlayer:
        raise GameStateViolation(
            'it is %s\'s turn, NOT %s\'s' % (PlayersNames[Player.get_by_id(game.players[game.player_current_turn_index].id).idCharacter], PlayersNames[Player.get_by_id(idPlayer).idCharacter]))

    # either you win, or you lose
    #   good luck.

    print("Parsing accusation : %s vice solution=%s" % (accusation,game.solution))
    
    if set([i.id for i in game.solution]) == set(accusation):
        print("PLAYER WON: %s" % (player))
        game.winner = player
        game.losers = [i for i in game.players if i != player]
        for i in game.losers:
            i.isPlaying = False
        game.turn_state = None
        game.meta_state = GameStates.FINISHED
        game.log.append('%s won the game!' % (PlayersNames[Player.get_by_id(idPlayer).idCharacter],))
    else:
        print("PLAYER LOST: %s" % (player))
        game.losers.append(player)
        player.isPlaying = False
        game.log.append('%s lost the game' % (PlayersNames[player.idCharacter],))

        # if there is only one person left, they win!
        playersRemaining = [i for i in game.players if i.isPlaying]
        if len(playersRemaining) == 1:
            game.winner = playersRemaining[0]
            print("PLAYER WON: %s" % (game.winner))
            game.meta_state = GameStates.FINISHED
            game.log.append('%s won the game by default!' % (PlayersNames[Player.get_by_id(game.winner.id).idCharacter],))

            

    state = game.serialize(idPlayer=idPlayer)
    return state
