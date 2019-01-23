package sample;

import fr.ensim.lemeeherbron.ClientHandler;
import fr.ensim.lemeeherbron.ClientInfo;
import fr.ensim.lemeeherbron.EntityJavamon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main{
    private static final int PORT = 7777;
    private static List<EntityJavamon> entityList;
    private static Semaphore semEntityList;

    public static void main(String[] args) {
        entityList = new ArrayList<EntityJavamon>();
        semEntityList = new Semaphore(1, true);

            ServerSocket serverSocket;
        try {

            serverSocket = new ServerSocket(PORT);
            do {
                System.out.println("Waiting for clients...");
                Socket sock = serverSocket.accept();
                System.out.println("New client connected : " + sock.getLocalAddress().toString());
                ClientInfo newClient = new ClientInfo(sock);
                new Thread(new ClientHandler(newClient, semEntityList)).start();
            }while(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}