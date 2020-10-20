package mistaomega.jahoot.client;

import java.io.*;
import java.net.Socket;

public class Client {
    private final String hostname;
    private final int port;
    private String Username;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public static void main(String[] args) {
        if (args.length < 2) return;

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        Client client = new Client(hostname, port);
        client.run();
    }

    public void run() {
        try {

            System.out.println("Connecting to " + hostname + " on port " + port);
            Socket client = new Socket(hostname, port);

            System.out.println("Just connected to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            out.writeUTF("Hello from " + client.getLocalSocketAddress());
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
