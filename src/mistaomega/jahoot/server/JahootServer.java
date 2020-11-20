package mistaomega.jahoot.server;

import mistaomega.jahoot.gui.ServerGUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JahootServer implements IJahootServer{
    //region class data
    private final int port;
    private final Set<String> Usernames = new HashSet<>(); // hashset of all usernames
    private final Set<ClientHandler> Clients = new HashSet<>(); // hashset of all clients
    private final Map<ClientHandler, Integer> ClientScores = new HashMap<>();
    private final ExecutorService ThreadPool =
            Executors.newFixedThreadPool(10);
    private final ServerGUI serverGUI;
    private final ArrayList<Question> questions;
    protected Thread RunningThread = null;
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
    @Override
    public void run() {
        synchronized (this) {
            this.RunningThread = Thread.currentThread();
        }
        serverSocket = openServerConnections(port);
        isAcceptingConnections = true;
        System.out.println("Server listening on port " + port);
        while (isAcceptingConnections) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Just connected to " + socket.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                out.writeUTF("Thank you for connecting to " + socket.getLocalSocketAddress());
                out.flush();

                ClientHandler newUser = new ClientHandler(socket, this, in, out, objectOut, questions);
                Clients.add(newUser);
                serverGUI.addToUsers(newUser);
                this.ThreadPool.execute(
                        newUser);

            } catch (IOException e) {
                if (!isAcceptingConnections) {
                    System.out.println("Server no longer accepting connection.");
                    setupUsersForGame();
                    return;
                }
            }

        }

    }

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

    public int getClientScore(ClientHandler clientHandler) {
        return ClientScores.get(clientHandler);
    }

    public void setNewClientScore(ClientHandler clientHandler, int newTotal) {
        ClientScores.remove(clientHandler);
        ClientScores.put(clientHandler, newTotal);
    }

    public boolean AllClientsResponded() {
        for (ClientHandler client :
                Clients) {
            if (!client.isQuestionResponded()) {
                return false;
            }
        }
        return true;
    }

    public Map<ClientHandler, Integer> getClientScores() {
        return ClientScores;
    }

    public boolean isClientsStillPlaying() {
        for (ClientHandler client : Clients) {
            if (!client.isFinishedPlaying()) {
                return true;
            }
        }
        return false;
    }

    //endregion
}
