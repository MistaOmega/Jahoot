package mistaomega.jahoot.server;

import java.io.*;
import java.net.Socket;

/**
 * Each client is handled (Server side) through one of these threads
 */
public class ClientHandler extends Thread {

    private DataOutputStream out;
    private DataInputStream in;
    private final Socket socket;
    private final JahootServer jahootServer;
    private PrintWriter writer;
    private boolean readyToPlay = false;


    public ClientHandler(Socket socket, JahootServer jahootServer, DataInputStream in, DataOutputStream out) {
        this.socket = socket;
        this.jahootServer = jahootServer;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        super.run();
        while(true) {
            try {
                requestChecker();
                Thread.sleep(1000);
            } catch (IOException | InterruptedException ignored) {
            }
        }
    }

    public void requestChecker() throws IOException {
        String received;
        received = in.readUTF();
        if (received.charAt(0) == 'u'){
            System.out.println("Username attempt");
            jahootServer.addUserName(received.substring(1));
            System.out.println(jahootServer.getUsernames());
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
        writer.println("Question");

    }

    void sendMessage(String message) {
        writer.println(message);
    }
}
