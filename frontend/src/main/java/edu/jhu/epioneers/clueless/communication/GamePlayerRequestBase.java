package edu.jhu.epioneers.clueless.communication;

/***
 * Base request containing game id and player id
 */
public class GamePlayerRequestBase {
    /***
     * Id of the game
     */
    private int idGame;

    /***
     * Id of the player making the request
     */
    private int idPlayer;

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }
}
