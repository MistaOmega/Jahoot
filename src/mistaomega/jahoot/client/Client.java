package mistaomega.jahoot.client;

import mistaomega.jahoot.gui.ClientConnectUI;
import mistaomega.jahoot.gui.ClientMainUI;
import mistaomega.jahoot.gui.Leaderboard;
import mistaomega.jahoot.server.Question;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client {
    private final String hostname;
    private final int port;
    private final String Username;
    private final ClientConnectUI clientConnectUI;
    private final Leaderboard leaderboard;
    private ClientMainUI clientMainUI;
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
            Socket client = new Socket(hostname, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            InputStream inFromServer = client.getInputStream();
            OutputStream outToServer = client.getOutputStream();
            objectIn = new ObjectInputStream(inFromServer);
            out = new DataOutputStream(outToServer);
            in = new DataInputStream(inFromServer);
            System.out.println("Server says " + in.readUTF());

            // send username
            out.writeUTF("u" + Username);
            out.flush();

            while (!GameStarted) {
                out.writeUTF("g");
                out.flush();
                boolean isReady;
                isReady = in.readBoolean();
                if (isReady) {
                    GameStarted = true;
                    System.out.println("ready to play");
                    clientMainUI = new ClientMainUI(this);
                    clientMainUI.run();
                    break;

                } else {
                    clientConnectUI.clearConsole();
                    clientConnectUI.setConsoleOutput("Waiting");
                }
            }

            assert clientMainUI != null;
            playGame();
        } catch (IOException e) {
            clientConnectUI.setConsoleOutput("Connection to server failed.");
            clientConnectUI.getBtnConnect().setEnabled(true);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void checkAnswer(int timeLeft, List<String> answers, String correctAnswer) {
        System.out.println(timeLeft);
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
                System.out.println("Total: " + total);
                out.writeInt(total);
            }

            Map<String, Integer> clientScores = (Map<String, Integer>) objectIn.readObject(); // unchecked cast present here; will be find as only object sent by client handler at this point is a map.

            if (in.readBoolean()) {
                leaderboard.displayLatestScores(clientScores, true);
                leaderboard.show();
                Thread.sleep(5000);
                leaderboard.hide();
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
        }

    }

    public void answerQuestion(int givenAnswerIndex) {
        this.givenAnswerIndex = givenAnswerIndex;
        questionAnswered = true;
    }

    @Override
    public String toString() {
        return Username + Thread.currentThread().getId();
    }
}
