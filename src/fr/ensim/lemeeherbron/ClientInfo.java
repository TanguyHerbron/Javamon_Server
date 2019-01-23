package fr.ensim.lemeeherbron;

import java.net.Socket;

public class ClientInfo {

    private Socket sock;

    public ClientInfo(Socket sock){
        this.sock = sock;
    }


    public Socket getSock() {
        return sock;
    }

    public void setSock(Socket sock) {
        this.sock = sock;
    }
}
