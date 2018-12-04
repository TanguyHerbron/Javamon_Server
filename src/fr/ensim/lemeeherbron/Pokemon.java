package fr.ensim.lemeeherbron;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Pokemon extends VBox {

    private boolean hasBehavior;
    private int speed;
    private ImageView sprite;
    private String name;
    private String spritePath;
    private int id;
    private boolean foreign;

    public Pokemon(String spritePath, String name, int speed, boolean hasBehavior, int x, int y, int id)
    {
        sprite = new ImageView(spritePath);
        Text text = new Text(name);
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: bold");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(sprite.getFitWidth());

        this.hasBehavior = hasBehavior;
        this.speed = speed;
        this.name = name;
        this.spritePath = spritePath;

        sprite.setX(x);
        sprite.setY(y);

        sprite.setViewport(new Rectangle2D(0, 0, sprite.getFitWidth(), sprite.getFitHeight() + 40));

        this.getChildren().add(text);
        this.getChildren().add(sprite);

        this.id = id;

        foreign = true;
    }

    public Pokemon(String spritePath, String name, int speed, boolean hasBehavior, int x, int y)
    {
        sprite = new ImageView(spritePath);
        Text text = new Text(name);
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: bold");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(sprite.getFitWidth());

        this.hasBehavior = hasBehavior;
        this.speed = speed;
        this.name = name;
        this.spritePath = spritePath;

        sprite.setX(x);
        sprite.setY(y);

        sprite.setViewport(new Rectangle2D(0, 0, sprite.getFitWidth(), sprite.getFitHeight() + 40));

        this.getChildren().add(text);
        this.getChildren().add(sprite);

        id = hashCode();
    }

    public boolean hasBehavior()
    {
        return hasBehavior;
    }

    public void enableBehavior()
    {
        hasBehavior = true;
    }

    public int getSpeed()
    {
        return speed;
    }

    public void update()
    {
        if(hasBehavior)
        {
            int randX = (int) Math.round(Math.random() * (speed * 2) - speed);
            int randY = (int) Math.round(Math.random() * (speed * 2) - speed);

            if(randX + getLayoutX() > getMaxWidth() || randX + getLayoutX() < getMinWidth())
            {
                setLayoutX(getLayoutX() - randX);
            }
            else
            {
                setLayoutX(getLayoutX() + randX);
            }

            if(randY + getLayoutY() > getMaxHeight() || randY + getLayoutY() < getMinHeight())
            {
                setLayoutY(getLayoutY() - randY);
            }
            else
            {
                setLayoutY(getLayoutY() + randY);
            }
        }
    }

    public String getTx()
    {
        return name + "!" + spritePath + "!" + speed + "!" + hasBehavior + "!" + (int) getLayoutX() + "!" + (int) getLayoutY() + "!" + hashCode();
    }

    public int getPokemonId()
    {
        return id;
    }

    public boolean isForeign()
    {
        return foreign;
    }
}
