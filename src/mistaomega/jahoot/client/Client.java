package mistaomega.jahoot.client;

import java.io.*;
import java.net.Socket;

public class Client {
    private final String hostname;
    private final int port;
    private String Username;

    public Client(String hostname, int port, String Username) {
        this.hostname = hostname;
        this.port = port;
        this.Username = Username;
    }

    public static void main(String[] args) {
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];

        Client client = new Client(hostname, port, username);
        client.run();
    }

    public void run() {
        try {

            System.out.println("Connecting to " + hostname + " on port " + port);
            Socket client = new Socket(hostname, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("u"+Username);
            out.flush();

            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);

            System.out.println("Server says " + in.readUTF());
        } catch (IOException e) {
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
