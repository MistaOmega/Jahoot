package mistaomega.jahoot.client;

import mistaomega.jahoot.gui.ClientConnectUI;
import mistaomega.jahoot.gui.ClientMainUI;
import mistaomega.jahoot.server.Question;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client {
    private final String hostname;
    private final int port;
    private final String Username;
    private final ClientConnectUI clientConnectUI;
    ObjectOutputStream objectOut;
    ObjectInputStream objectIn;
    DataOutputStream out;
    DataInputStream in;
    private volatile boolean GameStarted = false;
    private volatile boolean questionAnswered;
    private volatile int givenAnswerIndex;

    public Client(String hostname, int port, String Username, ClientConnectUI clientConnectUI) {
        this.hostname = hostname;
        this.port = port;
        this.Username = Username;
        this.clientConnectUI = clientConnectUI;
    }

    public void run() {
        try {

            System.out.println("Connecting to " + hostname + " on port " + port);
            Socket client = new Socket(hostname, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            InputStream inFromServer = client.getInputStream();
            OutputStream outToServer = client.getOutputStream();
            objectOut = new ObjectOutputStream(outToServer);
            objectIn = new ObjectInputStream(inFromServer);
            out = new DataOutputStream(outToServer);
            in = new DataInputStream(inFromServer);
            System.out.println("Server says " + in.readUTF());

            // send username
            out.writeUTF("u" + Username);
            out.flush();

            new Thread(() -> { // connection check thread
                while (!GameStarted) {
                    try {
                        out.writeUTF("g");
                        out.flush();
                        if (in.readBoolean()) {
                            GameStarted = true;
                            System.out.println("ready to play");
                            ClientMainUI clientMainUI = new ClientMainUI(this);
                            clientMainUI.start();
                            playGame(clientMainUI);
                            break;

                        } else {
                            clientConnectUI.clearConsole();
                            clientConnectUI.setConsoleOutput("Waiting");
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


        } catch (IOException e) {
            clientConnectUI.setConsoleOutput("Connection to server failed.");
            e.printStackTrace();
        }
    }

    public void playGame(@NotNull ClientMainUI clientMainUI) throws IOException, ClassNotFoundException {
        Thread.onSpinWait();

        Question question = (Question) objectIn.readObject();
        System.out.println(question.getQuestionName());

        clientMainUI.addQuestion(question.getQuestionName()); //TODO not working for some reason

        List<String> answers = Arrays.asList(question.getQuestionChoices());
        String correct = answers.get(question.getCorrect());
        Collections.shuffle(answers);
        // send here
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 30000;

            public void run() {
                i -= 1;
                if (questionAnswered) {
                    timer.cancel();
                    System.out.println("Entry 1");
                    try {
                        checkAnswer(i, answers, correct);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (i < 0) {
                    timer.cancel();
                    try {
                        checkAnswer(i, answers, correct);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, 0, 1000);

    }

    public void answerQuestion(int index) {
        givenAnswerIndex = index;
        questionAnswered = true;
    }

    public void checkAnswer(int timeLeft, List<String> answers, String correctAnswer) throws IOException {
        int total;
        if (!questionAnswered) {
            out.writeInt(0);
        }
        else {

            total = correctAnswer.equals(answers.get(givenAnswerIndex)) ? 1000 / (timeLeft / 1000) :
                    100 / (timeLeft / 1000);
            out.writeInt(total);
        }

    }

    @Override
    public String toString() {
        return Username + Thread.currentThread().getId();
    }
}
