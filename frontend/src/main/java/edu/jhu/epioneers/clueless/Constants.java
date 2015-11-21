package edu.jhu.epioneers.clueless;

/**
 * Contains constants used by the application
 */
public final class Constants {
    public static final String LobbyLayout = "/layout/LobbyLayout.fxml";
    public static final String ChooseCharacterLayout = "/layout/ChooseCharacterLayout.fxml";
    public static final String BoardLayout = "/layout/BoardLayout.fxml";

    public static final int StageSizeX = 800;
    public static final int StageSizeY = 600;

    //public static final String SERVER_URL="http://75.118.10.202:65500/";
    public static final String SERVER_URL="http://127.0.0.1:65500/";

    public static final String GET_PENDING_GAMES_PATH="get_pending_games";
    public static final String GET_CHARACTERS_PATH="get_characters";
    public static final String JOIN_GAME_PATH="join_game";
    public static final String GET_BOARD_STATE_PATH="get_board_state";
}