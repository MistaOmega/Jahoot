package mistaomega.jahoot.server;

import java.io.Serializable;

/**
 * Serializable question object which stores all question information
 *
 * @author Jack Nash
 * @version 1.0
 */
public class Question implements Serializable {
    private static final long serialVersionUID = 123456789L;
    String QuestionName;
    String[] QuestionChoices;
    int Correct;

    /**
     * Constructor
     * @param questionName Name of the question
     * @param questionChoices Possible choices for the question
     * @param correct Index of the correct answer
     */
    public Question(String questionName, String[] questionChoices, int correct) {
        this.QuestionName = questionName;
        this.QuestionChoices = questionChoices;
        this.Correct = correct;
    }

    public int getCorrect() {
        return Correct;
    }

    public String getQuestionName() {
        return QuestionName;
    }

    public String[] getQuestionChoices() {
        return QuestionChoices;
    }


    @Override
    public String toString() {
        return this.QuestionName;
    }
}
