package mistaomega.jahoot.client;

import mistaomega.jahoot.gui.ClientConnectUI;
import mistaomega.jahoot.gui.ClientMainUI;

import java.io.*;
import java.net.Socket;

public class Client {
    private final String hostname;
    private final int port;
    private final String Username;
    private final ClientConnectUI clientConnectUI;
    private boolean waiting = true;

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
            DataOutputStream out = new DataOutputStream(outToServer);
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Server says " + in.readUTF());
            out.writeUTF("u" + Username);
            out.flush();

            while(waiting){
                out.writeUTF("g");
                if(in.readBoolean()){
                    System.out.println("waiting");
//                    clientConnectUI.getMainPanel().setVisible(false);
//                    ClientMainUI clientMainUI = new ClientMainUI();
//                    clientMainUI.start();
                }
                else{
                    clientConnectUI.setConsoleOutput("Waiting for game start");
                }
            }
            System.out.println("We fucking did it");


        } catch (IOException e) {
            clientConnectUI.setConsoleOutput("Connection to server failed.");
            e.printStackTrace();
        }
    }

}
