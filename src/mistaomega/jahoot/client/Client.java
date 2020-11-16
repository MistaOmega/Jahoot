package mistaomega.jahoot.client;

import mistaomega.jahoot.gui.ClientConnectUI;
import mistaomega.jahoot.gui.ClientMainUI;
import mistaomega.jahoot.server.Question;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Client {
    private final String hostname;
    private final int port;
    private final String Username;
    private final ClientConnectUI clientConnectUI;
    private ClientMainUI clientMainUI;
    private ObjectOutputStream objectOut;
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

                }
                else {
                    clientConnectUI.clearConsole();
                    clientConnectUI.setConsoleOutput("Waiting");
                }
                Thread.sleep(500);
            }

            assert clientMainUI != null;
            playGame();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            clientConnectUI.setConsoleOutput("Connection to server failed.");
            clientConnectUI.getBtnConnect().setEnabled(true);
            e.printStackTrace();
        }
    }

    public void checkAnswer(int timeLeft, List<String> answers, String correctAnswer) {
        try {
            int total = 0;
            if (!questionAnswered) {
                out.writeInt(0);
            } else {
                total += correctAnswer.equals(answers.get(givenAnswerIndex)) ? 1000 / timeLeft : 100 / timeLeft;
                out.writeInt(total);
            }
            playGame();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }

    public void playGame() throws IOException, ClassNotFoundException {
        questionAnswered = false;
        Question question = (Question) objectIn.readObject();
        List<String> answers = Arrays.asList(question.getQuestionChoices());
        Collections.shuffle(answers);
        String correct = answers.get(question.getCorrect());
        clientMainUI.addQuestion(question.getQuestionName(), answers);

        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 30000;
            @Override
            public void run() {
                i-=1;
                if (questionAnswered) { // If statement triggered if question is answered before the timer runs out
                    timer.cancel();
                    System.out.println("Entry 1");
                    checkAnswer(i, answers, correct);
                }
                if (i < 0) { // triggered if the timer runs out
                    timer.cancel();
                    checkAnswer(0, answers, correct);
                }
            }
        }, 0, 1000);

    }

    public void answerQuestion(int givenAnswerIndex){
        this.givenAnswerIndex = givenAnswerIndex;
        questionAnswered = true;
    }

    @Override
    public String toString() {
        return Username + Thread.currentThread().getId();
    }
}
