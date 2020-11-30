package mistaomega.jahoot.server;

import java.io.Serializable;

public class Question implements Serializable {
    private static final long serialVersionUID = 123456789L;
    String QuestionName;
    String[] QuestionChoices;
    int Correct;

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
