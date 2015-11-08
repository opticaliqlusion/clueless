package edu.jhu.epioneers.clueless.communication;

/***
 * Request for joinGame operation
 */
public class JoinGameRequest {
    /***
     * Id of the game
     */
    private int idGame;

    /***
     * Id of the character making the request
     */
    private int idCharacter;

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public int getIdCharacter() {
        return idCharacter;
    }

    public void setIdCharacter(int idCharacter) {
        this.idCharacter = idCharacter;
    }
}
