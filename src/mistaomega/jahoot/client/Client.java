package mistaomega.jahoot.client;

import mistaomega.jahoot.gui.ClientConnectUI;

import java.io.*;
import java.net.Socket;

public class Client {
    private final String hostname;
    private final int port;
    private String Username;
    private ClientConnectUI clientConnectUI;

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
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("u" + Username);
            out.flush();


            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);

            System.out.println("Server says " + in.readUTF());
        } catch (IOException e) {
            clientConnectUI.setConsoleOutput("Connection to server failed.");
            e.printStackTrace();
        }
    }

    String getUserName() {
        return this.Username;
    }

    void setUserName(String userName) {
        this.Username = userName;
    }

}
