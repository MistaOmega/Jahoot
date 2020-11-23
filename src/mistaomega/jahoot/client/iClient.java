package mistaomega.jahoot.client;

import java.util.List;

/**
 * Client Interface, for expansion purposes
 * @author Jack Nash
 * @version 1.0
 */
public interface iClient {
    void run();

    void checkAnswer(int timeLeft, List<String> answers, String correctAnswer);

    void playGame();

    void answerQuestion(int givenAnswerIndex);
}
