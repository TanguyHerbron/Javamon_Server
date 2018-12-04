package sample;

import fr.ensim.lemeeherbron.PokemonServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static final int HEIGHT = 500;
    private static final int WIDTH = 500;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Javamon Server");
        primaryStage.sizeToScene();

        Group group = new Group();

        Scene scene = new Scene(group, WIDTH, HEIGHT, Color.GREEN);

        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            PokemonServer pokemonServer = new PokemonServer(7210, group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
