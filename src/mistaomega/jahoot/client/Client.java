package mistaomega.jahoot.client;

import mistaomega.jahoot.gui.ClientConnectUI;
import mistaomega.jahoot.gui.ClientMainUI;
import mistaomega.jahoot.gui.Leaderboard;
import mistaomega.jahoot.server.Question;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.*;

public class Client implements iClient {
    private final String hostname;
    private final int port;
    private final ClientConnectUI clientConnectUI;
    private final Leaderboard leaderboard;
    private String Username;
    private ClientMainUI clientMainUI;
    private Socket clientSocket;
    private ObjectInputStream objectIn;
    private DataOutputStream out;
    private DataInputStream in;
    private volatile boolean GameStarted = false;
    private volatile boolean questionAnswered;
    private volatile int givenAnswerIndex;

    public Client(String hostname, int port, String Username, ClientConnectUI clientConnectUI) {
        this.hostname = hostname;
        this.port = port;
        this.Username = Username;
        this.clientConnectUI = clientConnectUI;

        leaderboard = new Leaderboard();
        leaderboard.run();
    }

    /**
     * This is the entry function for the client and is reponsible for the following:
     * Connect to the client handler through a socket
     * send client username to server and await response
     * wait for game start to be called
     * begin the game on the client end
     */
    @Override
    public void run() {
        try {
            // setup connection
            System.out.println("Connecting to " + hostname + " on port " + port);
            clientSocket = new Socket(hostname, port);

            System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
            InputStream inFromServer = clientSocket.getInputStream();
            OutputStream outToServer = clientSocket.getOutputStream();
            objectIn = new ObjectInputStream(inFromServer);
            out = new DataOutputStream(outToServer);
            in = new DataInputStream(inFromServer);
            System.out.println("Server says " + in.readUTF());

            // send username
            Username = processUsername(Username); // Using the Username the client sent through from the connect ui first.
            clientConnectUI.setConsoleOutput("Welcome " + Username + " waiting for game to start");

            //wait for game start
            while (!GameStarted) { // this while loop will run so long as the server host hasn't selected play game
                out.writeUTF("g");
                out.flush();
                if (in.readBoolean()) {
                    break;
                }
            }

            //prepare clientMainUI and run the game
            clientConnectUI.setConsoleOutput("Game will be starting in 5 seconds, please wait for the question to appear");
            Thread.sleep(5000); // keep up with ClientHandler
            System.out.println(in.readBoolean());
            clientConnectUI.hide();
            GameStarted = true;
            clientMainUI = new ClientMainUI(this);
            clientMainUI.run();
            assert clientMainUI != null;
            playGame();
        } catch (IOException | InterruptedException e) {
            clientConnectUI.setConsoleOutput("Connection to server failed.");
            clientConnectUI.getBtnConnect().setEnabled(true);
            try {
                clientSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    /**
     * Responsible for verifying and calculating given answers
     *
     * @param timeLeft      how much time was left for the client to answer
     * @param answers       potential answers
     * @param correctAnswer the correct answer
     */
    @SuppressWarnings("unchecked")
    public void checkAnswer(int timeLeft, List<String> answers, String correctAnswer) {
        try {
            int total = 0;
            if (!questionAnswered) {
                out.writeInt(0);
            } else {
                if (correctAnswer.equals(answers.get(givenAnswerIndex))) { // givenAnswerIndex is a class-wide value
                    total += 1000 + (10 * timeLeft); // max 1300
                } else {
                    total += 100 + (timeLeft); // max 130
                }
                out.writeInt(total); // send to clientHandler for processing
            }

            Map<String, Integer> clientScores = (Map<String, Integer>) objectIn.readObject(); // unchecked cast present here; will be find as only object sent by client handler at this point is a map.
            clientMainUI.hide();
            // Run if the game is finished (last question has just been answered)
            if (in.readBoolean()) {
                leaderboard.displayLatestScores(clientScores, true);
                leaderboard.show();
                Thread.sleep(5000);
                leaderboard.hide();

                clientConnectUI.show();
                clientConnectUI.setConsoleOutput("Game complete, shutting down client, you may reconnect using the connect UI when ready!");
                clientConnectUI.getBtnConnect().setVisible(true);
                throw new IOException("Game complete, shutting down client, you may reconnect using the connect UI when ready!");

            } else { // this runs for all other answers
                leaderboard.displayLatestScores(clientScores, false);
                leaderboard.show();
                Thread.sleep(5000);
                leaderboard.hide();
                clientMainUI.show();
                playGame();
            }
        } catch (ClassNotFoundException | IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * This is the function called each time there is a question to respond to, it is responsible for:
     * Gathering the current question
     * Getting information from the given question object such as the answers and correct answer
     * Setting up the timer for the user
     * Waiting until answer is given
     * Sending data to answer checker
     */
    @Override
    public void playGame() {
        try {
            questionAnswered = false;
            Question question = (Question) objectIn.readObject();
            List<String> answers = Arrays.asList(question.getQuestionChoices());
            String correct = answers.get(question.getCorrect());
            Collections.shuffle(answers);
            clientMainUI.addQuestion(question.getQuestionName(), answers);
            Timer timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                int i = 30;

                @Override
                public void run() {
                    if (questionAnswered) { // If statement triggered if question is answered before the timer runs out
                        timer.cancel();
                        System.out.println("Entry 1");
                        checkAnswer(i, answers, correct);
                    }
                    if (i < 0) { // triggered if the timer runs out
                        timer.cancel();
                        checkAnswer(0, answers, correct);
                    }
                    i--;
                    clientMainUI.setTfTimeLeft(String.valueOf(i));
                }
            }, 0, 1000);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error caught");
            e.printStackTrace();
        }

    }


    /**
     * Called by the clientMainUI when a question is answered
     *
     * @param givenAnswerIndex index for the given answer
     */
    @Override
    public void answerQuestion(int givenAnswerIndex) {
        this.givenAnswerIndex = givenAnswerIndex;
        questionAnswered = true;
    }

    /**
     * Handles the verification of a new username
     *
     * @param username username to verify
     * @return username when verified by server
     * @throws IOException Throws when issues with the streams are present
     */
    public String processUsername(String username) throws IOException {
        out.writeUTF("u" + username);
        out.flush();

        if (!in.readBoolean()) {
            String usernameNew = JOptionPane.showInputDialog("Username already exists");
            processUsername(usernameNew);
        }
        return username;
    }


    @Override
    public String toString() {
        return Username + Thread.currentThread().getId();
    }
}
