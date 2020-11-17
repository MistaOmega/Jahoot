package mistaomega.jahoot.lib;

import mistaomega.jahoot.server.Question;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A class responsible for common static utilities that all classes can pull from
 * @author Jack Nash
 * @since 1.0
 */
public class CommonUtils {


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

    /**
     * Returns list of strings from value
     * generified for any object type
     * @param hashMap map to get keys from
     * @param input value associated with key
     * @return list of key - value pairs
     */
    public static List<Object> findKeyFromValue(Map<?, ?> hashMap, Object input){
        List <Object> keys = new ArrayList<>();
        for(Object mapKey:hashMap.keySet()){
            if(hashMap.get(mapKey).equals(input)) {
                keys.add(mapKey);
            }
        }
        return keys;
    }
}
