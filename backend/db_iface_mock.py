import os, sys, random


def enum(*sequential, **named):
    enums = dict(zip(sequential, range(len(sequential))), **named)
    return type('Enum', (), enums)


RoomTypes = enum('ROOM', 'HALL')
GameStates = enum('PENDING', 'PLAYING', 'FINISHED')
TurnState = enum('SELECTING_MOVE', 'MAKING_SUGGESTION', 'SOLICITING_DISPROVALS')
CardTypes = enum('WEAPON', 'ROOM', 'CHARACTER')


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
    def __init__(self, id, name):
        self.id = id
        self.name = name


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
        self.turn_state = None

        self.solution = []

        super(Game, self).__init__()

    def __repr__(self):
        return '<Game(id=%d)>' % (self.id,)

    def serialize(self, idPlayer=None):
        playerCards = None
        if idPlayer:
            player = [i for i in self.players if i.id == idPlayer][0]
            playerCards = [i.id for i in player.cards]

        # import pdb; pdb.set_trace()
        return {
            # TODO Hack if room or char map is null
            'playerGameIdMap': {i.id: (None if i.room is None else i.room.id) for i in self.players},
            'characterMap': {self.players[i].id: self.character_map[i] for i in range(len(self.character_map))},
            'idCurrentTurn': self.player_current_turn_index,
            'gameState': self.meta_state,
            'turnState': self.turn_state,
            'cardIds': playerCards,
            # TODO Actual log data
            'logs': [],
            'lastLogId': 0,
            'idGame': self.id,
            'idPlayer': idPlayer
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
    r = Card(room.name, CardTypes.ROOM)


# TODO this is here just to get data back to the client

def get_characters():
    characters = [Character(1, "Char 1").__dict__,
                  Character(2, "Char 2").__dict__,
                  Character(3, "Char 3").__dict__,
                  Character(4, "Char 4").__dict__,
                  Character(5, "Char 5").__dict__,
                  Character(6, "Char 6").__dict__]

    return characters


def get_pending_games():
    # The full extent of my python skills
    pendingGames = dict()

    for i in Game.static_list:
        if i.meta_state == GameStates.PENDING:
            pendingGames[i.id] = []

            for p in i.players:
                pendingGames[i.id].append(p.idCharacter)

    return pendingGames


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

    i = 0
    while card_list_rooms and card_list_weapons and card_list_characters:

        if card_list_rooms:
            game.players[i].cards.append(card_list_rooms.pop())

        if card_list_weapons:
            game.players[i].cards.append(card_list_weapons.pop())

        if card_list_characters:
            game.players[i].cards.append(card_list_characters.pop())

        i = (i + 1) % len(game.players)

    # generate players' starting positions, randomly
    card_list_rooms = [Room.get_by_name(i.name) for i in Card.static_list if i.type == CardTypes.ROOM]
    random.shuffle(card_list_rooms)

    for player in game.players:
        player.room = card_list_rooms.pop()

    game.character_map = range(0, len([i for i in Card.static_list if i.type == CardTypes.CHARACTER]))
    random.shuffle(game.character_map)

    # finally, tell the game its time to start playing
    game.turn_state = TurnState.SELECTING_MOVE

    return game.serialize(idPlayer=idPlayer)


def get_valid_moves(idGame, idPlayer):
    game = Game.get_by_id(idGame)
    player = Player.get_by_id(idPlayer)

    if not player in game.players:
        return

    return [i.id for i in player.room.adjacent_rooms]

def get_all_cards():
    return Card.static_list;

def get_board_state(idGame, idPlayer):
    game = Game.get_by_id(idGame)
    player = Player.get_by_id(idPlayer)
    if not player in game.players:
        return

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

    print('moving player %d to room %d' % (idPlayer, idRoom))
    player.room = Room.get_by_id(idRoom)

    game.turn_state = TurnState.MAKING_SUGGESTION

    return game.serialize(idPlayer=idPlayer)
