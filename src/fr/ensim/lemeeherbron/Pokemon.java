package fr.ensim.lemeeherbron;



public class Pokemon {

    private int idClient;
    private int id;
    private String spritePath;
    private double x;
    private double y;
    private char direction;

    public Pokemon(int idClient, int id, String spritePath, double x, double y, char direction)
    {
        this.idClient = idClient;
        this.id = id;
        this.spritePath = spritePath;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public void setSpritePath(String spritePath) {
        this.spritePath = spritePath;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public char getDirection() {
        return direction;
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }
}
