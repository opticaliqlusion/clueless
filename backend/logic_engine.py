import sys, os, json

import db_iface_mock as db_iface

class GameState():
    def __init__():
        return


def get_characters():
    return db_iface.get_characters()

def get_pending_games():
    return db_iface.get_pending_games()

def add_player_to_game(idGame, idPlayer=None):
    addPlayerResult = db_iface.add_player_to_game(idGame, idPlayer)
    return get_board_state(addPlayerResult['idGame'], addPlayerResult['idPlayer'])
def start_game(idGame, idPlayer):
    return db_iface.start_game(idGame, idPlayer)

def get_valid_moves(idGame, idPlayer):
    return db_iface.get_valid_moves(idGame, idPlayer)

def get_board_state(idGame, idPlayer):
    return db_iface.get_board_state(idGame, idPlayer)

def move_player(idGame, idPlayer, idRoom):
    return db_iface.move_player(idGame, idPlayer, idRoom)
    
def make_suggestion(idGame, idPlayer, cards):
    return

def make_accusation(idGame, idPlayer, cards):
    return

def disprove_suggestion(idGame, idPlayer, idCard):
    return

def end_player_turn(idGame, idPlayer):
    return
    
def main():
    return

if __name__ == '__main__':
    main()