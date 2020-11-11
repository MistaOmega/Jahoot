package mistaomega.jahoot.server;

import java.io.Serializable;

public class Question implements Serializable { //TODO Implement this
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

    public void setCorrect(char correct) {
        Correct = correct;
    }

    public String getQuestionName() {
        return QuestionName;
    }

    public void setQuestionName(String questionName) {
        QuestionName = questionName;
    }

    public String[] getQuestionChoices() {
        return QuestionChoices;
    }

    public void setQuestionChoices(String[] questionChoices) {
        QuestionChoices = questionChoices;
    }
}
