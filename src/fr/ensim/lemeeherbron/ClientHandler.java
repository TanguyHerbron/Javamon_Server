package fr.ensim.lemeeherbron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

public class ClientHandler implements Runnable{
    private ClientInfo client;
    private Semaphore semEntityList;
    private boolean isAuthenticated = false;
    private BufferedReader clientBuffer;

    public ClientHandler(ClientInfo client, Semaphore semEntityList){
        this.client = client;
        this.semEntityList = semEntityList;

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
}
