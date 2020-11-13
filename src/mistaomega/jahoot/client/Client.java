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
            while (!GameStarted) {
                out.writeUTF("g");
                out.flush();
                boolean isReady;
                isReady = in.readBoolean();
                if (isReady) {
                    GameStarted = true;
                    System.out.println("ready to play");
                    ClientMainUI clientMainUI = new ClientMainUI(this);
                    clientMainUI.run(objectIn);

                    break;

                }
                else {
                    clientConnectUI.clearConsole();
                    clientConnectUI.setConsoleOutput("Waiting");
                }
                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException e) {
            clientConnectUI.setConsoleOutput("Connection to server failed.");
            clientConnectUI.getBtnConnect().setEnabled(true);
            e.printStackTrace();
        }
    }

    public void checkAnswer(int timeLeft, List<String> answers, String correctAnswer) throws IOException {
        int total;
        if (!questionAnswered) {
            out.writeInt(0);
        } else {

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
