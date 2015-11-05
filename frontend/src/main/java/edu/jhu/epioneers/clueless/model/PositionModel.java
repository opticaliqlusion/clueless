package edu.jhu.epioneers.clueless.model;

/**
 * Base model for entities with a position
 */
public class PositionModel extends ModelBase {
    /**
     * x position of the entity
     */
    private int x;

    /**
     * y position of the entity
     */
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
