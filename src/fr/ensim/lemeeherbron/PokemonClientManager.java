package fr.ensim.lemeeherbron;

import javafx.application.Platform;
import javafx.scene.Group;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PokemonClientManager implements Runnable {

    private PrintWriter writer;
    private BufferedInputStream reader;
    private Socket socket;
    private Group group;
    private PokemonOnDisplay displayedPokemons;

    public PokemonClientManager(Socket socket, Group group)
    {
        this.socket = socket;
        this.group = group;

        displayedPokemons = PokemonOnDisplay.getInstance();
    }

    @Override
    public void run() {
        while(!socket.isClosed())
        {
            try {
                writer = new PrintWriter(socket.getOutputStream());
                reader = new BufferedInputStream(socket.getInputStream());

                String response = read();
                int pokeSend = 0;

                String[] result = response.split(Pattern.quote("$"));

                String clientId = result[0];

                if(!displayedPokemons.containsKey(clientId))
                {
                    displayedPokemons.put(clientId, new HashMap<>());
                }

                for(int i = 1; i < result.length; i++)
                {
                    Pokemon pokemon = new Pokemon(result[i]);

                    if(displayedPokemons.get(clientId).containsKey(pokemon.getPokemonId()))
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Pokemon pokemonResult = displayedPokemons.get(clientId).get(pokemon.getPokemonId());

                                pokemonResult.setLayoutY(pokemon.getLayoutY());
                                pokemonResult.setLayoutX(pokemon.getLayoutX());
                            }
                        });
                    }
                    else
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                displayedPokemons.get(clientId).put(pokemon.getPokemonId(), pokemon);
                                group.getChildren().add(pokemon);

                                removeDuplicates();
                            }
                        });
                    }
                }

                /*for (Object obj : displayedPokemons.entrySet()) {

                    Map.Entry entry = (Map.Entry) obj;

                    if(!entry.getKey().equals(clientId))
                    {
                        for(Object objPoke : displayedPokemons.get(entry.getKey()).entrySet())
                        {
                            Map.Entry entryPoke = (Map.Entry) objPoke;

                            Pokemon pokemon = (Pokemon) entryPoke.getValue();

                            writer.write(pokemon.getTx() + "$");
                            pokeSend++;
                        }
                    }
                }*/

                if(pokeSend == 0)
                {
                    writer.write(">");
                }

                writer.flush();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();

                reader = null;
            }
        }
    }

    private void removeDuplicates()
    {
        int i = 0;

        while(i < group.getChildren().size())
        {
            int j = i + 1;

            while(j < group.getChildren().size())
            {
                if(((Pokemon) group.getChildren().get(i)).getPokemonId().equals(((Pokemon) group.getChildren().get(j)).getPokemonId()))
                {
                    group.getChildren().remove(j);
                }

                j++;
            }

            i++;
        }
    }

    private String read() throws IOException{
        String response = "";
        int stream = 0;
        byte[] b = new byte[4096];

        try {
            stream = reader.read(b);
        } catch (SocketException e) {
            e.printStackTrace();

            reader = null;
        }
        response = new String(b, 0, stream);

        return response;
    }
}
