package mistaomega.jahoot.server;

import mistaomega.jahoot.gui.ServerGUI;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JahootServer {
    private final int port;
    private final Set<String> Usernames = new HashSet<>(); // hashset of all usernames
    private final Set<ClientHandler> Clients = new HashSet<>(); // hashset of all clients
    private final Map<ClientHandler, Integer> ClientScores = new HashMap<>();
    private final ExecutorService Threadpool =
            Executors.newFixedThreadPool(10);
    protected Thread RunningThread = null;
    private boolean isAcceptingConnections = true;
    private Socket socket;
    private ServerSocket serverSocket = null;
    private List<Question> Questions;
    private final ServerGUI serverGUI;
    private int score;

    public JahootServer(int port, ServerGUI serverGUI) {
        this.port = port;
        this.serverGUI = serverGUI;
    }

    public void run() {
        synchronized (this) {
            this.RunningThread = Thread.currentThread();
        }
        openServerConnections();
        System.out.println("Server listening on port " + port);
        while (isAcceptingConnections) {
            try {
                socket = serverSocket.accept();
                System.out.println("Just connected to " + socket.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
                out.writeUTF("Thank you for connecting to " + socket.getLocalSocketAddress());
                out.flush();

                ClientHandler newUser = new ClientHandler(socket, this, in, out, objectIn, objectOut);
                Clients.add(newUser);
                this.Threadpool.execute(
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

    public void setAcceptingConnections(boolean acceptingConnections) {
        isAcceptingConnections = acceptingConnections;
    }

    public synchronized void stop() {
        this.isAcceptingConnections = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerConnections() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            serverSocket.setSoTimeout(5000);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + port, e);
        }
    }

    public void setReadyToPlay(boolean readyToPlay) {
        for (ClientHandler client : Clients) {
            client.setReadyToPlay(readyToPlay);
        }
        isAcceptingConnections = false;
    }

    /**
     * Broadcast message to all clients
     *
     * @param message what to broadcast
     */
    public void broadcast(String message) {
        for (ClientHandler client : Clients) {
            client.sendMessage(message);
        }
    }

    /**
     * Broadcast message to all clients
     *
     * @param message what to broadcast
     * @param exclude this is a ClientHandler that is used to exclude a user
     */
    public void broadcast(String message, ClientHandler exclude) {
        for (ClientHandler client : Clients) {
            if (client != exclude) {
                client.sendMessage(message);
            }
        }
    }

    public void sendQuestion(Question Question) {
        for (ClientHandler clientHandler : Clients) {
            clientHandler.printQuestion(Question.toString());
        }
    }

    /**
     * Add username to the usernames list
     *
     * @param Username username to add
     */
    public void addUserName(String Username) {
        if (Usernames.contains(Username)) {
            Usernames.add(Username);
        }
        Usernames.add(Username);


    }

    void removeUser(String Username, ClientHandler client) {
        boolean removed = Usernames.remove(Username);
        if (removed) {
            Clients.remove(client);
            System.out.println("The user " + Username + " quit");
        }
    }

    public void setupUsersForGame() {
        for (ClientHandler client :
                Clients) {
            ClientScores.put(client, 0);
        }
    }

    public Set<String> getUsernames() {
        return Usernames;
    }

    public boolean isOtherUserConnected() {
        return !this.Usernames.isEmpty();
    }
}
