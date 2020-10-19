package mistaomega.jahoot.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class JahootServer {
    private final int port;
    private Set<String> Usernames = new HashSet<>(); // hashset of all usernames
    private Set<ClientHandler> Clients = new HashSet<>(); // hashset of all clients

    public JahootServer(int port){
        this.port = port;
    }

    public void run(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("Server listening on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
