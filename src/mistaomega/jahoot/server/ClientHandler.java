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

    private ServerGUI serverGUI;


    public ClientHandler(Socket socket, JahootServer jahootServer, DataInputStream in, DataOutputStream out, ServerGUI serverGUI) {
        this.socket = socket;
        this.jahootServer = jahootServer;
        this.in = in;
        this.out = out;
        this.serverGUI = serverGUI;
    }


    @Override
    public void run() {
        try {
            out.writeBoolean(false);
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (readyToPlay) { // game is running
            try {
                requestChecker();
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                jahootServer.removeUser(username, this);
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

        if(received.charAt(0) == 'g'){
            System.out.println("ready check");
            readyToPlay = serverGUI.isReadytoplay();
            out.writeBoolean(readyToPlay);
            out.flush();
        }
    }


    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    void printUsers() {
        if (jahootServer.isOtherUserConnected()) {
            writer.println("Connected users: " + jahootServer.getUsernames());
        } else {
            writer.println("No other users connected");
        }
    }

    void printQuestion(String Question) {
        writer.println(Question);

    }

    void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
