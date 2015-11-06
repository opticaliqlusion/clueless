import sqlalchemy
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.sql import select
from sqlalchemy.orm import sessionmaker, relationship
from sqlalchemy import Column, Integer, String, ForeignKey, ForeignKeyConstraint

CARD_TYPE_ROOM      = 0
CARD_TYPE_CHARACTER = 1
CARD_TYPE_WEAPON    = 2

ROOM_TYPE_ROOM      = 0
ROOM_TYPE_HALLWAY   = 1

# special credit to
# http://docs.sqlalchemy.org/en/rel_1_0/orm/tutorial.html

db_name = 'clueless.db'

Base = declarative_base()
engine = sqlalchemy.create_engine('sqlite:///%s' % db_name)

class players(Base):
    __tablename__ = 'players'
    idPlayers = Column(Integer, primary_key=True)
    def __repr__(self):
        return '<player(id=%d)>' % (self.idPlayers)

class games(Base):
    __tablename__ = 'games'
    # whose turn is it
    # what is the state of the game (soliciting disprovals, waiting on moves?)
    idGames = Column(Integer, primary_key=True)
    def __repr__(self):
        return '<game(id=%d)>' % (self.idGames)

class characters(Base):
    __tablename__ = 'characters'
    idCharacters = Column(Integer, primary_key=True)
    name         = Column(String)
    def __repr__(self):
        return '<character(id=%d, name=%s)>' % (self.idCharacters, self.name)

class cards(Base):
    __tablename__ = 'cards'
    idCards = Column(Integer, primary_key=True)
    name    = Column(String)
    type    = Column(Integer)
    def __repr__(self):
        return '<cards(id=%d, name=%s, type=%d)>' % (self.idCharacters, self.name, self.type)

class rooms(Base):
    __tablename__ = 'rooms'
    idRooms = Column(Integer, primary_key=True)
    name    = Column(String)
    type    = Column(Integer)
    def __repr__(self):
        return '<rooms(id=%d, name=%s)>' % (self.idRooms, self.name)

class relation_room_adjacency(Base):
    __tablename__ = 'relation_room_adjacency'
    room_a = Column(Integer, primary_key=True)
    room_b = Column(Integer, primary_key=True)
    __table_args__ = (ForeignKeyConstraint([room_a, room_b], [rooms.idRooms, rooms.idRooms]), {})

class relation_player_game(Base):
    __tablename__ = 'relation_player_game'
    idPlayer = Column(Integer, primary_key=True)
    idGame = Column(Integer, primary_key=True)
    __table_args__ = (ForeignKeyConstraint([idPlayer, idGame], [players.idPlayers, games.idGames]), {})

# relation card_player_games

# relation character_player_games

def init_db():
    Base.metadata.create_all(engine)
    session = sessionmaker(bind=engine)()

    room_names   = ['Conservatory', 'Ballroom', 'Kitchen', 'Dining Room', 'Lounge', 'Hall', 'Study', 'Library', 'Billiard Room']
    char_names   = ['Miss Scarlet','Col. Mustard', 'Mrs. White', 'Mr. Green', 'Mrs. Peacock', 'Prof. Plum']
    weapon_names = ['Rope', 'Lead Pipe', 'Knife', 'Wrench', 'Candlestick', 'Revolver']

    # rooms
    session.add_all([rooms(name = i, type = ROOM_TYPE_ROOM) for i in room_names])

    # characters
    session.add_all([characters(name=i) for i in char_names])

    # cards
    session.add_all([cards(name=i, type=CARD_TYPE_CHARACTER) for i in char_names])
    session.add_all([cards(name=i, type=CARD_TYPE_ROOM)      for i in room_names])
    session.add_all([cards(name=i, type=CARD_TYPE_WEAPON)    for i in weapon_names])

    session.commit()

    # adjacency matrix
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

    def create_relation_room(room_a, room_b, hallway=True):
        conn = engine.connect()
        room1 = [ i for i in conn.execute(select([rooms]).where(rooms.name==room_a)) ][0]
        room2 = [ i for i in conn.execute(select([rooms]).where(rooms.name==room_b)) ][0]

        if hallway:
            hall_name = 'hall_%s_%s' % (room1.name, room2.name)
            new_hallway = rooms(name = hall_name, type = ROOM_TYPE_HALLWAY)
            session.add(new_hallway)

            session.flush()

            # room1-hallway adjacency
            session.add(relation_room_adjacency(room_a = room1.idRooms, room_b = new_hallway.idRooms))
            session.add(relation_room_adjacency(room_a = new_hallway.idRooms, room_b = room1.idRooms))

            # room2-hallway adjacency
            session.add(relation_room_adjacency(room_a = new_hallway.idRooms, room_b = room2.idRooms))
            session.add(relation_room_adjacency(room_a = room2.idRooms, room_b = new_hallway.idRooms))

        else:
            session.add(relation_room_adjacency(room_a = room1.idRooms, room_b = room2.idRooms))
            session.add(relation_room_adjacency(room_a = room2.idRooms, room_b = room1.idRooms))
        session.commit()

    for room_a, room_b in rooms_adjacent_with_halls:
        create_relation_room(room_a, room_b, hallway=True)

    for room_a, room_b in rooms_adjacent:
        create_relation_room(room_a, room_b, hallway=False)

