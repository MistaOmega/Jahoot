package mistaomega.jahoot.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Interface for the JahootServer class, for expansion purposes
 *
 * @author Jack Nash
 * @version 0.1
 */
public interface IJahootServer {
    default ServerSocket openServerConnections(int port) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(5000);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + port, e);
        }
        return serverSocket;
    }

    void run();

    void restart();

    void setupUsersForGame();

    boolean checkAndAddUser(String username);

    void removeUser(String username, ClientHandler clientHandler);
}
