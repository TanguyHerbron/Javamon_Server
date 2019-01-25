package fr.ensim.lemeeherbron;

import java.net.Socket;

public class ClientInfo {

    private Socket sock;
    private String name;

    public ClientInfo(Socket sock){
        this.sock = sock;
    }


    public Socket getSock() {
        return sock;
    }

    public void setSock(Socket sock) {
        this.sock = sock;
    }

    public String getName(){return name;}

    public void setName(String name){this.name = name;}
}
