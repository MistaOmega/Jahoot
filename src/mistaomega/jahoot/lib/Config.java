package mistaomega.jahoot.lib;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration class for the program.
 * Singleton pattern to allow only 1 instance of the class to exist.
 */
public class Config extends Properties {
    private static Config instance = null;

    private Config() {
    }

    public static Config getInstance() {
        if (instance == null) {
            try {
                instance = new Config();
                FileInputStream in = new FileInputStream("jahoot.properties");
                instance.load(in);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return instance;
    }
}
