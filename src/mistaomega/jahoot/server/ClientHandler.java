package mistaomega.jahoot.server;

import java.io.*;
import java.net.Socket;

/**
 * Each client is handled (Server side) through one of these threads
 */
public class ClientHandler implements Runnable {

    private final DataInputStream in;
    private final Socket socket;
    private final JahootServer jahootServer;
    private final DataOutputStream out;
    private final ObjectOutputStream objectOut;
    private final ObjectInputStream objectIn;
    private PrintWriter writer;
    private boolean readyToPlay = false;
    private String username = "";
    private volatile boolean shutdown;


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
        try {
            while (!isReadyToPlay()) { // waiting here!
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                requestChecker();
            }

            Question q = new Question("ketamine", new String[]{"Hello", "Goodbye", "You're a whore", "ded"}, 'A');
            objectOut.writeObject(q);
            out.flush();
            System.out.println("here");
            requestChecker();
        } catch (IOException e) {
            jahootServer.removeUser(username, this);
            System.out.println("connection closed");
            Thread.currentThread().interrupt();
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

        if (received.equals("noresponse")) {
            System.out.println("You're a failure.");
        }

    }

    public void shutdown() {
        shutdown = true;
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
}
