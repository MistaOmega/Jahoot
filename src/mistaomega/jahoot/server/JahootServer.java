package mistaomega.jahoot.server;

import mistaomega.jahoot.gui.ServerGUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class was written with the help of the following resource:
 * http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html
 * Code was not used in verbatim, more used to help guide me in the right direction
 * <p>
 * This code is for the primary server, this server is responsible for handling connection requests
 * and for storing instances of each ClientHandler for later use.
 * It, as the central system is also responsible for broadcasting changes in state, such as the game starting
 *
 * @author Jack Nash
 * @version 1.0
 */
public class JahootServer implements IJahootServer {
    //region class data
    private final int port;
    private final Set<String> Usernames = new HashSet<>(); // hashset of all usernames
    private final Set<ClientHandler> Clients = new HashSet<>(); // hashset of all clients
    private final Map<ClientHandler, Integer> ClientScores = new HashMap<>();
    private final ExecutorService ThreadPool =
            Executors.newFixedThreadPool(10);
    private final ServerGUI serverGUI;
    private final ArrayList<Question> questions;
    private boolean isAcceptingConnections = true;
    private ServerSocket serverSocket = null;
    //endregion

    //region constructor(s)
    public JahootServer(int port, ServerGUI serverGUI, ArrayList<Question> questions) {
        this.port = port;
        this.serverGUI = serverGUI;
        this.questions = questions;
    }
    //endregion

    //region Overwritten methods

    /**
     * Entry function
     * This function is reponsible for:
     * Listening for connection attempts
     * Making and storing instances of client handlers
     */
    @Override
    public void run() {

        serverSocket = openServerConnections(port);
        isAcceptingConnections = true;
        System.out.println("Server listening on port " + port);
        Socket socket;
        while (isAcceptingConnections) {
            try {
                socket = serverSocket.accept();
                System.out.println("Just connected to " + socket.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                out.writeUTF("Thank you for connecting to " + socket.getLocalSocketAddress());
                out.flush();

                ClientHandler newUser = new ClientHandler(socket, this, in, out, objectOut, questions);
                Clients.add(newUser);
                serverGUI.addToUsers(newUser);
                ThreadPool.execute(newUser);

            } catch (IOException e) {
                if (!isAcceptingConnections) {
                    System.out.println("Server no longer accepting connection.");
                    setupUsersForGame();
                    return;
                }
            }

        }

    }

    /**
     * Restarts the server
     */
    @Override
    public synchronized void restart() {
        try {
            this.serverSocket.close();
            serverGUI.clearAllClients();
            Clients.clear();
            ClientScores.clear();
            Usernames.clear();
            run();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    /**
     * This sets all player scores to 0 before playing
     */
    @Override
    public void setupUsersForGame() {
        for (ClientHandler client :
                Clients) {
            ClientScores.put(client, 0);
        }
    }

    /**
     * Add username to the usernames list
     *
     * @param Username username to add
     */
    @Override
    public boolean checkAndAddUser(String Username) {
        if (Usernames.contains(Username)) {
            return false;
        }
        Usernames.add(Username);
        return true;
    }

    /**
     * Removes a user
     *
     * @param Username Username to remove
     * @param client   ClientHandler instance to remove
     */
    @Override
    public void removeUser(String Username, ClientHandler client) {
        client.shutdown();
        boolean removed = Usernames.remove(Username);
        if (removed) {
            Clients.remove(client);
            System.out.println("The user " + Username + " quit");
        }
    }

    //endregion

    //region Getters Setters and Booleans return types
    public void setReadyToPlay(boolean readyToPlay) {
        isAcceptingConnections = false;
        setupUsersForGame();
        for (ClientHandler client : Clients) {
            client.setReadyToPlay(readyToPlay);
        }

    }

    /**
     * @param clientHandler ClientHandler to get the score from
     * @return score linked to ClientHandler
     */
    public int getClientScore(ClientHandler clientHandler) {
        return ClientScores.get(clientHandler);
    }

    /**
     * Sets new score for the client
     *
     * @param clientHandler ClientHandler instance to change score of
     * @param newTotal      New score
     */
    public void setNewClientScore(ClientHandler clientHandler, int newTotal) {
        ClientScores.remove(clientHandler);
        ClientScores.put(clientHandler, newTotal);
    }

    /**
     * Iterates through each ClientHandler to see if the question has been responded to
     *
     * @return true if all clients have responded
     */
    public boolean AllClientsResponded() {
        for (ClientHandler client :
                Clients) {
            if (!client.isQuestionResponded()) {
                System.out.println(client + " Not responded yet");
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the map of clients and their scores
     *
     * @return Map of clients and scores
     */
    public Map<ClientHandler, Integer> getClientScores() {
        return ClientScores;
    }

    /**
     * Iterates through each ClientHandler to see if all ClientHandlers are finished with the current game
     *
     * @return True if game is finished
     */
    public boolean isClientsStillPlaying() {
        for (ClientHandler client : Clients) {
            if (!client.isFinishedPlaying()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts a map of ClientHandlers and Integers to a list of Strings and Integers so that it can be transmitted over a socket.
     *
     * @param clients ClientHandler - Integer map
     * @return String - Integer map
     */
    public synchronized Map<String, Integer> convertClientHandlerMapToStringMap(Map<ClientHandler, Integer> clients) {
        Map<String, Integer> StringScores = new HashMap<>();
        for (ClientHandler client :
                clients.keySet()) {
            StringScores.put(client.getUsername(), clients.get(client));
        }

        return StringScores;
    }

    //endregion
}
