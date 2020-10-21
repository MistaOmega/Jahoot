package mistaomega.jahoot.server;

import java.io.Serializable;

public class Question implements Serializable { //TODO Implement this
    String QuestionName;
    String[] QuestionChoices;
    char Correct;

    public Question(String questionName, String[]questionChoices, char correct){
        this.QuestionName = questionName;
        this.QuestionChoices = questionChoices;
        this.Correct = correct;
    }

}
