package mistaomega.jahoot.client;

import mistaomega.jahoot.gui.ClientConnectUI;
import mistaomega.jahoot.gui.ClientMainUI;
import mistaomega.jahoot.server.Question;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                    } catch (IOException | ClassNotFoundException | InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


        } catch (IOException e) {
            clientConnectUI.setConsoleOutput("Connection to server failed.");
            e.printStackTrace();
        }
    }

    public void playGame(ClientMainUI clientMainUI) throws IOException, ClassNotFoundException, InterruptedException {
        Thread.onSpinWait();

        Question question = (Question) objectIn.readObject();
        System.out.println(question.getQuestionName());

        clientMainUI.addQuestion(question.getQuestionName()); //TODO not working for some reason

        List<String> answers = Arrays.asList(question.getQuestionChoices());
        String correct = answers.get(question.getCorrect());
        Collections.shuffle(answers);
        // send here


        //receive here
        while(!questionAnswered){
            //if timer is finished{
            // out.writeUTF("noresponse");
            //}
        }

        if(correct.equals(answers.get(givenAnswerIndex))){
            out.writeUTF("A correct");
        }
        else{
            out.writeUTF("A incorrect");
        }

        Thread.sleep(3000); // timer starts client side

    }

    public void answerQuestion(int index){
        givenAnswerIndex = index;
        questionAnswered = true;
    }

}
