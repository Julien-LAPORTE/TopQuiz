package fr.samneo.android.model;

import java.util.List;

public class Question {
    private final String m_question;
    private final List<String> m_choiceList;
    private final int m_answerIndex;

    public Question(String question, List<String> choiceList, int answerIndex) {
        m_question = question;
        m_choiceList = choiceList;
        m_answerIndex = answerIndex;
    }

    public String getQuestion() {
        return m_question;
    }

    public List<String> getChoiceList() {
        return m_choiceList;
    }

    public int getAnswerIndex() {
        return m_answerIndex;
    }
}
