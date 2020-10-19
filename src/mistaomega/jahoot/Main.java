package mistaomega.jahoot;

import mistaomega.jahoot.server.JahootServer;

public class Main {

    public static void main(String[] args) {
        JahootServer js = new JahootServer(5000);
        js.run();
    }
}
