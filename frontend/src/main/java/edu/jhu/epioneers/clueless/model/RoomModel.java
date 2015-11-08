package edu.jhu.epioneers.clueless.model;

/***
 * Represents a room entity
 */
public class RoomModel extends PositionModel {
    /***
     * Coordinates of secret passage associated with the room
     */
    private PositionModel secretPassage;

    public PositionModel getSecretPassage() {
        return secretPassage;
    }

    public void setSecretPassage(PositionModel secretPassage) {
        this.secretPassage = secretPassage;
    }
}
