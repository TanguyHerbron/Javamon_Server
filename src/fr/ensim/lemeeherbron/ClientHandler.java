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

    public ClientHandler(ClientInfo client,Set<Pokemon> listOfAllPokemon, Semaphore semEntityList, SQLiteHandler bdd){
        this.client = client;
        this.semEntityList = semEntityList;
        this.bdd = bdd;
        this.listOfAllPokemon = listOfAllPokemon;
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

        while(client.getSock().isBound() && !client.getSock().isClosed()){
            try {

                clientMessageHandler(clientBuffer.readLine());
                clientBuffer.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sendPokemonToClient();
        }
        clientDeconected();
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
        System.out.println("List of pokemon from client " + client.getID() + " :");

        JSONArray pokemonList = jsonMessage.getJSONArray("pokemon");

        //for each pokemon the client is sending
        for(int i = 0; i < pokemonList.length(); i++)
        {
            boolean allreadyExist = false;
            JSONObject pokemon = pokemonList.getJSONObject(i);
            int id;
            String name;
            double x ,y;
            char orientation;
            id = pokemon.getInt("id");
            name = pokemon.getString("name");
            x = pokemon.getDouble("x");
            y = pokemon.getDouble("y");
            orientation = pokemon.get("orientation").toString().charAt(0);

            //verify if the pokemon already exists
            for (Pokemon pokemonIndex : clientPokemon)
            {
                if(id == pokemonIndex.getId())
                {
                    allreadyExist = true;
                    pokemonIndex.setDirection(orientation);
                    pokemonIndex.setX(x);
                    pokemonIndex.setY(y);

                    newPokemonList.add(pokemonIndex);
                    break;
                }
            }

            if(!allreadyExist)
            {
                Pokemon newPokemon = new Pokemon(client.getID(), id, name, x, y, orientation);
                newPokemonList.add(newPokemon);
            }
            System.out.println("Id : " + id + "\nName : " + name + "\nx : " + x +" | y : " + y + "\n");
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

        updatePokemon(newPokemonList);
    }

    private void updatePokemon(Set<Pokemon> newPokemonList)
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

    private void sendPokemonToClient(){
        JSONArray pokemonListToSend = new JSONArray();
        for(Pokemon pokemon : listOfAllPokemon)
        {
            JSONObject pokemonObject = new JSONObject();
            pokemonObject.put("id", pokemon.getId());
            pokemonObject.put("name", pokemon.getSpritePath());
            pokemonObject.put("x", pokemon.getX());
            pokemonObject.put("y", pokemon.getY());
            pokemonObject.put("orientation", pokemon.getDirection());

            pokemonListToSend.put(pokemonObject);
        }
        clientOutput.println(pokemonListToSend.toString());
        System.out.println(pokemonListToSend.toString());
        clientOutput.flush();
    }

    public void clientDeconected()
    {
        try {
            semEntityList.acquire();
            listOfAllPokemon.removeAll(clientPokemon);
            clientPokemon.clear();
            semEntityList.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
