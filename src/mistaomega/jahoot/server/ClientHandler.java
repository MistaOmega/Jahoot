package mistaomega.jahoot.server;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Each client is handled (Server side) through one of these threads
 */
public class ClientHandler implements Runnable {
    private final ArrayList<Question> questions;
    private DataInputStream in;
    private final Socket socket;
    private final JahootServer jahootServer;
    private DataOutputStream out;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;
    private PrintWriter writer;
    private boolean readyToPlay = false;
    private String username = "";
    private boolean questionResponded;
    volatile boolean shutdown = false;


    public ClientHandler(Socket socket, JahootServer jahootServer, DataInputStream in, DataOutputStream out, ObjectInputStream objectIn, ObjectOutputStream objectOut, ArrayList<Question> questions) {
        this.questions = questions;
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

                for (Question question:
                     questions) {

                    objectOut.writeObject(question);
                    out.flush();
                    questionResponded = false;
                    System.out.println("here");

                    int playerTotal = in.readInt(); // client answered at this point.
                    questionResponded = true;
                    jahootServer.setNewClientScore(this, jahootServer.getClientScore(this) + playerTotal);
                    System.out.println(jahootServer.getClientScore(this));

                    //Wait for other clients to get on with answering
                    while (!jahootServer.AllClientsResponded()) {
                        System.out.println("waiting for other clients");
                    }
                }

                //Game ended here

            } catch (IOException e) {
                jahootServer.removeUser(username, this);
                System.out.println("connection closed");

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

    public String getUsername() {
        return username;
    }

    public synchronized void shutdown(){
        shutdown = true;
        Thread.currentThread().interrupt();
    }


    public boolean isQuestionResponded() {
        return questionResponded;
    }
}
