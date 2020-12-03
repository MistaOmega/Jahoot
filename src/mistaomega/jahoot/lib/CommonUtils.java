package mistaomega.jahoot.lib;

import mistaomega.jahoot.server.Question;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class responsible for common static utilities that all classes can pull from
 *
 * @author Jack Nash
 * @since 1.0
 */
public class CommonUtils {


    /**
     * Serializes given question array to file
     *  @param questions Array of questions
     * @param path  Given path
     */
    public static void SerializeQuestion(ArrayList<Question> questions, File path) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);
            objectOutputStream.writeObject(questions);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes question array from file
     *
     * @param file file to deserialize
     * @return ArrayList of Questions gathered from file
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Question> DeserializeQuestion(File file) {
        try {
            //Creating stream to read the object
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            ArrayList<Question> questions = (ArrayList<Question>) objectInputStream.readObject();
            objectInputStream.close();
            return questions;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns list of strings from value
     * generified for any object type
     *
     * @param hashMap map to get keys from
     * @param input   value associated with key
     * @return list of key - value pairs
     */
    public static List<Object> findKeyFromValue(Map<?, ?> hashMap, Object input) {
        List<Object> keys = new ArrayList<>();
        for (Object mapKey : hashMap.keySet()) {
            if (hashMap.get(mapKey).equals(input)) {
                keys.add(mapKey);
            }
        }
        return keys;
    }

    public static File[] getQuestionBanks(String directory) {
        File f = new File(directory);
        FilenameFilter textFilter = (dir, name) -> name.toLowerCase().endsWith(".qbk");

        return f.listFiles(textFilter);
    }
}
