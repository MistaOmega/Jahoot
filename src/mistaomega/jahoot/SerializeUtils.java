package mistaomega.jahoot;

import mistaomega.jahoot.server.Question;

import java.io.*;
import java.util.ArrayList;

public class SerializeUtils  {

    public static void SerializeQuestion(ArrayList<Question> questions, String filename) {
        try {
            FileOutputStream fos = new FileOutputStream(filename);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(questions);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    public static ArrayList<Question> DeserializeQuestion(File file) {
        try {
            //Creating stream to read the object
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            ArrayList<Question> questions = (ArrayList<Question>) in.readObject();
            in.close();
            return questions;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
