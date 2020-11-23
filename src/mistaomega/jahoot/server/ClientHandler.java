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
 * Each client is handled (Server side) through an instance of this class
 *
 * @author Jack Nash
 * @version 1.0
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
    private boolean FinishedPlaying;


    /**
     * default constructor
     * @param socket client side socket
     * @param jahootServer reference to the main server
     * @param in input stream for data
     * @param out output stream for data
     * @param objectOut output stream for handling serializable objects
     * @param questions the arraylist of questions that will be iterated through
     */
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
                while (!requestChecker()) { // waiting here!
                    if (shutdown) {
                        throw new IOException(); // force the client handler to quit
                    }
                }


                for (Question question :
                        questions) {
                    System.out.println(this + " Outputting question bank to client");
                    objectOut.writeObject(question);
                    out.flush();
                    questionResponded = false;
                    System.out.println("here");

                    int playerTotal = in.readInt(); // client answered at this point.
                    questionResponded = true;
                    jahootServer.setNewClientScore(this, jahootServer.getClientScore(this) + playerTotal);
                    System.out.println(jahootServer.getClientScore(this));

                    //Wait for other clients to get on with answering, currently blank, will have to change to a notify system at some point
                    while (!jahootServer.AllClientsResponded()) {
                        System.out.println("Waiting");
                    }
                    //leaderboards
                    Map<ClientHandler, Integer> clientScores = jahootServer.getClientScores(); // This map needs to be converted to String as the ClientHandler isn't Serializable
                    Map<String, Integer> stringIntegerMap = convertClientHandlerMapToStringMap(clientScores);


                    objectOut.writeObject(stringIntegerMap);
                    if (question == questions.get(questions.size() - 1)) { // if last question
                        out.writeBoolean(true);
                        FinishedPlaying = true;

                        if (!jahootServer.isClientsStillPlaying()) {
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

    public boolean requestChecker() throws IOException {
        String received;
        received = in.readUTF();
        if (received.charAt(0) == 'u') {
            System.out.println("Username attempt");
            username = received.substring(1);
            boolean usernameAccepted = jahootServer.checkAndAddUser(received.substring(1));
            out.writeBoolean(usernameAccepted);
        }

        if (received.charAt(0) == 'g') {
            if (isReadyToPlay()) {
                try {
                    out.writeBoolean(true);
                    out.flush();
                    Thread.sleep(5000); // Server is on a 5 second timeout, this will ensure if someone else connects, they can also play
                    out.writeBoolean(true);
                    out.flush();
                    return true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                out.writeBoolean(false);
                out.flush();
            }
        }


        return false;
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

    public boolean isFinishedPlaying() {
        return FinishedPlaying;
    }

    public boolean isQuestionResponded() {
        return questionResponded;
    }

    public synchronized Map<String, Integer> convertClientHandlerMapToStringMap(Map<ClientHandler, Integer> clients) {
        Map<String, Integer> StringScores = new HashMap<>();
        for (ClientHandler client :
                clients.keySet()) {
            StringScores.put(client.getUsername(), clients.get(client));
        }

        return StringScores;
    }

    @Override
    public String toString() {
        return username;
    }
}
