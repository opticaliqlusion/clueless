package edu.jhu.epioneers.clueless.communication;

/***
 * Request class for making player moves
 */
public class MovePlayerRequest extends GamePlayerRequestBase {
    /***
     * Id of the room to move to
     */
    private int idRoom;

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }
}
