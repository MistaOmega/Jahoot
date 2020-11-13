package mistaomega.jahoot.server;

import java.io.*;
import java.net.Socket;

/**
 * Each client is handled (Server side) through one of these threads
 */
public class ClientHandler implements Runnable {

    private DataInputStream in;
    private final Socket socket;
    private final JahootServer jahootServer;
    private DataOutputStream out;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;
    private PrintWriter writer;
    private boolean readyToPlay = false;
    private String username = "";
    volatile boolean shutdown = false;


    public ClientHandler(Socket socket, JahootServer jahootServer, DataInputStream in, DataOutputStream out, ObjectInputStream objectIn, ObjectOutputStream objectOut) {
        this.socket = socket;
        this.jahootServer = jahootServer;
        this.in = in;
        this.out = out;
        this.objectOut = objectOut;
        this.objectIn = objectIn;
    }

    @Override
    public void run() {
        while (!shutdown) {
            try {
                while (!isReadyToPlay()) { // waiting here!
                    if (shutdown) {
                        throw new IOException(); // force the client handler to quit
                    }
                    requestChecker();
                }

                Question q = new Question("What is the meaning of life", new String[]{"1", "2", "3", "42"}, 0);

                //foreach question in questions
                objectOut.writeObject(q);
                out.flush();
                System.out.println("here");


                int playerTotal = in.readInt();
                jahootServer.setNewClientScore(this, jahootServer.getClientScore(this) + playerTotal);
                System.out.println(jahootServer.getClientScore(this));

                //after receiving total

            } catch (IOException e) {
                jahootServer.removeUser(username, this);
                System.out.println("connection closed");
                in = null;
                out = null;
                objectOut = null;
                objectIn = null;

            }
            finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.currentThread().interrupt();
            }
        }
    }

    public void requestChecker() throws IOException {
        String received;
        received = in.readUTF();
        if (received.charAt(0) == 'u') {
            System.out.println("Username attempt");
            username = received.substring(1);
            jahootServer.addUserName(received.substring(1));
            printUsers();
        }

        if (received.charAt(0) == 'g') {
            System.out.println("ready check");
            out.writeBoolean(isReadyToPlay());
            out.flush();
        }


    }

    public boolean isReadyToPlay() {
        return readyToPlay;
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    void printUsers() {
    }

    void printQuestion(String Question) {

    }

    void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public synchronized void shutdown(){
        shutdown = true;
        Thread.currentThread().interrupt();
    }
}
