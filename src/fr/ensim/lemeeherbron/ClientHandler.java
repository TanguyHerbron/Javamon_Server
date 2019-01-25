package fr.ensim.lemeeherbron;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

public class ClientHandler implements Runnable{
    private ClientInfo client;
    private Semaphore semEntityList;
    private boolean isAuthenticated = false;
    private BufferedReader clientBuffer;
    private SQLiteHandler bdd;

    public ClientHandler(ClientInfo client, Semaphore semEntityList, SQLiteHandler bdd){
        this.client = client;
        this.semEntityList = semEntityList;
        this.bdd = bdd;

        try {
            this.clientBuffer = new BufferedReader(new InputStreamReader(client.getSock().getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(!isAuthenticated){
            verifyLog();
        }
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

        String msgType;

        msgType = jsonMessage.getString("type");
    }
}
