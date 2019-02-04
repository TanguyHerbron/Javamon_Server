package sample;

import fr.ensim.lemeeherbron.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class Main{
    private static final int PORT = 7777;
    private static Semaphore semEntityList;
    private static SQLiteHandler bdd;
    private static int idCounter;
    private static Set<Pokemon> listOfAllPokemon;

    public static void main(String[] args) {
        listOfAllPokemon = new HashSet<Pokemon>();
        semEntityList = new Semaphore(1, true);
        bdd = new SQLiteHandler("javamon.db");

        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(PORT);
            do {
                System.out.println("Waiting for clients...");
                Socket sock = serverSocket.accept();
                System.out.println("New client connected : " + sock.getLocalAddress().toString());
                ClientInfo newClient = new ClientInfo(sock, idCounter);
                idCounter++;
                new Thread(new ClientHandler(newClient, listOfAllPokemon, semEntityList, bdd)).start();
            }while(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}