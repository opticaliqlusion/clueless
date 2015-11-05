package edu.jhu.epioneers.clueless.model;

/**
 * Data model for the player entity
 */
public class PlayerModel extends PositionModel {
    /**
     * Contains the player character information
     */
    private ModelBase character;

    public ModelBase getCharacter() {
        return character;
    }

    public void setCharacter(ModelBase character) {
        this.character = character;
    }
}
