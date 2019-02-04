package fr.ensim.lemeeherbron;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.concurrent.Semaphore;

public class ClientHandler implements Runnable{
    private ClientInfo client;
    private Semaphore semEntityList;
    private boolean isAuthenticated = false;
    private BufferedReader clientBuffer;
    private SQLiteHandler bdd;
    private PrintWriter clientOutput;

    public ClientHandler(ClientInfo client, Semaphore semEntityList, SQLiteHandler bdd){
        this.client = client;
        this.semEntityList = semEntityList;
        this.bdd = bdd;

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
        while(client.getSock().isConnected()){
            try {
                if(clientBuffer.ready())
                {
                    clientMessageHandler(clientBuffer.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        JSONObject jsonMessage = new JSONObject(message);
        System.out.println("List of pokemon from client " + client.getID() + " :");

        JSONArray pokemonList = jsonMessage.getJSONArray("pokemon");
        for(int i = 0; i < pokemonList.length(); i++)
        {
            JSONObject pokemon = pokemonList.getJSONObject(i);
            int id;
            String name;
            int x ,y;
            id = pokemon.getInt("id");
            name = pokemon.getString("name");
            x = pokemon.getInt("x");
            y = pokemon.getInt("y");

            System.out.println("Id : " + id + "\nName : " + name + "\nx : " + x +" | y : " + y + "\n");
        }
    }
}
