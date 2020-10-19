package mistaomega.jahoot.server;

import java.io.*;
import java.net.Socket;

/**
 * Each client is handled (Server side) through one of these threads
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private final JahootServer jahootServer;
    private PrintWriter writer;
    private boolean readyToPlay = false;


    public ClientHandler(Socket socket, JahootServer jahootServer) {
        this.socket = socket;
        this.jahootServer = jahootServer;
    }

    @Override
    public void run() {
        super.run();

        try{
            while(!readyToPlay) {
                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                OutputStream socketOutputStream = socket.getOutputStream();
                String username = reader.readLine();
                jahootServer.addUserName(username);

                String serverMessage = "New user connected: " + username;
                jahootServer.broadcast(serverMessage, this);
            }

            while(true){
                System.out.println(readyToPlay);
            }


        } catch (IOException e) {
            e.printStackTrace();
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

    void printQuestion(String Question){
        writer.println("Question");

    }

    void sendMessage(String message) {
        writer.println(message);
    }
}
