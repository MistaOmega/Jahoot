package mistaomega.jahoot.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Each client is handled (Server side) through one of these threads
 */
public class ClientHandler implements Runnable {
    private final ArrayList<Question> questions;
    private final DataInputStream in;
    private final Socket socket;
    private final JahootServer jahootServer;
    private final DataOutputStream out;
    private final ObjectOutputStream objectOut;
    volatile boolean shutdown = false;
    private boolean readyToPlay = false;
    private String username = "";
    private boolean questionResponded;


    public ClientHandler(Socket socket, JahootServer jahootServer, DataInputStream in, DataOutputStream out, ObjectOutputStream objectOut, ArrayList<Question> questions) {
        this.questions = questions;
        this.socket = socket;
        this.jahootServer = jahootServer;
        this.in = in;
        this.out = out;
        this.objectOut = objectOut;
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


                for (Question question :
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

                    //leaderboards
                    Map<ClientHandler, Integer> clientScores = jahootServer.getClientScores();
                    Map<String, Integer> stringIntegerMap = convertClientHandlerMapToStringMap(clientScores);


                    objectOut.writeObject(stringIntegerMap);
                    if (question == questions.get(questions.size() - 1)) { // if last question
                        out.writeBoolean(true);

                        if (jahootServer.isPlaying()) {
                            jahootServer.setPlaying(false);
                            jahootServer.restart();
                            shutdown();
                        }
                    } else {
                        out.writeBoolean(false);
                    }
                }

                //Game ended here

            } catch (IOException e) {
                jahootServer.removeUser(username, this);
                System.out.println("connection closed");

            } finally {
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
        }

        if (received.charAt(0) == 'g') {
            System.out.println("ready check");
            if (isReadyToPlay()) {
                try {
                    Thread.sleep(5000); // Server is on a 5 second timeout, this will ensure no extra connections, and if so their client handler can catch
                    out.writeBoolean(isReadyToPlay());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                out.writeBoolean(isReadyToPlay());
            }

            out.flush();
        }


    }

    public boolean isReadyToPlay() {
        return readyToPlay;
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    public String getUsername() {
        return username;
    }

    public synchronized void shutdown() {
        shutdown = true;
        Thread.currentThread().interrupt();
    }


    public boolean isQuestionResponded() {
        return questionResponded;
    }

    public Map<String, Integer> convertClientHandlerMapToStringMap(Map<ClientHandler, Integer> clients) {
        Map<String, Integer> StringScores = new HashMap<>();
        for (ClientHandler client :
                clients.keySet()) {
            StringScores.put(client.getUsername(), clients.get(client));
        }

        return StringScores;
    }
}
