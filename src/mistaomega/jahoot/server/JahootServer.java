package mistaomega.jahoot.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JahootServer {
    private final int port;
    private final Set<String> Usernames = new HashSet<>(); // hashset of all usernames
    private final Set<ClientHandler> Clients = new HashSet<>(); // hashset of all clients
    protected Thread RunningThread = null;
    private boolean isAcceptingConnections = true;
    private Socket socket;
    private ServerSocket serverSocket = null;
    private List<Question> Questions;
    private final ExecutorService Threadpool =
            Executors.newFixedThreadPool(10);

    public JahootServer(int port) {
        this.port = port;
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
                out.writeUTF("Thank you for connecting to " + socket.getLocalSocketAddress());
                out.flush();

                ClientHandler newUser = new ClientHandler(socket, this, in, out);
                Clients.add(newUser);
                this.Threadpool.execute(
                        new ClientHandler(socket, this, in, out));

            } catch (IOException e) {
                if (!isAcceptingConnections) {
                    System.out.println("Server no longer accepting connection.");
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
        if(Usernames.contains(Username)){
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

    public Set<String> getUsernames() {
        return Usernames;
    }

    public boolean isOtherUserConnected() {
        return !this.Usernames.isEmpty();
    }
}
