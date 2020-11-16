package mistaomega.jahoot.server;

import mistaomega.jahoot.gui.ServerGUI;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
    private final ServerGUI serverGUI;
    protected Thread RunningThread = null;
    private boolean isAcceptingConnections = true;
    private Socket socket;
    private ServerSocket serverSocket = null;
    private final ArrayList<Question> questions;
    private int score;

    public JahootServer(int port, ServerGUI serverGUI, ArrayList<Question> questions) {
        this.port = port;
        this.serverGUI = serverGUI;
        this.questions = questions;
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

                ClientHandler newUser = new ClientHandler(socket, this, in, out, objectIn, objectOut, questions);
                Clients.add(newUser);
                serverGUI.addToUsers(newUser);
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
        try {
            serverSocket.setSoTimeout(0);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        isAcceptingConnections = false;
        setupUsersForGame();
        for (ClientHandler client : Clients) {
            client.setReadyToPlay(readyToPlay);
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

   public void removeUser(String Username, ClientHandler client) {
        client.shutdown();
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

    public int getClientScore(ClientHandler clientHandler) {
        return ClientScores.get(clientHandler);
    }

    public void setNewClientScore(ClientHandler clientHandler, int newTotal) {
        ClientScores.remove(clientHandler);
        ClientScores.put(clientHandler, newTotal);
    }

    public boolean AllClientsResponded(){
        for (ClientHandler client:
             Clients) {
            if(!client.isQuestionResponded()){
                return false;
            }
        }
        return true;
    }

    public Set<String> getUsernames() {
        return Usernames;
    }

    public boolean isOtherUserConnected() {
        return !this.Usernames.isEmpty();
    }
}
