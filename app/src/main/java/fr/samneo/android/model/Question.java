package fr.samneo.android.model;

import android.os.Bundle;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.List;

import icepick.Bundler;

@Parcel
public class Question implements Bundler<Question> {
    private String m_question;
    private List<String> m_choiceList;
    private int m_answerIndex;

    public Question() {
    }

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

    @Override
    public void put(String s, Question question, Bundle bundle) {
        bundle.putParcelable(s, Parcels.wrap(question));
    }

    @Override
    public Question get(String s, Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(s));
    }
}
