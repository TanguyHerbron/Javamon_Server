package fr.ensim.lemeeherbron;

import java.net.Socket;

public class ClientInfo {

    private Socket sock;
    private String name;
    private int id;

    public ClientInfo(Socket sock, int id){
        this.sock = sock;
        this.id = id;
    }


    public Socket getSock() {
        return sock;
    }

    public void setSock(Socket sock) {
        this.sock = sock;
    }

    public String getName(){return name;}

    public void setName(String name){this.name = name;}

    public int getID() {return id;}
}
