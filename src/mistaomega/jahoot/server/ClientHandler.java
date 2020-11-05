package mistaomega.jahoot.server;

import mistaomega.jahoot.gui.ServerGUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Each client is handled (Server side) through one of these threads
 */
public class ClientHandler implements Runnable {

    private final DataInputStream in;
    private final Socket socket;
    private final JahootServer jahootServer;
    private final DataOutputStream out;
    private PrintWriter writer;
    private boolean readyToPlay = false;
    private String username = "";
    private volatile boolean shutdown;



    public ClientHandler(Socket socket, JahootServer jahootServer, DataInputStream in, DataOutputStream out) {
        this.socket = socket;
        this.jahootServer = jahootServer;
        this.in = in;
        this.out = out;
    }


    @Override
    public void run() {
        while(!shutdown) {
            while (!isReadyToPlay()) { // waiting here!
                requestChecker();
            }
        }
    }

    public void requestChecker() {
        try {
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
        } catch (IOException e) {
            jahootServer.removeUser(username, this);
            System.out.println("connection closed");
            shutdown();
        }
    }

    public void shutdown(){
        shutdown = true;
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    public boolean isReadyToPlay() {
        return readyToPlay;
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
