package edu.jhu.epioneers.clueless.model;

/**
 * Created by Phillip on 11/4/2015.
 */
public class RoomModel extends PositionModel {
    private PositionModel secretPassage;

    public PositionModel getSecretPassage() {
        return secretPassage;
    }

    public void setSecretPassage(PositionModel secretPassage) {
        this.secretPassage = secretPassage;
    }
}
