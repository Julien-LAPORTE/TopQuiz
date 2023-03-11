package fr.samneo.android.model;


import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank implements Serializable {
    private static final String SEPARATEUR = "/@/";

    private List<Question> m_questionList;


    private int m_questionIndex;
    private boolean m_endQuestionList = false;

    public QuestionBank(List<Question> questionList) {
        m_questionList = questionList;
        Collections.shuffle(m_questionList);
        //m_questionIndex = 0;
    }

    public Question getCurrentQuestion() {
        return m_questionList.get(m_questionIndex);
    }

    public Question getNextQuestion() {
        if (!m_endQuestionList) {
            if (m_questionIndex < m_questionList.size() - 2) {
                m_questionIndex++;
            } else {
                m_questionIndex++;
                m_endQuestionList = true;
            }
        } else {
            throw new IllegalStateException("end of the question list : ");
        }
        return getCurrentQuestion();
    }

    public boolean isEndQuestionList() {
        return m_endQuestionList;
    }

    public String getAllQuestionsForBundle() {
        int numberOfQuestion = m_questionList.size();
        String questionsBundle = new String();//Contient une chaine String avec toutes les infos des questions en vu d'un restauration

        for (int i = 0; i < numberOfQuestion; i++) {
            questionsBundle += m_questionList.get(i).getQuestion() + SEPARATEUR;//Ajout de la question

            for (String choiceList : m_questionList.get(i).getChoiceList()) {//Ajout des choix de réponses
                questionsBundle += choiceList + SEPARATEUR;
            }
            questionsBundle += m_questionList.get(i).getAnswerIndex() + SEPARATEUR;//Ajout de l'index de la bonne réponse
        }
        return questionsBundle;
    }

    public static final String[] putAllQuestionsFromBundle(String questionsBundle) {
        String[] listQuestions = String.valueOf(questionsBundle).split(SEPARATEUR);
        return listQuestions;
    }
}
