package fr.ensim.lemeeherbron;

public class Entity {

    private String sprite;
    private double x;
    private double y;
    private char orientation;
    private String mapValue;
    private boolean walking;

    public Entity(String sprite, double x, double y, char orientation, String mapValue, boolean walking)
    {
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.orientation = orientation;
        this.mapValue = mapValue;
        this.walking = walking;
    }

    public String getSprite() {
        return sprite;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public char getOrientation() {
        return orientation;
    }

    public String getMapValue() {
        return mapValue;
    }

    public boolean isWalking()
    {
        return walking;
    }
}
