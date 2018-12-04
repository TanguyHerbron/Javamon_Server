package fr.ensim.lemeeherbron;

import javafx.scene.Group;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class PokemonServer extends ServerSocket {

    public PokemonServer(int port, Group group) throws IOException {
        super(port);

        setReuseAddress(true);

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isClosed())
                {
                    HashMap<String, Pokemon> displayedPokemon = new HashMap<>();

                    try {
                        System.out.println("Waiting for connexion");

                        Socket client = accept();

                        System.out.println("New client connected");

                        Thread clientThread = new Thread(new PokemonClientManager(client, group, displayedPokemon));
                        clientThread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        serverThread.start();
    }


}
