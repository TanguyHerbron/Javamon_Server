package fr.ensim.lemeeherbron;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ClientHandler implements Runnable{

    private ClientInfo client;
    private Semaphore semEntityList;
    private boolean isAuthenticated = false;
    private BufferedReader clientBuffer;
    private SQLiteHandler bdd;
    private PrintWriter clientOutput;
    private Set<Pokemon> listOfAllPokemon;
    private Set<Pokemon> clientPokemon;

    private Set<Entity> listOfAllPlayers;
    private Entity currentPlayer;

    public ClientHandler(ClientInfo client,Set<Pokemon> listOfAllPokemon, Set<Entity> listOfAllPlayers, Semaphore semEntityList, SQLiteHandler bdd){
        this.client = client;
        this.semEntityList = semEntityList;
        this.bdd = bdd;
        this.listOfAllPokemon = listOfAllPokemon;
        this.listOfAllPlayers = listOfAllPlayers;
        clientPokemon = new HashSet<Pokemon>();

        try {
            this.clientBuffer = new BufferedReader(new InputStreamReader(client.getSock().getInputStream()));
            this.clientOutput = new PrintWriter(new OutputStreamWriter(client.getSock().getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        clientOutput.println(client.getID());
        clientOutput.flush();

        while(!client.getSock().isClosed()){
            try {
                clientMessageHandler(clientBuffer.readLine());
                checkForBaby();
                sendDataToClient();
            } catch (IOException e) {
                try {
                    client.getSock().close();
                    clientDisconnected();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //e.printStackTrace();
            }
        }
//        while(!isAuthenticated){
//            verifyLog();
//        }
    }

    private void verifyLog() {
        try {
            String message = clientBuffer.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clientMessageHandler(String message)
    {
        Set<Pokemon> newPokemonList = new HashSet<Pokemon>();
        JSONObject jsonMessage = new JSONObject(message);
        //System.out.println("List of pokemon from client " + client.getID() + " :");

        JSONObject playerObject = jsonMessage.getJSONObject("player");

        Entity newPlayer = new Entity(playerObject.getString("sprite"),
                playerObject.getDouble("x"),
                playerObject.getDouble("y"),
                playerObject.get("orientation").toString().charAt(0),
                playerObject.getString("map"),
                playerObject.getBoolean("walking"));

        JSONArray pokemonList = jsonMessage.getJSONArray("pokemon");

        //for each pokemon the client is sending
        for(int i = 0; i < pokemonList.length(); i++)
        {
            boolean allreadyExist = false;
            JSONObject pokemon = pokemonList.getJSONObject(i);
            int id, sexe;
            String name;
            double x ,y, speed;
            char orientation;
            id = pokemon.getInt("id");
            name = pokemon.getString("name");
            x = pokemon.getDouble("x");
            y = pokemon.getDouble("y");
            orientation = pokemon.get("orientation").toString().charAt(0);
            speed = pokemon.getDouble("speed");
            sexe = pokemon.getInt("sexe");

            //verify if the pokemon already exists
            for (Pokemon pokemonIndex : clientPokemon)
            {
                if(id == pokemonIndex.getId())
                {
                    allreadyExist = true;
                    pokemonIndex.setSpritePath(name);
                    pokemonIndex.setDirection(orientation);
                    pokemonIndex.setX(x);
                    pokemonIndex.setY(y);

                    newPokemonList.add(pokemonIndex);
                    break;
                }
            }
            if(!allreadyExist)
            {
                Pokemon newPokemon = new Pokemon(client.getID(), id, name, x, y, orientation, speed, sexe);
                newPokemonList.add(newPokemon);
            }
        }

        Iterator iterator = clientPokemon.iterator();
        List<Pokemon> removePokemons = new ArrayList<>();
        while(iterator.hasNext())
        {
            boolean hasBeenDeleted = true;

            Pokemon presentPokemon = (Pokemon) iterator.next();

            for(Pokemon pokemon : newPokemonList)
            {
                if(pokemon.getId() == presentPokemon.getId())
                {
                    hasBeenDeleted = false;
                }
            }
            if(hasBeenDeleted)
            {
                removePokemons.add(presentPokemon);
            }
        }

        newPokemonList.removeAll(removePokemons);

        updateEntities(newPokemonList, newPlayer);
    }

    private void updateEntities(Set<Pokemon> newPokemonList, Entity newPlayer)
    {
        try {
            semEntityList.acquire();

            if(currentPlayer != null) listOfAllPlayers.remove(currentPlayer);
            listOfAllPlayers.add(newPlayer);

            semEntityList.release();
            currentPlayer = newPlayer;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        updateEntities(newPokemonList);
    }

    private void updateEntities(Set<Pokemon> newPokemonList)
    {
        try {
            semEntityList.acquire();

            listOfAllPokemon.removeAll(clientPokemon);
            listOfAllPokemon.addAll(newPokemonList);

            semEntityList.release();
            clientPokemon = newPokemonList;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized void sendDataToClient(){
        JSONObject mainObject = new JSONObject();

        JSONArray pokemonListToSend = new JSONArray();
        JSONArray entityListToSend = new JSONArray();

        Set<Pokemon> tempPokemon = null;
        Set<Entity> tempEntity = null;

        try {
            semEntityList.acquire();
            tempPokemon = new HashSet<Pokemon>(listOfAllPokemon);
            tempEntity = new HashSet<>(listOfAllPlayers);
            semEntityList.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(Pokemon pokemon : tempPokemon)
        {
            JSONObject pokemonObject = new JSONObject();
            pokemonObject.put("id", pokemon.getId());
            pokemonObject.put("name", pokemon.getSpritePath());
            pokemonObject.put("x", pokemon.getX());
            pokemonObject.put("y", pokemon.getY());
            pokemonObject.put("orientation", pokemon.getDirection());
            pokemonObject.put("speed", pokemon.getSpeed());
            pokemonObject.put("sexe", pokemon.getSexe());

            pokemonListToSend.put(pokemonObject);
        }

        for(Entity entity : tempEntity)
        {
            if(entity.getMapValue().equals(currentPlayer.getMapValue()))
            {
                JSONObject entityObject = new JSONObject();

                entityObject.put("sprite", entity.getSprite());
                entityObject.put("x", entity.getX());
                entityObject.put("y", entity.getY());
                entityObject.put("orientation", entity.getOrientation());
                entityObject.put("walking", entity.isWalking());

                entityListToSend.put(entityObject);
            }
        }

        mainObject.put("entities", entityListToSend);
        mainObject.put("pokemon", pokemonListToSend);

        clientOutput.println(mainObject.toString());
        //System.out.println(pokemonListToSend.toString());
        clientOutput.flush();

        cleanNewPokemons(tempPokemon);
    }

    private void cleanNewPokemons(Set<Pokemon> pokemonSet)
    {
        for(Pokemon pokemon : pokemonSet)
        {
            if(pokemon.getId() == 0)
            {
                try {
                    semEntityList.acquire();
                    listOfAllPokemon.remove(pokemon);
                    semEntityList.release();
                } catch (InterruptedException e) {}
            }
        }
    }

    public void clientDisconnected()
    {
        try {
            semEntityList.acquire();
            listOfAllPokemon.removeAll(clientPokemon);
            listOfAllPlayers.remove(currentPlayer);
            clientPokemon.clear();
            currentPlayer = null;
            semEntityList.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void checkForBaby(){
        Set<Pokemon> temp = new HashSet<Pokemon>();
        Set<Pokemon> tempClientPokemon = new HashSet<Pokemon>(clientPokemon);
        try {
            semEntityList.acquire();
            temp = new HashSet<Pokemon>(listOfAllPokemon);
            semEntityList.release();
        } catch (InterruptedException e) {}

        for (Pokemon pokemon : clientPokemon)
        {
            for (Pokemon pokemonCmp : temp) {
                if(pokemon.getX() == pokemonCmp.getX()
                        && pokemon.getY() == pokemonCmp.getY()
                        && pokemon.getSpritePath().equals(pokemonCmp.getSpritePath())
                        && (pokemon.getId() != pokemonCmp.getId() || pokemon.getIdClient() != pokemonCmp.getIdClient())
                        && System.currentTimeMillis() - pokemon.getLastFuck() >= 30000
                        && System.currentTimeMillis() - pokemonCmp.getLastFuck() >= 30000
                        && pokemon.getSexe() + pokemonCmp.getSexe() == 1)
                {
                    int nbBaby;

                    switch (pokemon.getSpritePath())
                    {
                        case "pokemon/chetiflor":
                            nbBaby = new Random().nextInt(9);
                            break;
                        case "pokemon/boustiflor":
                            nbBaby = new Random().nextInt(9);
                            break;
                        case "pokemon/empiflor":
                            nbBaby = new Random().nextInt(9);
                            break;
                        case "pokemon/bulbizarre":
                            nbBaby = new Random().nextInt(1);
                            break;
                        case "pokemon/herbizarre":
                            nbBaby = new Random().nextInt(1);
                            break;
                        case "pokemon/florizarre":
                            nbBaby = new Random().nextInt(1);
                            break;
                        case "pokemon/magicarpe":
                            nbBaby = new Random().nextInt(7);
                            break;
                        case "pokemon/leviator":
                            nbBaby = new Random().nextInt(2);
                            break;
                        case "pokemon/mystherbe":
                            nbBaby = new Random().nextInt(5);
                            break;
                        case "pokemon/ortide":
                            nbBaby = new Random().nextInt(3);
                            break;
                        case "pokemon/rafflesia":
                            nbBaby = new Random().nextInt(1);
                            break;
                        case "pokemon/pikachu":
                            nbBaby = new Random().nextInt(7);
                            break;
                        case "pokemon/ptera":
                            nbBaby = new Random().nextInt(2);
                            break;
                        default:
                            nbBaby = new Random().nextInt(1);
                    }

                    for(int i = 0; i < nbBaby; i++)
                    {
                        if(new Random().nextBoolean())
                        {
                            tempClientPokemon.add(new Pokemon(pokemon.getIdClient()
                                    , 0
                                    , pokemon.getSpritePath()
                                    , pokemon.getX()
                                    , pokemon.getY()
                                    , pokemon.getDirection()
                                    , pokemon.getSpeed()
                                    , pokemon.getSexe()));
                        }
                        else
                        {
                            tempClientPokemon.add(new Pokemon(pokemonCmp.getIdClient()
                                    , 0
                                    , pokemon.getSpritePath()
                                    , pokemon.getX()
                                    , pokemon.getY()
                                    , pokemon.getDirection()
                                    , pokemon.getSpeed()
                                    , pokemon.getSexe()));
                        }

                        pokemonCmp.fucked();
                        pokemon.fucked();
                    }
                }
            }
        }

        clientPokemon = tempClientPokemon;

        updateEntities(tempClientPokemon);
    }
}
