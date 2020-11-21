package mistaomega.jahoot.client;

import mistaomega.jahoot.gui.ClientConnectUI;
import mistaomega.jahoot.gui.ClientMainUI;
import mistaomega.jahoot.gui.Leaderboard;
import mistaomega.jahoot.server.Question;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.Timer;

public class Client {
    private final String hostname;
    private final int port;
    private String Username;
    private final ClientConnectUI clientConnectUI;
    private final Leaderboard leaderboard;
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

    public void run() {
        try {
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
            while (!GameStarted) { // this while loop will run so long as the server host hasn't selected play game
                out.writeUTF("g");
                out.flush();
                if (in.readBoolean()) {
                    break;
                }
            }
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

    @SuppressWarnings("unchecked")
    public void checkAnswer(int timeLeft, List<String> answers, String correctAnswer) {
        try {
            int total = 0;
            if (!questionAnswered) {
                out.writeInt(0);
            } else {
                if (correctAnswer.equals(answers.get(givenAnswerIndex))) {
                    total += 1000 + (10 * timeLeft); // max 1300
                } else {
                    total += 100 + (timeLeft); // max 130
                }
                out.writeInt(total);
            }

            Map<String, Integer> clientScores = (Map<String, Integer>) objectIn.readObject(); // unchecked cast present here; will be find as only object sent by client handler at this point is a map.

            if (in.readBoolean()) {
                leaderboard.displayLatestScores(clientScores, true);
                leaderboard.show();
                Thread.sleep(5000);
                leaderboard.hide();

                clientConnectUI.show();
                clientConnectUI.setConsoleOutput("Game complete, shutting down client, you may reconnect using the connect UI when ready!");
                clientConnectUI.getBtnConnect().setVisible(true);
                throw new IOException("Game complete, shutting down client, you may reconnect using the connect UI when ready!");
            } else {
                leaderboard.displayLatestScores(clientScores, false);
                leaderboard.show();
                Thread.sleep(5000);
                leaderboard.hide();
                playGame();
            }
        } catch (ClassNotFoundException | IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

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

    public void answerQuestion(int givenAnswerIndex) {
        this.givenAnswerIndex = givenAnswerIndex;
        questionAnswered = true;
    }

    public String processUsername(String username) throws IOException {
        out.writeUTF("u" + username);
        out.flush();

        if(!in.readBoolean()){
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
