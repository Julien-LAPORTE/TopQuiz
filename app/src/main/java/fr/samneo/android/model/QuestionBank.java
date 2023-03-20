package fr.samneo.android.model;


import android.os.Bundle;
import android.util.Log;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import icepick.Bundler;
import icepick.State;

@Parcel
public class QuestionBank implements Serializable, Bundler<QuestionBank> {
    private static final String SEPARATEUR = "/@/";

    @State(Question.class) List<Question> m_questionList;

    @State(User.class) User m_user;
    private int m_questionIndex;
    private boolean m_endQuestionList = false;

    public QuestionBank() {

    }

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

    @Override
    public void put(String s, QuestionBank questionBank, Bundle bundle) {
        bundle.putParcelable(s, Parcels.wrap(questionBank));
    }

    @Override
    public QuestionBank get(String s, Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(s));
    }
}
